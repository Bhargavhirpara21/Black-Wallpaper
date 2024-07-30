package wallpaper.black.live.uhd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import org.json.JSONException;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.Model.DataListClass;
import wallpaper.black.live.uhd.Model.DataListModel;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.databinding.ActivitySplashScreenBinding;
import wallpaper.black.live.uhd.notifier.EventNotifier;
import wallpaper.black.live.uhd.notifier.EventState;
import wallpaper.black.live.uhd.notifier.EventTypes;
import wallpaper.black.live.uhd.notifier.IEventListener;
import wallpaper.black.live.uhd.notifier.ListenerPriority;
import wallpaper.black.live.uhd.notifier.NotifierFactory;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class SplashScreenActivity extends AppCompatActivity implements RetroCallbacks, IEventListener {

    private InAppPreference inAppPreference;
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inAppPreference = InAppPreference.getInstance(SplashScreenActivity.this);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
            splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
                @Override
                public boolean shouldKeepOnScreen() {
                    return false;
                }
            });
        }
        setContentView(binding.getRoot());

        if(getSupportActionBar()!=null)
            this.getSupportActionBar().hide();

        if (!AppUtility.isInterNetworkAvailable(this)) {
            try {
                AppUtility.showInterNetworkDialog(this, new InternetNetworkInterface() {
                    @Override
                    public void onOkClick() throws Exception {
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            registerSilentLoginInfoListener();
            if (BlackApplication.getBlackApplication() != null) {
//                BlackWallpaperApplication.getBlackLyAppContext().resetLastAdTime();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BlackApplication.getBlackApplication().getReferrerSource();
                    }
                });
            }

        }
        BlackApplication.getBlackApplication().isAppRunning = true;

    }

    private void registerSilentLoginInfoListener() {
        EventNotifier notifier =
                NotifierFactory.getInstance().getNotifier(
                        NotifierFactory.EVENT_NOTIFIER_USER_INFO);
        notifier.registerListener(this, ListenerPriority.PRIORITY_HIGH);
    }

    private void unregisterSilentLoginInfoListener() {
        EventNotifier notifier =
                NotifierFactory.getInstance().getNotifier(
                        NotifierFactory.EVENT_NOTIFIER_USER_INFO);
        notifier.unRegisterListener(this);
    }

    @Override
    public int eventNotify(int eventType, final Object eventObject) {
        int eventState = EventState.EVENT_IGNORED;
        switch (eventType) {

            case EventTypes.EVENT_REFERER_GOT:
                eventState = EventState.EVENT_PROCESSED;
                getUserData();
                break;

            case EventTypes.EVENT_USER_INFO_ERROR:
                eventState = EventState.EVENT_PROCESSED;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String result = null;
                        try {
                            result = (String) eventObject;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(result))
                            result = getString(R.string.server_error);
                        showDataRetryDialog(getString(R.string.info), result);
                    }
                });

                break;
        }
        return eventState;
    }


    private void showDataRetryDialog(final String title, final String msg) {

        AppUtility.showDialogUser(this, title, msg, getString(R.string.retry), "", false, new AppUtility.DialogOnButtonClickListener() {
            @Override
            public void onOKButtonCLick() {
                if (AppUtility.isInterNetworkAvailable(SplashScreenActivity.this)) {
                    getUserData();
                } else
                    showDataRetryDialog(title, msg);
            }

            @Override
            public void onCancelButtonCLick() {
            }
        });
    }

    private void getUserData() {

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, null);
        } else {
            if (inAppPreference.isLoggedIn()) {
                RetroApiRequest.loginApiRetro(this, inAppPreference.getUserId(), this);
            } else {
                RetroApiRequest.loginApiRetro(this, "", this);
            }
        }
    }

    boolean isFirstTime;

    @Override
    public void onDataSuccess(IModel result) throws JSONException {
        if (result != null) {

            DataListModel model = (DataListModel) result;

            if (model.getStatus() == 1) {

                DataListClass obj = model.getData();
                if (inAppPreference == null)
                    inAppPreference = InAppPreference.getInstance(this);
                inAppPreference.setUserId(obj.getUserId());
                inAppPreference.setCountryCode(obj.getCountryCode());
                inAppPreference.setLogIn(true);
                inAppPreference.setIsPro(obj.getIs_pro());
                inAppPreference.setImgUrl(model.getLogic().getImgPath());

                SingletonControl.getInstance().setDataListModel(model);
                SingletonControl.getInstance().setCategoryList(obj.getCategory());
                SingletonControl.getInstance().setStockcategoryList(obj.getCategory_stock());
                LoggerCustom.erorr("TAG", "onSuccess");
                callMainActivity();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoggerCustom.erorr("Splash", "resultCode:" + resultCode + " requestCode:" + requestCode);
        if (requestCode == 100) {
            activityVisible = true;
            if (resultCode == RESULT_OK) {
                new Handler().post(new Runnable() {
                    @Override
                        public void run() {
                            resultOnAct();
                        }
                    });
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void resultOnAct() {
        inAppPreference.setFirstTimeLaunch(false);
        isFirstTime = false;
        callMainActivity();
    }

    @Override
    public void onDataError(String result) throws Exception {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSilentLoginInfoListener();
        inAppPreference = null;
    }

    //    private boolean isContinue;
    private boolean isBypassed;
    private boolean activityVisible = true;

    private void callMainActivity() {
        LoggerCustom.erorr("TAG", "callMainActivity:" + activityVisible);
        if (!activityVisible) {
            isContinue = true;
            return;
        }
        if (isFinishing())
            return;

        Intent intent = new Intent(this, MainActivity.class);
        if (getIntent() != null && getIntent().getExtras() != null)
            intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        BlackApplication.getBlackApplication().onPause(this);
        activityVisible = false;
    }

    private boolean isContinue;

    @Override
    protected void onResume() {
        super.onResume();
        BlackApplication.getBlackApplication().onResume(this);
        activityVisible = true;
        if (isBypassed) {
            isBypassed = false;
            Intent intent = new Intent(this, MainActivity.class);
            if (getIntent() != null && getIntent().getExtras() != null)
                intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        } else if (isContinue) {
            isContinue = false;
            callMainActivity();
        }
    }
}
