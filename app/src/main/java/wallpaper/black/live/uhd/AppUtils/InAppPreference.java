package wallpaper.black.live.uhd.AppUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class InAppPreference {
    private static final String VIDEO_LIVE_WALLPAPER = "VIDEOWALLPATH";
    private static final String VIDEO_LIVE_WALLPAPER_TEMP = "VIDEOWALLPATHETMP";
    private static final String PRE_CAT_VIEW_COUNT = "PRE_CAT_VIEW_COUNT";
    private static InAppPreference store;
    private final SharedPreferences SP;
    private static final String filename = "Black_Preference";
    String isLoggedIn = "isLoggedIn";
    String UserId = "UserId";
    String fcm_token = "fcmToken";
    String fcm_token_send = "fcmTokenSend";
    String imgUrl = "imgUrl";
    String APP_LANGEUGE = "app_lang";
    String downloadId = "downloadId";
    private static final String IS_REFFERER_EVENT_SEND = "REFERER_SEND";
    private static final String REFFERAL_ID = "REFFERAL_ID";
    private static final String TAGS = "KEYWORD_TAG";
    private String IS_ENABLE_SERVICE = "service";
    private String AUTO_WALLPAPER_TIME = "auto_time";
    private String COUNTRY_CODE = "country_code";
    private static final String LAST_AUTO_CHANGE_TIME = "Last_Auto_Change_Time";
    public static final String WALLSCREEN = "WALLSCREEN";
    private String NOTIFICATION = "notification";

    private InAppPreference(Context context) {
        SP = context.getApplicationContext().getSharedPreferences(filename, 0);
    }

    public static InAppPreference getInstance(Context context) {
        if (store == null) {
            store = new InAppPreference(context);
        }
        return store;
    }
    public void setIsReferSend(boolean val) {
        SP.edit().putBoolean(IS_REFFERER_EVENT_SEND, val).commit();
    }


    public boolean getIsReferSend() {
        return SP.getBoolean(IS_REFFERER_EVENT_SEND, false);
    }

    public void setReferId(String val) {
        SP.edit().putString(REFFERAL_ID, val).commit();
    }


    public String getReferId() {
        return SP.getString(REFFERAL_ID, "");
    }

    public String getImgUrl() {
        String s = SP.getString(imgUrl, "");
        return s;
    }

    public String getKeywordTags() {
        return SP.getString(TAGS, "");
    }
    public void setKeywordTags(String val) {
        SP.edit().putString(TAGS, val).commit();
    }


    public void setCountryCode(String val) {
        SP.edit().putString(COUNTRY_CODE, val).commit();
    }

    public String getCountryCode() {
        return SP.getString(COUNTRY_CODE, "");
    }


    public void setCatViewCount(String val) {
        SP.edit().putString(PRE_CAT_VIEW_COUNT, val).commit();
    }

    public String getCatViewCount() {
        return SP.getString(PRE_CAT_VIEW_COUNT, "");
    }

    public String getVideoLiveWallpaperPath() {
        String s = SP.getString(VIDEO_LIVE_WALLPAPER, "");
        LoggerCustom.erorr("preference", s);
        return s;
    }

    public void setVideoLiveWallpaperPath(String value) {
        SP.edit().putString(VIDEO_LIVE_WALLPAPER, value).commit();
    }

    public void setImgUrl(String Url) {
        Url="https://njapplications.com/BlackWall/Assets/";
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(imgUrl, Url);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return SP.getBoolean(isLoggedIn, false);
    }

    public void setLogIn(boolean n) {
        SharedPreferences.Editor editor = SP.edit();
        editor.putBoolean(isLoggedIn, n);
        editor.commit();
    }

    public String getUserId() {

        String s = SP.getString(UserId, "");
        Log.e("Keystore", "in pref get user" + s);
        return s;
    }

    public void setUserId(String num) {
        Log.e("Keystore", "in pref set user" + num);
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(UserId, num);
        editor.commit();
    }

    public String getFCM() {
        return SP.getString(fcm_token, "");
    }

    public void setFCM(String num) {
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(fcm_token, num);
        editor.commit();
    }

    public String getDownloadId() {
        return SP.getString(downloadId, "");
    }

    public void setDownloadId(String num) {
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(downloadId, num);
        editor.commit();
    }

    public String getVideoLiveWallpaperPathTemp() {
        return SP.getString(VIDEO_LIVE_WALLPAPER_TEMP, "");
    }

    public void setVideoLiveWallpaperPathTemp(String value) {
        SP.edit().putString(VIDEO_LIVE_WALLPAPER_TEMP, value).commit();
    }

    public void setChangeTime(int val) {
//        Logger.e("preference set time",val+"");
        SP.edit().putInt(AUTO_WALLPAPER_TIME, val).commit();
    }

    public int getChangeTime() {
        int i = SP.getInt(AUTO_WALLPAPER_TIME, 1);
//        Logger.e("preference get time",i+"");
        return i;
    }

    public void setServiceEnable(boolean val) {
        LoggerCustom.erorr("preference", val + " :set service");
        SP.edit().putBoolean(IS_ENABLE_SERVICE, val).commit();
    }

    public boolean isServiceEnable() {
        boolean s = SP.getBoolean(IS_ENABLE_SERVICE, false);
        LoggerCustom.erorr("preference", s + " :get service");
        return s;
    }

    public void setFCMTokenSend(boolean val) {
        SP.edit().putBoolean(fcm_token_send, val).commit();
    }

    public boolean isFCMTokenSend() {
        boolean s = SP.getBoolean(fcm_token_send, false);
        return s;
    }

    public String getSaveResponse(String OperationID) {
        return SP.getString(OperationID, "");
    }

    public void setSaveResponse(String OperationID, String value) {
        SP.edit().putString(OperationID, value).commit();
    }

    public void setLastAutoChangedTime() {
        long time = Calendar.getInstance().getTimeInMillis();
        Log.i("LastAutoChangedTime", "LastAutoChangedTime:::" + time);
        SP.edit().putLong(LAST_AUTO_CHANGE_TIME, time).commit();
    }


    public long getLastAutoChangedTime() {
        return SP.getLong(LAST_AUTO_CHANGE_TIME, 0);
    }

    private static final String AUTO_WALL_TEMP = "AUTO_WALL_TEMP";
    public void setSelectedImageTemp(String val) {
        SP.edit().putString(AUTO_WALL_TEMP, val).commit();
    }

    public String getSelectedImageTemp() {
        return SP.getString(AUTO_WALL_TEMP, "");
    }

    private static String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public void setFirstTimeLaunch(boolean isFirstTime) {
        LoggerCustom.erorr("pref : ", "" + isFirstTime);
        SP.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).commit();
    }

    public boolean isFirstTimeLaunch() {
//        Logger.e("pref : ", "" + SP.getBoolean(IS_FIRST_TIME_LAUNCH, true));
        return SP.getBoolean(IS_FIRST_TIME_LAUNCH, true);
//        return true;
    }


    public int getWallScreen() {
        return SP.getInt(WALLSCREEN, 0);
    }

    public void setWallScreen(int val) {
        SP.edit().putInt(WALLSCREEN, val).commit();
    }

    public void setSelLang(String val) {
        SP.edit().putString(APP_LANGEUGE, val).commit();
    }

    public String getSelLang() {
        return SP.getString(APP_LANGEUGE, "");
    }

    private String PRE_IS_PRO= "is_pro";
    public void setIsPro(boolean val) {
        SP.edit().putBoolean(PRE_IS_PRO, val).commit();
    }
    public boolean getIsPro() {
//        return  true;
        return SP.getBoolean(PRE_IS_PRO, false);
    }

    private String PRE_OLD_DEVICE_ID = "OLD_DEVICE_ID";
    public void setOldDeviceID(String val) {
        SP.edit().putString(PRE_OLD_DEVICE_ID, val).commit();
    }

    public String getOldDeviceID() {
        return SP.getString(PRE_OLD_DEVICE_ID, "");
    }

    public void setNotification(boolean val) {
        SP.edit().putBoolean(NOTIFICATION, val).commit();
    }

    public boolean getNotification() {
        return SP.getBoolean(NOTIFICATION, true);
    }
}
