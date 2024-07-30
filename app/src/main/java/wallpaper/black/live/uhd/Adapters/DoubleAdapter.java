package wallpaper.black.live.uhd.Adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.BlurImageFunction;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.BuildConfig;
import wallpaper.black.live.uhd.Fragments.DoubleWallpaperFragment;
import wallpaper.black.live.uhd.Interface.DoubleClickCallback;
import wallpaper.black.live.uhd.Model.DoubleWallsModel;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class DoubleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DoubleWallsModel> doubleWallsModels;
    public static final int WALLPAPER_TYPE = 0;
    public static final int HOME_LOADING = 1;
    private Context mContext;
    private InAppPreference inAppPreference;
    private int counter = 0;
    private DoubleClickCallback downloadCallback;

    public DoubleAdapter(List<DoubleWallsModel> doubleList, Context context, DoubleClickCallback callback) {
        this.doubleWallsModels = doubleList;
        this.mContext = context;
        this.downloadCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case WALLPAPER_TYPE:
                View listItem = layoutInflater.inflate(R.layout.item_double, parent, false);
                ViewHolder viewHolder = new ViewHolder(listItem);
                return viewHolder;
            case HOME_LOADING:
                View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_common, parent, false);
                return new ProgressHolder(v4);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final int type = getItemViewType(position);

        DoubleWallsModel doubleWallpaper = doubleWallsModels.get(position);
        inAppPreference = InAppPreference.getInstance(mContext);

        switch (type) {
            case WALLPAPER_TYPE:

                float height;

                height = mContext.getResources().getDimension(R.dimen.double_wall_hight);

                Animator translationAnimator = ObjectAnimator.ofFloat(((ViewHolder) holder).llImg1, View.TRANSLATION_Y, 0f, -height).setDuration(1000);
                final Animator alphaAnimator = ObjectAnimator.ofFloat(((ViewHolder) holder).llImg1, View.ALPHA, 1f, 0f).setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        translationAnimator,
                        alphaAnimator
                );

                String currentDate = new SimpleDateFormat("EEE, d MMM ", Locale.getDefault()).format(new Date());
                ((ViewHolder) holder).txt_date.setText(currentDate);

                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                ((ViewHolder) holder).time_main.setText(currentTime);
                ((ViewHolder) holder).txt_time_lock.setText(currentTime);

                if (position == DoubleWallpaperFragment.currentPosition) {
                    animatorSet.start();
//                    Log.e("TAG", "onBindViewHolder: animation Start" );
//                    Log.e("TAG", "onBindViewHolder: animation Start DoubleWallsFragment -"+DoubleWallsFragment.currentPosition);
//                    Log.e("TAG", "onBindViewHolder: animation Start position -"+position);
                } else {
                    animatorSet.end();
//                    Log.e("TAG", "onBindViewHolder: animation end" );
                }

                Glide.with(mContext)
                        .asBitmap()
                        .load(inAppPreference.getImgUrl()+ Constants.doubleThumbImgPath+doubleWallpaper.getImg1())
                        .centerCrop()
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                Bitmap m = BlurImageFunction.with(mContext).load(bitmap).intensity(25).Async(true).getImageBlur();
                                Drawable d = new BitmapDrawable(mContext.getResources(), m);
//                                drawerLayout.setBackground(d);
//                                ((ViewHolder) holder).back.setBackground(d);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });

                Glide.with(mContext)
                        .load(inAppPreference.getImgUrl()+ Constants.doubleThumbImgPath+doubleWallpaper.getImg1())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                counter = counter + 1;
                                return false;
                            }
                        }).into(((ViewHolder) holder).img_1);

                Glide.with(mContext)
                        .load(inAppPreference.getImgUrl()+ Constants.doubleThumbImgPath+doubleWallpaper.getImg2())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                counter = counter + 1;
                                return false;
                            }
                        }).into(((ViewHolder) holder).img_2);

                animatorSet.addListener(new Animator.AnimatorListener() {
                    private boolean mCanceled;

                    @Override
                    public void onAnimationStart(Animator animator) {
                        mCanceled = false;
                    }

                    @Override
                    public void onAnimationEnd(final Animator animator) {

                        if (!mCanceled) {

                            if (((ViewHolder) holder).llImg1 == null) {
                                Log.e("TAG", "onAnimationEnd: Image is Null---" );
                                return;
                            }

                            if (DoubleWallpaperFragment.currentPosition != position) {
                                ((ViewHolder) holder).llImg1.setTranslationY(0);
                                final Animator alphaAnimatorFad = ObjectAnimator
                                        .ofFloat(((ViewHolder) holder).llImg1, View.ALPHA, 0f, 1f).setDuration(400);
                                alphaAnimatorFad.start();
//                                Log.e("TAG", "onAnimationEnd: Current Position---" );
                                return;
                            }
//                            Log.e("TAG", "onAnimationEnd: Anim End---" );

                            ((ViewHolder) holder).llImg1.setTranslationY(0);
                            final Animator alphaAnimatorFad = ObjectAnimator
                                    .ofFloat(((ViewHolder) holder).llImg1, View.ALPHA, 0f, 1f).setDuration(800);
                            alphaAnimatorFad.start();
                            alphaAnimatorFad.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator1) {

                                    if (!mCanceled) {
                                        if (animator != null) {
                                            animator.setStartDelay(1000);
                                            animator.start();
//                                            Log.e("TAG", "onAnimationEnd: animator Anim END---" );
                                        }
//                                        Log.e("TAG", "onAnimationEnd: Canceled Anim END---" );
                                    }

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        mCanceled = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

                ((ViewHolder) holder).set.setTag("" + position);
                ((ViewHolder) holder).set.setOnClickListener(onClickListener);

                ((ViewHolder) holder).img_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteToServer(doubleWallpaper);
                    }
                });
                break;

            case HOME_LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return doubleWallsModels.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (doubleWallsModels != null && doubleWallsModels.size() != 0) {
            if (doubleWallsModels.get(position).getImg_id().equals("-99")) {
                return HOME_LOADING;
            } else {
                return WALLPAPER_TYPE;
            }
        }
        return 0;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout photosLayout, back;
        private RelativeLayout llImg1, llImg2;
        private ImageView img_1, img_2, set,img_delete;
        private TextView time_main,txt_date,txt_time_lock;

        public ViewHolder(@NonNull View view) {
            super(view);

            img_1 = view.findViewById(R.id.img_1);
            img_2 = view.findViewById(R.id.img_2);
            llImg1 = view.findViewById(R.id.rl_img1);
            llImg2 = view.findViewById(R.id.rl_img2);
            photosLayout = view.findViewById(R.id.rl_photos);
            back = view.findViewById(R.id.z);
            set = view.findViewById(R.id.img_set);
            time_main = view.findViewById(R.id.txt_time);
            txt_date = view.findViewById(R.id.txt_date);
            txt_time_lock = view.findViewById(R.id.txt_time_lock);
            img_delete = view.findViewById(R.id.img_delete);

            if(BuildConfig.DEBUG)
                img_delete.setVisibility(View.VISIBLE);
            else
                img_delete.setVisibility(View.GONE);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!AppUtility.isInterNetworkAvailable(mContext)) {
                AppUtility.showInterNetworkDialog(mContext,null);
                return;
            }

            int pos = Integer.parseInt(view.getTag().toString());
            try {
                downloadCallback.onDownloadClick(pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void deleteToServer(DoubleWallsModel doubleWallsModel) {
        LoggerCustom.erorr("ThumbImageActivity",doubleWallsModel.getImg_id()+" path:"+doubleWallsModel.getImg1());
        RetroApiRequest.deleteDoubleRetro(mContext, doubleWallsModel.getImg_id(),  new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                Toast.makeText(mContext, R.string.delete, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataError(String result) throws Exception {
            }
        });
    }

    protected class ProgressHolder extends RecyclerView.ViewHolder {
        public ProgressHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.progress_download).setVisibility(View.VISIBLE);
        }
    }

    public void destroyResource() {
        mContext = null;
        downloadCallback = null;
        if (doubleWallsModels != null)
            doubleWallsModels.clear();
        doubleWallsModels = null;
        onClickListener = null;
    }

}
