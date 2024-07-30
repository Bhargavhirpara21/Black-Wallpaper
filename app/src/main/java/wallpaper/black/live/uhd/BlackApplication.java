package wallpaper.black.live.uhd;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;

import androidx.appcompat.view.ContextThemeWrapper;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.net.URLDecoder;

import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.notifier.EventNotifier;
import wallpaper.black.live.uhd.notifier.EventTypes;
import wallpaper.black.live.uhd.notifier.NotifierFactory;

public class BlackApplication extends Application {

    public static BlackApplication getBlackApplication() {
        return blackApplication;
    }

    private static BlackApplication blackApplication;
    public static Context bContext = null;
    public boolean isAdClicked,isShareRate;
    public boolean isShowGoogleRate;
    public static boolean isAppRunning = false;
    public static int selectedFilter_h;
    public static int selectedFilter_l;
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            bContext = new ContextThemeWrapper(this, R.style.Theme_BlackWallpaperFinal);
        } catch (Exception e) {
            e.printStackTrace();
            bContext=this;
        }

        blackApplication = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    public FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseAnalytics getFirebaseAnalytics() {
        try {
            if (mFirebaseAnalytics == null)
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFirebaseAnalytics;
    }

    private InstallReferrerClient installReferrerClient;

    public void getReferrerSource() {
        try {
            InAppPreference store = InAppPreference.getInstance(bContext);

            boolean isReferrerEventSend = store.getIsReferSend();
            LoggerCustom.erorr("isReferrerEventSend", "" + isReferrerEventSend);
            if (isReferrerEventSend) {
                EventNotifier notifier = NotifierFactory.getInstance().getNotifier(NotifierFactory.EVENT_NOTIFIER_USER_INFO);
                notifier.eventNotify(EventTypes.EVENT_REFERER_GOT, null);
                return;
            }
            installReferrerClient = InstallReferrerClient.newBuilder(bContext).build();
            installReferrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    try {
                        ReferrerDetails response = installReferrerClient.getInstallReferrer();
                        String referrer = response.getInstallReferrer();

                        try { // Remove any url encoding
                            referrer = URLDecoder.decode(referrer, "UTF-8");
                        } catch (Exception e) {
                            return;
                        }

                        String referrerValue = "Other";
                        if (referrer != null && !referrer.equals("")) {
                            String storedReferrerId = store.getReferId();
                            if (/*!isProcessingStarted &&*/ TextUtils.isEmpty(storedReferrerId)) {
                                store.setReferId(referrer);
                            }

                            if (referrer.contains("Blackly")) {
                                referrerValue = "Old Black";
                            }else if (referrer.contains("F_")) {
                                referrerValue = "Free Referrer";
                            } else if (referrer.contains("organic")) {
                                referrerValue = "Organic";
                            } else if (referrer.contains("not set")) {
                                referrerValue = "Not Set";
                            } else if (referrer.contains("adsplayload")) {
                                referrerValue = "Ads playload";
                            }else if (referrer.length() > 22) {
                                referrerValue = referrer.substring(0, 21);
                            }
                        }

                        String oldDeviceId = "";
                        if (!TextUtils.isEmpty(referrer) && referrer.contains("Blackly")) {
                            oldDeviceId = referrer.replace("Blackly_", "");
                        }
                        store.setOldDeviceID(oldDeviceId);
//                        EventManager.sendEvent(EventManager.LBL_REFERRER_PAGE,
//                                EventManager.ATR_KEY_REFERRER_DETAILS, referrerValue);
                        store.setIsReferSend(true);
                        LoggerCustom.erorr("getReferrerClient", "" + referrerValue);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EventNotifier notifier =
                            NotifierFactory.getInstance().getNotifier(
                                    NotifierFactory.EVENT_NOTIFIER_USER_INFO);
                    notifier.eventNotify(EventTypes.EVENT_REFERER_GOT, null);

                    try {
                        installReferrerClient.endConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            EventNotifier notifier =
                    NotifierFactory.getInstance().getNotifier(
                            NotifierFactory.EVENT_NOTIFIER_USER_INFO);
            notifier.eventNotify(EventTypes.EVENT_REFERER_GOT, null);
            LoggerCustom.printStackTrace(e);
        }
    }

    public void onPause(Activity act) {
    }

    public void onResume(Activity act) {
    }

}
