package wallpaper.black.live.uhd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.BlurryFilterBuilder;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.CustomViews.WallAppliedDialog;
import wallpaper.black.live.uhd.CustomViews.WallAppliedDialogListener;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.databinding.ActivityWallpaperDetailsBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class WallpaperDetailsActivity extends BaseActivity implements InternetNetworkInterface {

    private BottomSheetDialog bottomSheetDialog;
    private final ColorMatrix brightnessMatrix = new ColorMatrix();
    private ColorMatrix tintMatrix = new ColorMatrix();
    private final ColorMatrix overlayMatrix = new ColorMatrix();
    private ThinDownloadManager downloadManagerThin;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 1;
    private DownloadStatusListener downloadStatusListener = new DownloadStatusListener();
    private int downloadId;
    public static boolean isImageLoaded;
    public static boolean isAddShown;
    private Slider brightnessSlider, blurSlider, tintSlider, overlaySlider;
    private float radius;
    private InAppPreference inAppPreference;
    private Drawable orginalImg;
    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 1000;
    private WallpaperModel wallpaperModel;
    private boolean isLikedWallpaper;
    private String downloadUrl;
    public static boolean isAdForceOnBack;
    private String destination, edited_dst, GalleryUrl;
    boolean isInfoVisible;
    private int actionType = 0;
    private int setAs;
    private AlertDialog alertDialog;

    private ImageView img_reset, img_editSetWall;

    private ColorMatrix orgColorMatrix;
    private boolean isedited;
    RelativeLayout editDialogLayout;
    private Bitmap editedBitmap;
    private boolean isFromGallery;
    private float[] orrMatrixValues;
    ActivityWallpaperDetailsBinding binding;
    boolean isHiddenvisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallpaperDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LoggerCustom.erorr("ThumbActivity", "onCreate:" + savedInstanceState);
        if (savedInstanceState == null) {
            isAdForceOnBack = false;
        }

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, this);
            return;
        }

        FirebaseEventManager.sendEventFirebase("Open", "Wallpaper", "detail");
        binding.llUserOption.setAlpha(0.0f);
        binding.thumbTool.setAlpha(0.0f);

        try {
            if (SingletonControl.getInstance().getDataList().getLogic().is_edit_show()) {
                binding.imgEditwall.setVisibility(View.VISIBLE);
            } else
                binding.imgEditwall.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        inAppPreference = InAppPreference.getInstance(this);

        binding.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orginalImg == null)
                    return;
                showInfoMessage();
            }
        });

        binding.ImgMainWallpaper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                LoggerCustom.erorr("ThumbActivity", "onTouch");
                if (isImageLoaded && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (findViewById(R.id.edit_dialog_include).getVisibility() == View.VISIBLE) {
                        LoggerCustom.erorr("ThumbActivity", "onTouch edit_dialog_include");
                        enableSetButton();
                    } else if (binding.llUserOption.getAlpha() == 1) {
                        LoggerCustom.erorr("ThumbActivity", "onTouch if");
                        findViewById(R.id.edit_dialog_include).setVisibility(View.GONE);
                        setFadeOutButton(binding.llUserOption);
                        setFadeOutButton(binding.thumbTool);
//                        findViewById(R.id.ll_user_option).setVisibility(View.VISIBLE);
//                        findViewById(R.id.thumbTool).setVisibility(View.VISIBLE);
                    } else {
                        LoggerCustom.erorr("ThumbActivity", "onTouch else");
                        setFadeINButton(binding.llUserOption);
                        setFadeINButton(binding.thumbTool);
                    }
                    // Do what you want
                    return true;
                }
                return false;
            }
        });

        binding.imgDownloadWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orginalImg == null)
                    return;
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_THUMB_IMG, FirebaseEventManager.ATR_KEY_DOWNLOAD, FirebaseEventManager.ATR_VALUE_CATEGORY_ID + wallpaperModel.getCategoryId());
                actionType = 0;
                downloadFile(downloadUrl, getDownloadPath(), wallpaperModel.getImgPath());
            }
        });

        binding.imgShareWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orginalImg == null)
                    return;
                actionType = 1;
                if (!isFromGallery)
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_THUMB_IMG, FirebaseEventManager.ATR_KEY_SHARE, FirebaseEventManager.ATR_VALUE_CATEGORY_ID + wallpaperModel.getCategoryId());
                downloadFile(downloadUrl, getDownloadPath(), wallpaperModel.getImgPath());
            }
        });

        binding.imgSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orginalImg == null)
                    return;
                if (!isFromGallery) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_THUMB_IMG, FirebaseEventManager.ATR_KEY_SET, FirebaseEventManager.ATR_VALUE_CATEGORY_ID + wallpaperModel.getCategoryId());
                }
                actionType = 2;
                showBottomDialog();
                // downloadFile(downloadUrl, getDownloadPath(), wallpaper.getImgPath());
            }
        });

        binding.imgLikeWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orginalImg == null)
                    return;
                actionType = 3;
                updateOnClick();
            }
        });

        binding.imgEditwall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orginalImg == null)
                    return;
                ShowEditDialog();
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orginalImg == null)
                    return;
                finish();
            }
        });
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
        binding.imgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHiddenvisible) {

                    collapseView(binding.hiddenOptions);
                    isHiddenvisible = false;

                    Animation rotation = AnimationUtils.loadAnimation(WallpaperDetailsActivity.this, R.anim.rotation_normal);
                    rotation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            binding.imgOption.setRotation(90f);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    binding.imgOption.startAnimation(rotation);
                    rotation.setFillAfter(true);

                } else {
                    expandView(binding.hiddenOptions);
                    isHiddenvisible = true;
                    Animation rotation = AnimationUtils.loadAnimation(WallpaperDetailsActivity.this, R.anim.rotation);
                    rotation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            binding.imgOption.setRotation(90f);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    binding.imgOption.startAnimation(rotation);
//                    binding.icOption.setRotation(90f);
                    rotation.setFillAfter(true);
                }

            }
        });

        Intent i = getIntent();
        wallpaperModel = (WallpaperModel) i.getSerializableExtra("object");

        if (getIntent() != null) {
            Intent in = getIntent();
            isFromGallery = in.getBooleanExtra("isFromGallery", false);
            GalleryUrl = in.getStringExtra("dst");
            LoggerCustom.erorr("ThumbActivity", GalleryUrl);
        }

        if (isFromGallery) {
            wallpaperModel = new WallpaperModel();
            wallpaperModel.setImgPath(GalleryUrl);
            wallpaperModel.setImgId("-100");
            binding.imgWallpreview.setVisibility(View.INVISIBLE);
            binding.imgDownloadWallpaper.setVisibility(View.INVISIBLE);
            binding.imgLikeWallpaper.setVisibility(View.INVISIBLE);
            binding.imgInfo.setVisibility(View.INVISIBLE);
        } else {
            updateWallpaperLike();

            if (wallpaperModel == null) {
                finish();
                return;
            }
            String url2 = inAppPreference.getImgUrl() + Constants.smallPath + wallpaperModel.getImgPath();
            Glide.with(this)
                    .load(url2)
                    .apply(new RequestOptions())
                    .into(binding.imgWallpreview);
        }

        if (isFromGallery) {
            downloadUrl = wallpaperModel.getImgPath();
        } else {
            downloadUrl = inAppPreference.getImgUrl() + Constants.hdPath + wallpaperModel.getImgPath();
        }

        Glide.with(this)
                .load(downloadUrl)
                .apply(new RequestOptions())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        orginalImg = resource;
                        if (isFinishing())
                            return false;
                        try {
                            LoggerCustom.erorr("ThumbActivity", "onResourceReady:" + isFirstResource);
                            isImageLoaded = true;
                            binding.rlProgress.setVisibility(View.GONE);
                            setFadeINButton(binding.llUserOption);
                            setFadeINButton(binding.thumbTool);
                            updateInfoButton();
                            binding.imgWallpreview.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }).into(binding.ImgMainWallpaper);
    }

    private void ShowEditDialog() {
        editDialogLayout = findViewById(R.id.edit_dialog_include);
        if (editDialogLayout.getVisibility() != View.VISIBLE) {
            Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            editDialogLayout.startAnimation(slideUpAnimation);
            editDialogLayout.setVisibility(View.VISIBLE);
            binding.llUserOption.setAlpha(0f);
            binding.thumbTool.setAlpha(0f);
        } else {
            Animation slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            editDialogLayout.startAnimation(slideDownAnimation);
            editDialogLayout.setVisibility(View.INVISIBLE);
            binding.llUserOption.setVisibility(View.VISIBLE);
            binding.thumbTool.setVisibility(View.VISIBLE);
        }

        brightnessSlider = editDialogLayout.findViewById(R.id.slider_bright);
        brightnessSlider();

        blurSlider = editDialogLayout.findViewById(R.id.slider_blur);
        blurSlider.addOnChangeListener(new SliderListener());
        radius = blurSlider.getValue();
        blurSlider.setValue(radius);
        BlurSlider((int) radius);

        tintSlider = editDialogLayout.findViewById(R.id.slider_tint);
        tintSlider();

        overlaySlider = editDialogLayout.findViewById(R.id.slider_overlay);
        overlaySlider();

        orgColorMatrix = new ColorMatrix();
        orrMatrixValues = orgColorMatrix.getArray().clone();

        img_reset = editDialogLayout.findViewById(R.id.reset);
        img_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImage();
            }
        });

        img_editSetWall = editDialogLayout.findViewById(R.id.setwall);
        img_editSetWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSetButton();
                editedBitmap = captureImageViewContent(binding.ImgMainWallpaper);
            }
        });
    }

    private void enableSetButton() {
        findViewById(R.id.edit_dialog_include).setVisibility(View.GONE);
        setFadeINButton(binding.llUserOption);
        setFadeINButton(binding.thumbTool);
    }

    private void resetImage() {
        isedited = false;
        if (orginalImg != null)
            binding.ImgMainWallpaper.setImageDrawable(orginalImg);
        brightnessSlider.setValue(0.5f);
        applyBrightness(0.5f);
        blurSlider.setValue(0);
        tintSlider.setValue(0.0f);
//        applyColorTint(0.0f);
        HueFilter(0.0f);
        overlaySlider.setValue(0.5f);
        applyOverLay(0.5f);
    }

    private void brightnessSlider() {
        brightnessSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    isedited = true;
                    applyBrightness(value);
                }
            }
        });

    }

    private void applyBrightness(float brightnessValue) {
        //thumbImg.setColorFilter(createBrightnessMatrix(brightnessValue));
        if (brightnessValue <= 0.5f) {
            float scaleFactor = brightnessValue / 0.5f;
            brightnessMatrix.reset();
            brightnessMatrix.set(createBrightnessMatrix(scaleFactor));
            applyColorAdjustment();
        } else {
            float scaleFactor = 1.0f + (brightnessValue - 0.5f) * 2.0f;
            brightnessMatrix.reset();
            brightnessMatrix.set(createBrightnessMatrix(scaleFactor));
            applyColorAdjustment();
        }
    }

    public static ColorMatrix createBrightnessMatrix(float brightness) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{

                brightness, 0f, 0f, 0f, 0f,
                0f, brightness, 0f, 0f, 0f,
                0f, 0f, brightness, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        });
        return colorMatrix;
    }

    @Override
    public void onOkClick() throws Exception {
        finish();
    }

    private void showBottomDialog() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_setwall_dialog, (RelativeLayout) findViewById(R.id.setAsSheet));
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        RadioButton set_home = view.findViewById(R.id.rd_home);
        RadioButton set_lock = view.findViewById(R.id.rd_lock);
        RadioButton set_both = view.findViewById(R.id.rd_both);
        RadioButton set_system = view.findViewById(R.id.rd_system);

        if (inAppPreference.getWallScreen() == 0) {
            set_home.setChecked(true);
            set_lock.setChecked(false);
            set_both.setChecked(false);
            set_system.setChecked(false);
        } else if (inAppPreference.getWallScreen() == 1) {
            set_lock.setChecked(true);
            set_home.setChecked(false);
            set_both.setChecked(false);
            set_system.setChecked(false);
        } else if (inAppPreference.getWallScreen() == 2) {
            set_both.setChecked(true);
            set_home.setChecked(false);
            set_lock.setChecked(false);
            set_system.setChecked(false);
        } else if (inAppPreference.getWallScreen() == 3) {
            set_system.setChecked(true);
            set_home.setChecked(false);
            set_both.setChecked(false);
            set_lock.setChecked(false);
        }

        view.findViewById(R.id.rl_apply_wall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wallpaperModel == null) {
                    finish();
                    return;
                }
                int i = 0;
                if (bottomSheetDialog != null)
                    bottomSheetDialog.dismiss();

                if (set_home.isChecked()) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_SET_AS, FirebaseEventManager.ATR_KEY_SET_HOME, FirebaseEventManager.ATR_VALUE_CLICK);
                    i = 0;
                    inAppPreference.setWallScreen(i);
                } else if (set_lock.isChecked()) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_SET_AS, FirebaseEventManager.ATR_KEY_SET_LOCK, FirebaseEventManager.ATR_VALUE_CLICK);
                    i = 1;
                    inAppPreference.setWallScreen(i);
                } else if (set_both.isChecked()) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_SET_AS, FirebaseEventManager.ATR_KEY_SET_BOTH, FirebaseEventManager.ATR_VALUE_CLICK);
                    i = 2;
                    inAppPreference.setWallScreen(i);
                } else if (set_system.isChecked()) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_SET_AS, FirebaseEventManager.ATR_KEY_SET_SYSTEM, FirebaseEventManager.ATR_VALUE_CLICK);
                    i = 3;
                    inAppPreference.setWallScreen(i);
                }

                if (inAppPreference.getWallScreen() == 0) {
                    setAs = 0;
                } else if (inAppPreference.getWallScreen() == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setAs = 1;
                    } else {
                        setAs = 3;
                    }
                } else if (inAppPreference.getWallScreen() == 2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setAs = 2;
                    }
                } else if (inAppPreference.getWallScreen() == 3) {
                    setAs = 3;
                }
                //setDynamicWall(destination);
                if (setAs != 3) {
                    binding.rlProgress.setVisibility(View.VISIBLE);
                }
                downloadFile(downloadUrl, getDownloadPath(), wallpaperModel.getImgPath());
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                set_home.setVisibility(View.VISIBLE);
                set_lock.setVisibility(View.VISIBLE);
                set_both.setVisibility(View.VISIBLE);
                set_system.setVisibility(View.VISIBLE);
            } else {
                set_home.setVisibility(View.VISIBLE);
                set_lock.setVisibility(View.GONE);
                set_both.setVisibility(View.GONE);
                set_system.setVisibility(View.VISIBLE);
            }
        } else {
            set_home.setVisibility(View.VISIBLE);
            set_lock.setVisibility(View.GONE);
            set_both.setVisibility(View.GONE);
            set_system.setVisibility(View.VISIBLE);
        }
    }

    //    private ColorMatrix hueMatrix;
    private void HueFilter(float value) {
        double min = (Math.min(360.0f, Math.max(-360.0f, value)) / 360.0f) * (float) Math.PI;
        float cos = (float) Math.cos(min);
        float sin = (float) Math.sin(min);
        float f = (cos * (-0.715f)) + 0.715f;
        float f2 = ((-0.072f) * cos) + 0.072f;
        float f3 = ((-0.213f) * cos) + 0.213f;
        float[] fArr = {(sin * (-0.213f)) + (0.787f * cos) + 0.213f
                , ((-0.715f) * sin) + f
                , (sin * 0.928f) + f2, 0.0f, 0.0f
                , (0.143f * sin) + f3
                , (0.14f * sin) + (0.28500003f * cos) + 0.715f
                , ((-0.283f) * sin) + f2, 0.0f, 0.0f
                , ((-0.787f) * sin) + f3
                , (0.715f * sin) + f
                , (sin * 0.072f) + (cos * 0.928f) + 0.072f
                , 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
                , 1.0f, 0.0f, 0.0f, 0.0f, 0.0f
                , 0.0f, 1.0f};

        tintMatrix = new ColorMatrix(fArr);
        applyColorAdjustment();
    }

    private void overlaySlider() {
        overlaySlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    isedited = true;
                    applyOverLay(value);
                }
            }
        });
    }

    private void applyOverLay(float value) {
        overlayMatrix.reset();
        overlayMatrix.set(createOverlayMatrix(value));
        applyColorAdjustment();
    }

    private ColorMatrix createOverlayMatrix(float value) {

        if (value == 0.5f) {
            return new ColorMatrix();
        } else {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(value);
            return colorMatrix;
        }
    }

    private void applyColorAdjustment() {
        ColorMatrix combinedMatrix = new ColorMatrix();
        combinedMatrix.set(orrMatrixValues);
        combinedMatrix.postConcat(brightnessMatrix);
        combinedMatrix.postConcat(tintMatrix);
        combinedMatrix.postConcat(overlayMatrix);
        binding.ImgMainWallpaper.setColorFilter(new ColorMatrixColorFilter(combinedMatrix));
    }


    private class SliderListener implements Slider.OnChangeListener {
        @SuppressLint("RestrictedApi")
        @Override
        public void onValueChange(Slider slider, float value, boolean fromUser) {
            if (fromUser) {
                isedited = true;
                radius = (int) value;
                System.out.println(radius);

                if (radius < 1) {
                    BlurSlider(0);
                } else {
                    BlurSlider((int) radius);
                }
            }
        }
    }

    public void BlurSlider(int radius) {

        if (orginalImg == null)
            return;

        Bitmap originalBitmap = ((BitmapDrawable) orginalImg).getBitmap();
        int validRadius = Math.max(1, Math.min(radius, 25));

        if (radius == 0) {
            binding.ImgMainWallpaper.setImageDrawable(orginalImg);
        } else {

            Bitmap blurredBitmap = BlurryFilterBuilder.applyBlurBitmap(this, originalBitmap, validRadius);

            if (blurredBitmap != null) {
                binding.ImgMainWallpaper.setImageBitmap(blurredBitmap);
            }
        }
    }

    private void tintSlider() {
        tintSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    isedited = true;
                    HueFilter(value);
                }
            }
        });
    }

    private Bitmap captureImageViewContent(ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);
        return bitmap;
    }

    private void updateInfoButton() {
        if (!TextUtils.isEmpty(wallpaperModel.getAuthor()) || !TextUtils.isEmpty(wallpaperModel.getLicense()) || !TextUtils.isEmpty(wallpaperModel.getSourceLink())) {
            binding.imgInfo.setVisibility(View.VISIBLE);
            isInfoVisible = true;
        } else {
            isInfoVisible = false;
            binding.imgInfo.setVisibility(View.GONE);
        }
    }

    private void showInfoMessage() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.CustomInfoDialog);
        View mView = getLayoutInflater().inflate(R.layout.wallpaper_info_dialog, null);

        TextView author = mView.findViewById(R.id.author);
        if (!TextUtils.isEmpty(wallpaperModel.getAuthor()))
            author.setText(wallpaperModel.getAuthor());
        else {
            mView.findViewById(R.id.rl_author).setVisibility(View.GONE);
        }

        TextView link = mView.findViewById(R.id.link);
        if (!TextUtils.isEmpty(wallpaperModel.getSourceLink()))
            link.setText(wallpaperModel.getSourceLink());
        else {
            mView.findViewById(R.id.rl_link).setVisibility(View.GONE);
        }

        TextView license = mView.findViewById(R.id.license);
        if (!TextUtils.isEmpty(wallpaperModel.getLicense()))
            license.setText(wallpaperModel.getLicense());
        else {
            mView.findViewById(R.id.ll_licenece).setVisibility(View.GONE);
        }

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInChrome((String) link.getText());
            }
        });
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    void openUrlInChrome(String url) {
        try {
            try {
                Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                Uri uri = Uri.parse(url);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        } catch (Exception ex) {
            LoggerCustom.erorr("TAG", ex.getMessage());
        }
    }

    private void updateWallpaperLike() {
        if (SingletonControl.getInstance().getDataList() != null && SingletonControl.getInstance().getDataList().getData() != null) {
            String s = SingletonControl.getInstance().getDataList().getData().getLikeWallId();

            if (!TextUtils.isEmpty(s) && wallpaperModel != null) {
                String[] arr = s.split("_");
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].equals(wallpaperModel.getImgId())) {
                        isLikedWallpaper = true;
                        binding.imgLikeWallpaper.setImageResource(R.mipmap.favorite_red);
                        break;
                    } else {
                        isLikedWallpaper = false;
                    }
                }
            }
        }
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, AppUtility.permissions()[0])
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( //Method of Fragment
                        AppUtility.permissions(),
                        REQUEST_PERMISSIONS_CODE_WRITE_STORAGE
                );
                return false;
            } else {
                return true;
            }
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
                if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(downloadUrl, getDownloadPath(), wallpaperModel.getImgPath());
                } else if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.storage_txt), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateOnClick() {
        startLoadingLike();
        if (isLikedWallpaper) {
            dislikeWallpaper();
        } else {
            likeWallpaper();
        }
    }

    private void likeWallpaper() {

        try {
            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_THUMB_IMG, FirebaseEventManager.ATR_KEY_FAVOURITE, FirebaseEventManager.ATR_VALUE_CATEGORY_ID + wallpaperModel.getCategoryId());

            isLikedWallpaper = true;
            binding.imgLikeWallpaper.setImageResource(R.mipmap.favorite_red);

            String s = SingletonControl.getInstance().getDataList().getData().getLikeWallId();
            if (TextUtils.isEmpty(s)) {
                s = wallpaperModel.getImgId();
            } else {
                s = s + "_" + wallpaperModel.getImgId();
            }
            SingletonControl.getInstance().getDataList().getData().setLikeWallId(s);

            if (!AppUtility.isInterNetworkAvailable(this)) {
                AppUtility.showInterNetworkDialog(this, new InternetNetworkInterface() {
                    @Override
                    public void onOkClick() throws Exception {
                        finish();
                    }

                });
                return;
            } else {
                RetroApiRequest.setLikeRetro(this, wallpaperModel.getImgId(), "0", "0", new RetroCallbacks() {
                    @Override
                    public void onDataSuccess(IModel result) throws JSONException {
                        dismissLikeLoading();
                        Toast.makeText(WallpaperDetailsActivity.this, getString(R.string.set_fav_succ), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDataError(String result) throws Exception {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void dislikeWallpaper() {

        isLikedWallpaper = false;
        binding.imgLikeWallpaper.setImageResource(R.mipmap.favorite_white);

        try {
            String s = SingletonControl.getInstance().getDataList().getData().getLikeWallId();
            if (!TextUtils.isEmpty(s)) {
                List<String> arr = new LinkedList<String>(Arrays.asList(s.split("_")));

                String newString = null;
                for (int i = 0; i < arr.size(); i++) {

                    if (arr.get(i).equals(wallpaperModel.getImgId())) {

                    } else {
                        if (TextUtils.isEmpty(newString)) {
                            newString = arr.get(i);
                        } else {
                            newString = newString + "_" + arr.get(i);

                        }
                    }
                }
                SingletonControl.getInstance().getDataList().getData().setLikeWallId(newString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, null);
            return;
        }
        RetroApiRequest.setLikeRetro(this, wallpaperModel.getImgId(), "1", "0", new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                dismissLikeLoading();
                Toast.makeText(WallpaperDetailsActivity.this, getString(R.string.remove_fav), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataError(String result) throws Exception {
            }
        });
    }

    private String getDownloadPath() {
        return AppUtility.getSavedWallpaperPath();
    }

    public void showLoadingDialog(Context c) {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(c, R.style.CustomDialog);
        View customLayout = null;

        LayoutInflater li = LayoutInflater.from(c);
        // set the custom layout
        customLayout = li.inflate(R.layout.progress_layout, null);

        builder.setView(customLayout);
        progressIndicator = customLayout.findViewById(R.id.progress_download);
        progressIndicator.setProgress(0.0, 100.0);
        progressIndicator.setCurrentProgress(0.0);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    public void hideLoadingDialog() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    static CircularProgressIndicator progressIndicator;

    private void downloadFile(String url, String dirPath, String fileName) {
        if (!isPermissionGranted())
            return;

        if (isedited && editedBitmap != null) {
            LoggerCustom.erorr("ThumbActivity", "EditImage");
            File file;
            if (isFromGallery) {
                edited_dst = dirPath + "/" + "Edited" + "_" + System.currentTimeMillis() + ".jpg";
            } else {
                edited_dst = dirPath + "/" + "Edited" + "_" + wallpaperModel.getImgPath();
            }
            file = new File(edited_dst);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                editedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                if (file != null && file.exists()) {
                    doAction(false);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LoggerCustom.erorr("ThumbActivity", "ThumbImage");
            File file = null;
            if (isFromGallery) {
                destination = wallpaperModel.getImgPath();
                file = new File(wallpaperModel.getImgPath());
            } else {
                destination = dirPath + "/" + fileName;
                file = new File(dirPath + "/" + fileName);
            }

            if (file != null && file.exists()) {
                doAction(false);
                return;
            }
            isAddShown = false;
            FirebaseEventManager.sendEventFirebase("DownLoad", "Wallpaper", "detail");
            initializeDownload(destination, url);
        }
    }

    private void initializeDownload(String pathToDownload, String downloadURL) {

        showLoadingDialog(this);
        downloadManagerThin = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

        RetryPolicy retryPolicy = new DefaultRetryPolicy();
        Uri downloadUri = Uri.parse(downloadURL);
        Uri destinationUri = Uri.parse(pathToDownload);
        final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setRetryPolicy(retryPolicy)
                .setDownloadContext("Download1")
                .setStatusListener(downloadStatusListener);

        if (downloadManagerThin.query(downloadId) == com.thin.downloadmanager.DownloadManager.STATUS_NOT_FOUND) {
            downloadId = downloadManagerThin.add(downloadRequest1);
        }
    }

    class DownloadStatusListener implements DownloadStatusListenerV1 {

        @Override
        public void onDownloadComplete(DownloadRequest request) {
            final int id = request.getDownloadId();
            if (id == downloadId) {
                hideLoadingDialog();
                AppUtility.scanToGallery(WallpaperDetailsActivity.this, destination);

                String s = inAppPreference.getDownloadId();

                if (TextUtils.isEmpty(s)) {
                    s = wallpaperModel.getImgId();
                }
                s = s + "," + wallpaperModel.getImgId();
                inAppPreference.setDownloadId(s);
                doAction(true);
            }
        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            try {
                final int id = request.getDownloadId();
                if (id == downloadId) {
                    hideLoadingDialog();
                    Toast.makeText(WallpaperDetailsActivity.this, getString(R.string.unable_download) + errorMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();

            System.out.println("######## onProgress ###### " + id + " : " + totalBytes + " : " + downloadedBytes + " : " + progress);
            if (id == downloadId) {
                progressIndicator.setCurrentProgress(progress);
            }
        }
    }

    private void doAction(boolean isAdShown) {
        if (actionType == 0) {
            showSnackBar(isAdShown);
            binding.rlProgress.setVisibility(View.GONE);
        } else if (actionType == 1) {
            if (isedited) {
                AppUtility.sharedWallpaper(this, "", edited_dst, "", false);
            } else {
                AppUtility.sharedWallpaper(this, "", destination, "", false);
            }
        } else if (actionType == 2) {
            if (isedited) {
                setDynamicWall(edited_dst);
            } else {
                setDynamicWall(destination);
            }
        }
    }




    public void setDynamicWall(String dst) {
        if (isFinishing())
            return;
        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, null);
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());


        Glide.with(this)
                .asBitmap()
                .load(dst).apply(new RequestOptions().override(screenWidth, screenHeight)).centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    if (wallpaperManager == null)
                                        return;

                                    if (setAs == 0) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            if (resource != null && wallpaperManager != null)
                                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM);
                                        } else {
                                            if (resource != null)
                                                wallpaperManager.setBitmap(resource);
                                            else {
                                                InputStream ins = new URL("file://" + dst).openStream();
                                                wallpaperManager.setStream(ins);
                                            }
                                        }

                                    } else if (setAs == 1) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            if (resource != null && wallpaperManager != null)
                                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                                        } else {
                                            if (resource != null)
                                                wallpaperManager.setBitmap(resource);
                                            else {
                                                InputStream ins = new URL("file://" + dst).openStream();
                                                wallpaperManager.setStream(ins);
                                            }
                                        }
                                    } else if (setAs == 2) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            if (resource != null && wallpaperManager != null)
                                                wallpaperManager.setBitmap(resource, null, true);
                                        } else {
                                            if (resource != null)
                                                wallpaperManager.setBitmap(resource);
                                            else {
                                                InputStream ins = new URL("file://" + dst).openStream();
                                                wallpaperManager.setStream(ins);
                                            }
                                        }
                                    } else if (setAs == 3) {
                                        setSystemWallpaper();
                                        return;
                                    }
                                    try {
                                        sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.rlProgress.setVisibility(View.GONE);
//                                            showDownloadedAd(700);
//                                            fromSetAsDialog=false;
                                            showFinalDialog(getString(R.string.wall_applied), "", false);
//                                            Toast.makeText(ThumbImageActivity.this, getString(R.string.wall_applied), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void setSystemWallpaper() {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(destination)), "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("mimeType", "image/*");
        startActivity(Intent.createChooser(intent, getString(R.string.setas)));
    }

    private void showSnackBar(boolean isShowAd) {
        String path = Environment.DIRECTORY_PICTURES + "/" + AppUtility.NEW_FOLDER_NAME;
        if (isShowAd)
            showFinalDialog(getResources().getString(R.string.download_succ), getResources().getString(R.string.download_succ_des) + " : " + path, true);
        else
            showFinalDialog(getResources().getString(R.string.download_already), getResources().getString(R.string.download_succ_des) + " : " + path, true);
    }

    private void showFinalDialog(String tittle, String msg, boolean isFromDownload) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R && !isFromDownload) {
            Toast.makeText(this, tittle + " " + msg, Toast.LENGTH_SHORT).show();
            isAdForceOnBack = true;
        } else {
            try {
                new WallAppliedDialog.Builder(this)
                        .setTitle(tittle)
                        .setMessage(msg)
                        .setTitleTextColor(R.color.white)
                        .setDescriptionTextColor(R.color.white)
                        .setPositiveBtnBackground(R.color.white)
                        .setPositiveBtnText("Ok")
                        .setGifResource(R.drawable.gif_success)
                        .isCancellable(false)
                        .OnPositiveClicked(new WallAppliedDialogListener() {
                            @Override
                            public void OnClick() {
                                showDownloadedAd(0);
                            }
                        })
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDownloadedAd(int time) {
//        if (BlackWallpaperApplication.isAdOnDownload) {
        if (isAddShown)
            return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing())
                    return;
                showAdForcefull(false);
            }
        }, time);
//        } else {
//            isAdForceOnBack = true;
//        }
    }

    private void showAdForcefull(boolean needToFinish) {
        try {
            if (needToFinish)
                finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFadeINButton(View v) {
        LoggerCustom.erorr("ThumbActivity", "setFadeINButton");
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                LoggerCustom.erorr("ThumbActivity", "onAnimationEnd");
//                findViewById(R.id.thumbTool).setVisibility(View.VISIBLE);
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

    public void setFadeOutButton(View v) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        inAppPreference = null;
        actionType = 0;
        wallpaperModel = null;
        downloadManagerThin = null;
        isLikedWallpaper = false;
        downloadUrl = null;
        setAs = 0;
        destination = null;
        downloadStatusListener = null;
        downloadId = 0;
        isImageLoaded = false;
        bottomSheetDialog = null;
        orginalImg = null;
        editedBitmap = null;
    }

    public void expandView(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.expand_height));
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapseView(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void showDeleteDialog(boolean isDisable) {

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setCancelable(true);

        if (isDisable) {
            builder.setMessage(getString(R.string.disable_txt));

            builder.setTitle(getString(R.string.dis_dialog))

                    .setPositiveButton(getString(R.string.yes_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            DisableToServer();
                        }
                    });
        } else {
            builder.setMessage(getString(R.string.delete_txt));

            builder.setTitle(getString(R.string.del_dialog))

                    .setPositiveButton(getString(R.string.yes_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            deleteToServer();
                        }
                    });
        }
        builder.setNegativeButton(getString(R.string.no_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteToServer() {
        LoggerCustom.erorr("ThumbImageActivity", wallpaperModel.getImgPath());
        RetroApiRequest.deleteImgRetro(this, wallpaperModel.getImgId(), new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                finish();
            }

            @Override
            public void onDataError(String result) throws Exception {
            }
        });
    }

    private void DisableToServer() {
        LoggerCustom.erorr("ThumbImageActivity", wallpaperModel.getImgPath());
        RetroApiRequest.disableImageRetro(this, wallpaperModel.getImgId(), new RetroCallbacks() {
            @Override
            public void onDataSuccess(IModel result) throws JSONException {
                finish();
            }

            @Override
            public void onDataError(String result) throws Exception {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggerCustom.erorr("ThumbImageActivity", "onResume");
    }
}
