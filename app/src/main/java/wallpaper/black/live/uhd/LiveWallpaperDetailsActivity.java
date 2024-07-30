package wallpaper.black.live.uhd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.BlurImageFunction;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.Services.ExoVideoService;
import wallpaper.black.live.uhd.databinding.ActivityLiveWallpaperDetailsBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class LiveWallpaperDetailsActivity extends BaseActivity implements View.OnClickListener, InternetNetworkInterface {

    private InAppPreference inAppPreference;
    private WallpaperModel wallpaperModel;
    private boolean isLikedVideo;
    private String url;
    private WallpaperInfo old_info;
    private String pathdst;
    private boolean isDownloadComplete = false;
    public static boolean isAdForceOnBack;
    private ThinDownloadManager thinDownloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 1;
    private MyDownloadDownloadStatusListenerV1
            myDownloadListener = new MyDownloadDownloadStatusListenerV1();
    private int downloadId1;
    ActivityLiveWallpaperDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveWallpaperDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, this);
            return;
        }

        if (savedInstanceState == null) {
            isAdForceOnBack = false;
        }

        FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_VIDEO, FirebaseEventManager.ATR_VALUE_CLICK, FirebaseEventManager.ATR_VALUE_CLICK);
        inAppPreference = InAppPreference.getInstance(this);

        binding.fullscreenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                LoggerCustom.erorr("ThumbActivity", "onTouch");

                if (isDownloadComplete && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (binding.llUserOption.getAlpha() == 1) {
                        setAnimeFadeOutButton(binding.llUserOption);
                        setAnimeFadeOutButton(binding.videoFeature);
                    } else {
                        setAnimeFadeINButton(binding.llUserOption);
                        setAnimeFadeINButton(binding.videoFeature);
                    }
                    return true;
                }
                return false;
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.back.setVisibility(View.GONE);

        Intent i = getIntent();
        wallpaperModel = (WallpaperModel) i.getSerializableExtra("object");

        if(wallpaperModel ==null)
        {
            finish();
            return;
        }
        url = inAppPreference.getImgUrl() + Constants.liveVideoPath + wallpaperModel.getVidPath();
        pathdst = AppUtility.createBaseDirectoryCache() + "/" + wallpaperModel.getImgId() + Constants.DOWNLOAD_EXTENSION_VIDEO;

        binding.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(false);
            }
        });

        binding.fabDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(true);
            }
        });

        Glide.with(this)
                .asBitmap().load(inAppPreference.getImgUrl() + Constants.liveThumbPath + wallpaperModel.getImgPath())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                        try {
                            if (bitmap != null) {
                                BlurImageFunction.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(binding.imgviewBlur);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Error e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .submit();
        binding.imgLikeWallpaper.setOnClickListener(this);
        binding.imgSetWallpaper.setOnClickListener(this);
        updateWallpaperLike();
        try {
            if (Constants.isWallSetCancel) {
                WallpaperManager wpm = WallpaperManager.getInstance(this);
                old_info = wpm.getWallpaperInfo();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(wallpaperModel.getImgId()))
            downloadVideoFile();
    }

    private void showDeleteDialog(boolean isDisable) {

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setCancelable(true);

        if(isDisable){
            builder.setMessage(getString(R.string.disable_txt));
            builder.setTitle(getString(R.string.dis_dialog))
                    .setPositiveButton(getString(R.string.yes_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            DisableToServer();
                        }
                    });
        }else{
            builder.setMessage(getString(R.string.delete_txt));
            builder.setTitle(getString(R.string.del_dialog))
                    .setPositiveButton(getString(R.string.yes_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deleteToServer();
                        }
                    });
        }

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void setAlphaAnimation(View v) {

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
        fadeOut.setDuration(400);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeOut);
        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                try {
                    binding.llUserOption.setAlpha(0.0f);
                    binding.videoFeature.setAlpha(0.0f);
                    binding.llUserOption.setVisibility(View.VISIBLE);
                    binding.videoFeature.setVisibility(View.VISIBLE);
                    setAnimeFadeINButton(binding.llUserOption);
                    setAnimeFadeINButton(binding.videoFeature);
                    binding.fullscreenView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mAnimationSet.start();
    }

    public void setAnimeFadeOutButton(View v) {

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
        fadeIn.setDuration(300);
        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        mAnimationSet.start();
    }

    private void DisableToServer() {
        LoggerCustom.erorr("ThumbImageActivity", wallpaperModel.getImgPath());
        RetroApiRequest.disableVideoRetro(this, wallpaperModel.getImgId(), new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                finish();
            }
            @Override
            public void onDataError(String result) throws Exception {
            }
        });
    }

    private void deleteToServer() {
        LoggerCustom.erorr("Delete","id:"+ wallpaperModel.getImgId()+" paath:"+ wallpaperModel.getVidPath());
        RetroApiRequest.deleteVideoRetro(this, wallpaperModel.getImgId(), new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                finish();
            }

            @Override
            public void onDataError(String result) throws Exception {
            }
        });
    }

    private void playLiveWallpaper() {
        try {
            binding.back.setVisibility(View.VISIBLE);
            binding.fullscreenView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(pathdst);

            binding.fullscreenView.setVideoURI(uri);
            binding.fullscreenView.requestFocus();

            binding.fullscreenView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = binding.fullscreenView.getWidth() / (float)
                            binding.fullscreenView.getHeight();
                    float scaleX = videoRatio / screenRatio;
                    if (scaleX >= 1f) {
                        binding.fullscreenView.setScaleX(scaleX);
                    } else {
                        binding.fullscreenView.setScaleY(1f / scaleX);
                    }
                    mp.setVolume(0, 0);
                    mp.setLooping(true);
                }
            });

            binding.fullscreenView.start();

            inAppPreference.setVideoLiveWallpaperPathTemp(pathdst);

            if (!binding.rlProgress.isShown()) {
                binding.llUserOption.setVisibility(View.VISIBLE);
                binding.imgviewBlur.setVisibility(View.GONE);
                binding.rlProgress.setVisibility(View.GONE);
                binding.videoFeature.setVisibility(View.VISIBLE);
                if (Constants.IS_DELETE) {
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.fabDisable.setVisibility(View.VISIBLE);
                } else {
                    binding.fabDelete.setVisibility(View.GONE);
                    binding.fabDisable.setVisibility(View.GONE);
                }
            } else {
                binding.rlProgress.setVisibility(View.GONE);
                setAlphaAnimation(binding.imgviewBlur);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOkClick() throws Exception {
        finish();
    }

    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

        @Override
        public void onDownloadComplete(DownloadRequest request) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {
                playLiveWallpaper();
                isDownloadComplete = true;
                LoggerCustom.erorr("Download", "onDownloadComplete");
            }
        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            try {
                LoggerCustom.erorr("Download", "onDownloadFailed");
                final int id = request.getDownloadId();
                if (id == downloadId1) {
                    deleteAndDownload();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();

            if (isFinishing()) {
                return;
            }

            System.out.println("######## onProgress ###### " + id + " : " + totalBytes + " : " + downloadedBytes + " : " + progress);
            try {
                binding.progressDownload.setCurrentProgress(progress);

                if (downloadedBytes == 0l) {
                    downloadedBytes = 1l;
                }
                binding.videofilesize.setText(convertToStringRepresentation(downloadedBytes) + "/" + convertToStringRepresentation(totalBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeDownload(String pathToDownload, String downloadURL) {

        thinDownloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

        RetryPolicy retryPolicy = new DefaultRetryPolicy();
        Uri downloadUri = Uri.parse(downloadURL);
        Uri destinationUri = Uri.parse(pathToDownload);
        final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setRetryPolicy(retryPolicy)
                .setDownloadContext("Download1")
                .setStatusListener(myDownloadListener);

        if (thinDownloadManager.query(downloadId1) == com.thin.downloadmanager.DownloadManager.STATUS_NOT_FOUND) {
            downloadId1 = thinDownloadManager.add(downloadRequest1);
        }
    }

    public void setAnimeFadeINButton(View v) {

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.back.setVisibility(View.VISIBLE);
                if (Constants.IS_DELETE) {
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.fabDisable.setVisibility(View.VISIBLE);
                } else {
                    binding.fabDelete.setVisibility(View.GONE);
                    binding.fabDisable.setVisibility(View.GONE);
                }
            }
        });
        mAnimationSet.start();
    }

    public void downloadVideoFile() {

        String dirPath = AppUtility.createBaseDirectoryCache();
        String fileName = wallpaperModel.getImgId() + Constants.DOWNLOAD_EXTENSION_VIDEO;
        LoggerCustom.erorr("download video path : ", "Destination:" + pathdst);

        File file = new File(dirPath + "/" + fileName);
        if (file != null && file.exists()) {
            isDownloadComplete = true;
            playLiveWallpaper();
            return;
        }
        initializeDownload(pathdst, url);
    }

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;

    public static String convertToStringRepresentation(final long value) {
        final long[] dividers = new long[]{T, G, M, K, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};
        if (value < 1)
            throw new IllegalArgumentException("Invalid file size: " + value);
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    private void deleteAndDownload() {

        try {
            if (!AppUtility.isInterNetworkAvailable(this)) {
                Toast.makeText(this, getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (thinDownloadManager != null && !isDownloadComplete) {
                thinDownloadManager.cancel(downloadId1);
                File file = new File(pathdst);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        downloadVideoFile();
    }


    @Override
    public void onResume() {
        super.onResume();
        LoggerCustom.erorr("tag", "onResume");
        if (!binding.fullscreenView.isPlaying() && isDownloadComplete) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    playLiveWallpaper();
                }
            }, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoggerCustom.erorr("tag", "onPause");
        if (binding.fullscreenView != null && binding.fullscreenView.isPlaying()) {
            LoggerCustom.erorr("pause", "onPause");
            binding.fullscreenView.pause();
        }
    }

    private void updateOnClick() {
        startLoadingLike();
        if (isLikedVideo) {
            dislikeLiveWallpaper();
        } else {
            likeLiveWallpaper();
        }
    }

    private void dismissLikeLoading() {
        binding.liveProgress.setVisibility(View.GONE);
        binding.imgLikeWallpaper.setVisibility(View.VISIBLE);
    }

    private void startLoadingLike() {
        binding.liveProgress.setVisibility(View.VISIBLE);
        binding.imgLikeWallpaper.setVisibility(View.GONE);
    }

    private void likeLiveWallpaper() {
        isLikedVideo = true;
        binding.imgLikeWallpaper.setImageResource(R.mipmap.favorite_red);

        try {
            String s = SingletonControl.getInstance().getDataList().getData().getLikeLiveId();
            if (TextUtils.isEmpty(s)) {
                s = wallpaperModel.getImgId();
            } else {
                s = s + "_" + wallpaperModel.getImgId();
            }
            SingletonControl.getInstance().getDataList().getData().setLikeLiveId(s);

            if (!AppUtility.isInterNetworkAvailable(this)) {
                AppUtility.showInterNetworkDialog(this, this);
                return;
            }
            RetroApiRequest.setLikeRetro(this, wallpaperModel.getImgId(), "0", "1", new RetroCallbacks() {
                @Override
                public void onDataSuccess(IModel result) throws JSONException {
                    dismissLikeLoading();
                    Toast.makeText(LiveWallpaperDetailsActivity.this, getString(R.string.set_fav_succ), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDataError(String result) throws Exception {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dislikeLiveWallpaper() {

        isLikedVideo = false;
        binding.imgLikeWallpaper.setImageResource(R.mipmap.favorite_white);

        try {
            String s = SingletonControl.getInstance().getDataList().getData().getLikeLiveId();
            List<String> arr = new LinkedList<String>(Arrays.asList(s.split("_")));

            String newString = null;
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).equals(wallpaperModel.getImgId())) {
    //                arr.remove(i);
                } else {
                    if (TextUtils.isEmpty(newString)) {
                        newString = arr.get(i);
                    } else {
                        newString = newString + "_" + arr.get(i);
                    }
                }
            }
            SingletonControl.getInstance().getDataList().getData().setLikeLiveId(newString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, this);
            return;
        }
        RetroApiRequest.setLikeRetro(this, wallpaperModel.getImgId(), "1", "1", new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                Toast.makeText(LiveWallpaperDetailsActivity.this,  getString(R.string.remove_fav), Toast.LENGTH_SHORT).show();
                dismissLikeLoading();
            }

            @Override
            public void onDataError(String result) throws Exception {

            }
        });
    }

    private void updateWallpaperLike() {
        if(SingletonControl.getInstance().getDataList()!=null && SingletonControl.getInstance().getDataList().getData()!=null){
            String s = SingletonControl.getInstance().getDataList().getData().getLikeLiveId();
            if (TextUtils.isEmpty(s)) {
                return;
            }
            String[] arr = s.split("_");
            for (int i = 0; i < arr.length; i++) {

                if (arr[i].equals(wallpaperModel.getImgId())) {
                    isLikedVideo = true;
                    binding.imgLikeWallpaper.setImageResource(R.mipmap.favorite_red);
                    break;
                } else {
                    isLikedVideo = false;
                }
            }
        }

    }

    private void setLiveWallpaper() {
        if (SingletonControl.getInstance().getDataList()==null){
            return;
        }

        boolean needToCallPreview = true;
        if (Build.VERSION.SDK_INT <= 32) {
            if (old_info != null && old_info.getComponent().getClassName().equals(ExoVideoService.class.getCanonicalName())) {
                needToCallPreview = false;
            }
        }else{
            if(ExoVideoService.isExoServiceRunning){
                needToCallPreview = false;
            }
        }

        if (Constants.isWallSetCancel && !needToCallPreview) {
            AppUtility.showDialogConfirmationUser(this, getString(R.string.info_btn), getString(R.string.change_live), new AppUtility.DialogOnButtonClickListener() {
                @Override
                public void onOKButtonCLick() {
                    inAppPreference.setVideoLiveWallpaperPath(pathdst);
                    Toast.makeText(LiveWallpaperDetailsActivity.this, getString(R.string.wall_updated), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelButtonCLick() {
                }
            });
        } else {
            Intent intentVideo = null;

            try {
                intentVideo = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intentVideo.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

                intentVideo.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(this,
                                ExoVideoService.class));
                startActivityForResult(intentVideo, 200);
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    startActivityForResult(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new
                                    ComponentName(this, ExoVideoService.class))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 200);
                } catch (ActivityNotFoundException e2) {
                    Toast.makeText(this, R.string.toast_failed_launch_wallpaper_chooser, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.img_likeWallpaper:
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_VIDEO, FirebaseEventManager.ATR_KEY_FAVOURITE, FirebaseEventManager.ATR_VALUE_CATEGORY_ID + wallpaperModel.getCategoryId());
                updateOnClick();
                break;
            case R.id.img_setWallpaper:
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_VIDEO, FirebaseEventManager.ATR_KEY_SET, FirebaseEventManager.ATR_VALUE_CATEGORY_ID + wallpaperModel.getCategoryId());
                setLiveWallpaper();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(inAppPreference ==null || wallpaperModel ==null)
            return;
        if (resultCode == RESULT_OK) {
            if (inAppPreference.getCatViewCount().equalsIgnoreCase(""))
                inAppPreference.setCatViewCount("'" + wallpaperModel.getCategoryId() + "'");
            else {
                if (!inAppPreference.getCatViewCount().contains("'" + wallpaperModel.getCategoryId() + "'")) {
                    inAppPreference.setCatViewCount(inAppPreference.getCatViewCount() + ",'" + wallpaperModel.getCategoryId() + "'");
                }
            }
            isAdForceOnBack=true;
            inAppPreference.setVideoLiveWallpaperPath(pathdst);
            Toast.makeText(this, getString(R.string.wall_applied), Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            if (Constants.isWallSetCancel) {
                WallpaperManager wpm = WallpaperManager.getInstance(this);
                WallpaperInfo info = wpm.getWallpaperInfo();

                if (info == null) {
                    return;
                }

                if (old_info != null) {
                    LoggerCustom.erorr("onActivityResult", "Old:" + old_info.getComponent().getClassName());
                }

                if(SingletonControl.getInstance().getDataList()==null || SingletonControl.getInstance().getDataList().getLogic()==null)
                    return;

                if (info != null && (old_info == null || (!old_info.getComponent().getClassName().equals(info.getComponent().getClassName())
                        && info.getComponent().getClassName().equals(ExoVideoService.class.getCanonicalName())))) {
                    LoggerCustom.erorr("onActivityResult", "canceled: dst" + pathdst);
                    inAppPreference.setVideoLiveWallpaperPath(pathdst);
                    isAdForceOnBack=true;
                    Toast.makeText(this, getString(R.string.wall_applied), Toast.LENGTH_SHORT).show();

                }
            }
        }
        // -1 success 0 failed
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (binding.fullscreenView != null) {
                binding.fullscreenView.stopPlayback();
                binding.fullscreenView.setVideoURI(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!isDownloadComplete) {
                thinDownloadManager.cancel(downloadId1);
                File f = new File(pathdst);
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        inAppPreference = null;
        wallpaperModel = null;
        isLikedVideo = false;
        url = null;
        old_info = null;
        pathdst = null;
        isDownloadComplete=false;
    }
}
