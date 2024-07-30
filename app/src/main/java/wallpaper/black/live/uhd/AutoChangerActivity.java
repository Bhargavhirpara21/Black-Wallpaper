package wallpaper.black.live.uhd;

import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.BGServices.AutoChangerService;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperListModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.databinding.ActivityAutoChangerBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class AutoChangerActivity extends AppCompatActivity {

    private final String TAG = "AutoWallpaperActivity";
    private WallpaperModel wallpaperModel;
    private InAppPreference inAppPreference;
    private int time_pos;
    private AlertDialog alertLoadingDlg;
    private WallpaperInfo old_info;
    public static boolean isAdForceOnBack;

    ActivityAutoChangerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAutoChangerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(getString(R.string.Auto_wall_change));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            isAdForceOnBack = false;
        }
        inAppPreference = InAppPreference.getInstance(this);
        if (inAppPreference.isServiceEnable()) {
            if (AutoChangerService.isServiceRunning) {
                LoggerCustom.erorr(TAG, "We're already running");
                // Still might be a preview, but the user is already running your LWP.
                binding.enableAutoWall.setText(R.string.update);
                binding.disablebtn.setVisibility(View.VISIBLE);
            } else {
                LoggerCustom.erorr(TAG, "We're not running, this should be a preview");
                binding.enableAutoWall.setText(R.string.apply);
                inAppPreference.setServiceEnable(false);
            }
        } else {
            binding.enableAutoWall.setText(R.string.apply);
        }

        Spinner timeChangeSpinner = binding.timeSpinner;
        timeChangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter timead = new ArrayAdapter(this, R.layout.spinner_item,
                getResources().getTextArray(R.array.auto_time));
        timead.setDropDownViewResource(R.layout.spinner_item);
        timeChangeSpinner.setAdapter(timead);

        int timeType = inAppPreference.getChangeTime();
        timeChangeSpinner.setSelection(timeType);

        binding.enableAutoWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWallpaperData();
            }
        });

        binding.disablebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_AUTO_CHANGER, "Action", "Disable");
                LoggerCustom.erorr("TAG", "in disable========= ");
                if (inAppPreference.isServiceEnable()) {
                    inAppPreference.setServiceEnable(false);
                }
                inAppPreference.setLastAutoChangedTime();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        myWallpaperManager.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                showMessage(getResources().getString(R.string.auto_wall_off), false);
            }
        });

        Glide.with(this).asGif().load(R.raw.auto_change).into(binding.autoImage3);

        try {
            if (Constants.isWallSetCancel) {
                WallpaperManager wpm = WallpaperManager.getInstance(this);
                old_info = wpm.getWallpaperInfo();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLoadingDialog(String text) {

        try {
            final AlertDialog.Builder alert = new AlertDialog.Builder(
                    AutoChangerActivity.this, R.style.CustomDialog);

            View mView = getLayoutInflater().inflate(R.layout.progress_auto, null);
            TextView title = mView.findViewById(R.id.txt_progress_title);
            title.setText(text);
            alert.setView(mView);
            alertLoadingDlg = alert.create();
            alertLoadingDlg.setCanceledOnTouchOutside(false);
            alertLoadingDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertLoadingDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAutoWallpaperChanger() {
        LoggerCustom.erorr("TAG", "Selected position_var : " + time_pos);

        inAppPreference.setChangeTime(time_pos);
        if (!inAppPreference.isServiceEnable()) {
//            showLoadingDialog("Updating Data...");
            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_AUTO_CHANGER, "Action", "Apply");
            String path = inAppPreference.getImgUrl() + Constants.hdPath + wallpaperModel.getImgPath();
            inAppPreference.setSelectedImageTemp(path);
            openAutoWallChanger();
//            setWall(path);
        } else {
            showLoadingDialog(getString(R.string.updating_data));
            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_AUTO_CHANGER, "Action", "Update");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (alertLoadingDlg != null)
                            alertLoadingDlg.dismiss();
                        if (binding.disablebtn != null)
                            binding.disablebtn.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AutoChangerActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                }
            }, 2000);

            LoggerCustom.erorr("AutoWallpaperActivity", "in update time : " + time_pos + "");
        }
    }

    private void getWallpaperData() {
        if (wallpaperModel != null) {
            startAutoWallpaperChanger();
            return;
        }
        if (AppUtility.isInterNetworkAvailable(AutoChangerActivity.this)) {
            showLoadingDialog(getString(R.string.getting_wall));
            RetroApiRequest.getAutoWallpaperChangerRetro(AutoChangerActivity.this, new RetroCallbacks() {
                @Override
                public void onDataSuccess(IModel result) throws JSONException {
                    WallpaperListModel model = (WallpaperListModel) result;

                    if (model.getWallpaper() != null) {
                        List<WallpaperModel> resultList = model.getWallpaper();
                        wallpaperModel = resultList.get(0);
                        Log.e("TAG", "onSuccess: " + result);
                        Log.e("TAG", "onSuccess: wallpaper --> " + wallpaperModel);
                        if (alertLoadingDlg != null)
                            alertLoadingDlg.dismiss();
                        inAppPreference.setLastAutoChangedTime();
                        startAutoWallpaperChanger();
                    }
                }

                @Override
                public void onDataError(String result) throws Exception {
                }
            });
        } else {
            AppUtility.showInterNetworkDialog(AutoChangerActivity.this, new InternetNetworkInterface() {
                @Override
                public void onOkClick() throws Exception {

                }
            });
        }
    }

    private void showMessage(String text, boolean isEnable) {
        Toast.makeText(AutoChangerActivity.this, text, Toast.LENGTH_SHORT).show();
        binding.enableAutoWall.setText(R.string.apply);
        binding.disablebtn.setVisibility(View.GONE);
    }

    private void openAutoWallChanger() {
        try {
            BlackApplication.getBlackApplication().isShareRate = true;
            try {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(AutoChangerActivity.this,
                                AutoChangerService.class));
                startActivityForResult(intent, 200);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    startActivityForResult(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new
                                    ComponentName(AutoChangerActivity.this, AutoChangerService.class))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 200);
                } catch (Exception e2) {
                    Toast.makeText(AutoChangerActivity.this, R.string
                            .toast_failed_launch_wallpaper_chooser, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoggerCustom.erorr("onActivityResult", "requestCode:" + requestCode);
        if (resultCode == RESULT_OK) {
            isAdForceOnBack = true;
            Toast.makeText(AutoChangerActivity.this, getString(R.string.wall_applied), Toast.LENGTH_SHORT).show();
            BlackApplication.getBlackApplication().isShowGoogleRate = true;
        } else if (resultCode == RESULT_CANCELED) {
            try {
                if (Constants.isWallSetCancel) {
                    WallpaperManager wpm = WallpaperManager.getInstance(this);
                    WallpaperInfo info = wpm.getWallpaperInfo();
                    if (info == null) {
                        return;
                    }
                    if (old_info != null) {
                        LoggerCustom.erorr("onActivityResult", "Old:" + old_info.getComponent().getClassName());
                    }

                    if (info != null && (old_info == null || (!old_info.getComponent().getClassName().equals(info.getComponent().getClassName())
                            && info.getComponent().getClassName().equals(AutoChangerService.class.getCanonicalName())))) {
                        LoggerCustom.erorr("onActivityResult", "canceled: dst");
                        isAdForceOnBack = true;
                        Toast.makeText(AutoChangerActivity.this, getString(R.string.wall_applied), Toast.LENGTH_SHORT).show();
                        BlackApplication.getBlackApplication().isShowGoogleRate = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            BlackApplication.getBlackApplication().isShareRate = false;
            if (inAppPreference.isServiceEnable()) {
                if (AutoChangerService.isServiceRunning) {
                    LoggerCustom.erorr(TAG, "We're already running");
                    // Still might be a preview, but the user is already running your LWP.
                    binding.enableAutoWall.setText(R.string.update);
                    binding.disablebtn.setVisibility(View.VISIBLE);
                } else {
                    LoggerCustom.erorr(TAG, "We're not running, this should be a preview");
                    binding.enableAutoWall.setText(R.string.apply);
                    inAppPreference.setServiceEnable(false);
                }
            } else {
                binding.enableAutoWall.setText(R.string.apply);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wallpaperModel = null;
        inAppPreference = null;
        alertLoadingDlg = null;
    }
}
