package wallpaper.black.live.uhd.BGServices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;

import java.util.Calendar;
import java.util.List;


import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperListModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;


public class AutoChangerService extends WallpaperService {
	private final String TAG="AutoWallChangerService";
	public static boolean isServiceRunning;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public Engine onCreateEngine() {
		return new MyWallpaperEngine();
	}

	public long lastWallpaperChangeTime = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private class MyWallpaperEngine extends Engine{
		private String wallpaperSelectedPaths;
		private Bitmap toBeDraw;
		long frameDuration = 1000;
		boolean isWallpaperChanged;
		private int anInt = 0;
		private Canvas canvas;
		int mHeight =0, mWidth =0;
		private InAppPreference inAppPreference;
		private boolean mVisible = false;
		private final Handler mHandler = new Handler();

		public long getLast_auto_wallpaper_change_time() {
			if (lastWallpaperChangeTime == 0) {
				lastWallpaperChangeTime = InAppPreference.getInstance(getApplicationContext()).getLastAutoChangedTime();
			}
			return lastWallpaperChangeTime;
		}

		private final Runnable mUpdateDisplay = new Runnable() {
			public void run() {
				anInt++;
				reDraw();
			}
		};

		public Bundle onCommand(String action, int x, int y, int z,
								Bundle extras, boolean resultRequested) {
			return extras;
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			inAppPreference = InAppPreference.getInstance(AutoChangerService.this);

			if(!isPreview())
				isServiceRunning=true;
			if (isPreview()) {
				try {
					frameDuration = Constants.WALLPAPER_TIME_MILLI[inAppPreference.getChangeTime()];
				} catch (Exception e) {
					e.printStackTrace();
					frameDuration = Constants.WALLPAPER_TIME_MILLI[Constants.WALLPAPER_TIME_MILLI.length - 1];
				}
			} else {
				try {
					frameDuration = Constants.WALLPAPER_TIME_MILLI[inAppPreference.getChangeTime()];
				} catch (Exception e) {
					e.printStackTrace();
					frameDuration = Constants.WALLPAPER_TIME_MILLI[Constants.WALLPAPER_TIME_MILLI.length - 1];
				}
			}
			if(isPreview()) {
				wallpaperSelectedPaths = inAppPreference.getSelectedImageTemp();
			}else {
				if(!inAppPreference.isServiceEnable()){
					wallpaperSelectedPaths = inAppPreference.getSelectedImageTemp();
				}else
					wallpaperSelectedPaths = "";
				inAppPreference.setServiceEnable(true);
			}
			LoggerCustom.erorr(TAG, "AutoWallpaperChanger onCreate : "+ wallpaperSelectedPaths);
			if(!TextUtils.isEmpty(wallpaperSelectedPaths)){
				setWall(wallpaperSelectedPaths);
			}else
				getImagesList();
		}

		public MyWallpaperEngine() {
			super();

			WindowManager windowManager =
					(WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);

			mWidth = displayMetrics.widthPixels;
			mHeight = displayMetrics.heightPixels;

			LoggerCustom.i(TAG, "AutoWallpaperChanger :: height1 : " + mHeight);
			inAppPreference = InAppPreference.getInstance(AutoChangerService.this);
		}

		private void reDraw() {

				SurfaceHolder surfaceHolder = getSurfaceHolder();
				canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas();
					canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

					if(toBeDraw!=null){
						Paint p = new Paint();
						p.setColor(Color.BLACK);
						if (((frameDuration / 100) - anInt) < 10) {
//						getImagesList();
							anInt = 0;
							isWallpaperChanged = true;
//						Bitmap resize=getResizedBitmap(imgBit,height,width);
							Bitmap resize = resize(toBeDraw);
//						Logger.e("size",""+resize.getHeight());
							if (resize.getHeight() < (mHeight - 20))
								canvas.drawBitmap(resize, mWidth / 2 - resize.getWidth() / 2, mHeight / 2 - resize.getHeight() / 2, p);
							else
								canvas.drawBitmap(resize, mWidth / 2 - resize.getWidth() / 2, 0, p);
						} else {
							p.setAlpha(255);
							Bitmap resize1 = resize(toBeDraw);
							if (resize1.getHeight() < mHeight - 20)
								canvas.drawBitmap(resize1, mWidth / 2 - resize1.getWidth() / 2, mHeight / 2 - resize1.getHeight() / 2, p);
							else
								canvas.drawBitmap(resize1, mWidth / 2 - resize1.getWidth() / 2, 0, p);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				} finally {
					try {
						if (canvas != null)
							surfaceHolder.unlockCanvasAndPost(canvas);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}


				if(mHandler!=null && mUpdateDisplay!=null){
					mHandler.removeCallbacks(mUpdateDisplay);
					if (mVisible) {
						mHandler.postDelayed(mUpdateDisplay, 100);
					}
				}
		}

		private Bitmap resize(Bitmap image) {
			return image;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			LoggerCustom.erorr(TAG, "onVisibilityChanged:" + visible);
			LoggerCustom.erorr(TAG, "counter:" + anInt);

			if(!isPreview())
				isServiceRunning=true;
			mVisible = visible;
			if (visible) {
				if (isPreview()) {
					try {
						frameDuration = Constants.WALLPAPER_TIME_MILLI[inAppPreference
								.getChangeTime()];
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						frameDuration = Constants.WALLPAPER_TIME_MILLI[inAppPreference
								.getChangeTime()];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				reDraw();
				//isWallpaperChange = true;
				long currentTime = Calendar.getInstance().getTimeInMillis();
				if ((currentTime - getLast_auto_wallpaper_change_time()) > frameDuration) {
					isWallpaperChanged = true;
				} else {
					isWallpaperChanged = false;
				}
				LoggerCustom.erorr(TAG, (isPreview() ? "Preview : " : "") + "onVisibilityChanged:" + visible + " isWallpaperChange:" + isWallpaperChanged);
				if (isWallpaperChanged) {
					getImagesList();
				}
			} else {
				if(mHandler!=null && mUpdateDisplay!=null)
					mHandler.removeCallbacks(mUpdateDisplay);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
									 int width, int height) {
			LoggerCustom.erorr(TAG, "onSurfaceChanged");
			reDraw();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			LoggerCustom.erorr(TAG, "onSurfaceDestroyed");
			mVisible = false;
			if(mHandler!=null && mUpdateDisplay!=null)
				mHandler.removeCallbacks(mUpdateDisplay);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			if(!isPreview()) {
				isServiceRunning = false;
				if (inAppPreference != null)
					inAppPreference.setServiceEnable(false);
			}
			LoggerCustom.erorr(TAG, "onDestroy");
			mVisible = false;
			if(mHandler!=null && mUpdateDisplay!=null)
				mHandler.removeCallbacks(mUpdateDisplay);
			toBeDraw=null;
			canvas =null;
		}

		private void getMoreData() {
			LoggerCustom.erorr(TAG, "getdata :");
			if (AppUtility.isInterNetworkAvailable(AutoChangerService.this)) {
				RetroApiRequest.getAutoWallpaperChangerRetro(AutoChangerService.this, new RetroCallbacks() {
					@Override
					public void onDataSuccess(IModel result) throws JSONException {
						WallpaperListModel model = (WallpaperListModel) result;
						if (model.getWallpaper() != null) {
							List<WallpaperModel>  resultList = model.getWallpaper();
							WallpaperModel wallpaper = resultList.get(0);
							Log.e("TAG", "onSuccess: " + result);
							lastWallpaperChangeTime = 0;
							inAppPreference.setLastAutoChangedTime();
							String path = inAppPreference.getImgUrl() + Constants.hdPath + wallpaper.getImgPath();
							setWall(path);
						}
					}
					@Override
					public void onDataError(String result) throws Exception {
					}
				});
			} else {
			}
		}

		private void getImagesList(){
			LoggerCustom.erorr(TAG, "getImagesList:");
			if(isPreview()){
				setWall(wallpaperSelectedPaths);
				return;
			}
			getMoreData();
		}

		private void setWall(String path) {
			LoggerCustom.erorr(TAG, "setWall path:" + path);

			if(mWidth ==0){
				WindowManager windowManager =
						(WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

				DisplayMetrics displayMetrics = new DisplayMetrics();
				windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);

				mWidth = displayMetrics.widthPixels;
				mHeight = displayMetrics.heightPixels;
			}

			LoggerCustom.i(TAG, "setWall :: screenWidht : " + mWidth +" screenheight:"+ mHeight);
			Glide.with(getApplicationContext())
					.asBitmap()
					.load(path).apply(new RequestOptions().override(mWidth, mHeight)).centerCrop()
					.into(new CustomTarget<Bitmap>() {
						@Override
						public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
							toBeDraw=resource;
							anInt =0;
							reDraw();
						}
						@Override
						public void onLoadCleared(@Nullable Drawable placeholder) {
						}
					});
		}
	}
}
