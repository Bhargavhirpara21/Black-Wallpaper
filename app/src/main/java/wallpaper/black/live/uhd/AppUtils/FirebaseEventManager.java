package wallpaper.black.live.uhd.AppUtils;

import android.os.Bundle;
import android.text.TextUtils;

import wallpaper.black.live.uhd.BlackApplication;

public class FirebaseEventManager {

    public static String LBL_HOME_TAGS="Popular Tags";
    public static String LBL_Main="Main Screen";
    public static String LBL_DOWNLOAD="Download Wallpaper";

    public static String ATR_KEY_HOME="Home Screen";
    public static String ATR_VALUE_HOME="Home Click";

    public static String ATR_KEY_LIVE="Live Screen";
    public static String ATR_VALUE_LIVE="Live Click";

    public static String ATR_KEY_CATEGORY="Category Screen";
    public static String ATR_VALUE_CATEGORY="Category Click";
    public static String ATR_KEY_MENU="Menu Screen";
    public static String ATR_VALUE_MENU="Menu Click";
    public static String ATR_VALUE_FAVOURITE="Favourite Click";
    public static String ATR_VALUE_DOWNLOADS="Downloads Click";
    public static String ATR_VALUE_SHARE_APP="Share App Click";
    public static String ATR_VALUE_RATE_US="Rate Us Click";
    public static String ATR_VALUE_WALL_NOT_WORKING="Wallpaper not working Click";
    public static String ATR_VALUE_POLICY="Privacy Policy Click";
    public static String ATR_VALUE_SUPPORT="Support Click";

    public static String LBL_CATEGORY="Category Screen";
    public static String LBL_FILTER="Filter";
    public static String LBL_PERMISSION="Permission";
    public static String LBL_PITCH_BLACK="Pitch Black Screen";
    public static String LBL_GALLERY="Gallery Screen";
    public static String LBL_STOCK_WALLPAPER="Stock Wallpaper Screen";
    public static String LBL_BLACK_SECREEN="Black Screen";
    public static String LBL_PURE_BLACK="Pure Black Wallpaper";
    public static String LBL_DOUBLE_WALLPAPER="Double Wallpaper";
    public static String LBL_AUTO_CHANGER="Auto Wallpaper changer";

    public static String ATR_KEY_CATEGORY_SELECTED="Selected Category";
    public static String ATR_KEY_CATEGORY_SELECTED_CAROUSL="Selected Category Carousal";
    public static String ATR_KEY_STOCK_SELECTED="Selected STOCK";
    public static String ATR_KEY_STOCK_CATEGORY="Open STOCK";
    public static String ATR_KEY_BLACK_SCREEN="Open BLACKSCREEN";
    public static String ATR_KEY_SEARCH_SCREEN="Search";


    public static String LBL_SET_AS="Set As Screen";

    public static String ATR_KEY_SET_HOME="Home Screen Btn";
    public static String ATR_KEY_SET_LOCK="Lock Screen Btn";
    public static String ATR_KEY_SET_BOTH="Home & Lock Screen Btn";
    public static String ATR_KEY_SET_SYSTEM="system Btn";

    public static String ATR_VALUE_CLICK="Click";

    public static String LBL_THUMB_IMG="ThumbImage Screen";

    public static String ATR_KEY_DOWNLOAD="Download Click";
    public static String ATR_KEY_SET="Set Wallpaper Click";
    public static String ATR_KEY_FAVOURITE="Favourite Click";
    public static String ATR_KEY_SHARE="Share Wallpaper Click";
    public static String ATR_VALUE_CATEGORY_ID="Category id : ";
    public static String LBL_VIDEO="Video Screen";


    public static void sendEventFirebase(String eventLable, String attributeKey, String attributeValue){
        LoggerCustom.erorr("EventManager", "sendEvent Lable:" + eventLable + " :: Key:" + attributeKey + " :: Value:" + attributeValue);
        try {
            if (TextUtils.isEmpty(attributeKey)) {
                Bundle bundle = new Bundle();
                BlackApplication.getBlackApplication().getFirebaseAnalytics().logEvent(replaceSpace(eventLable), bundle);
            } else {
                if ( BlackApplication.getBlackApplication() != null) {
                    if(!eventLable.equalsIgnoreCase(LBL_FILTER)){
                        Bundle bundle = new Bundle();
                        bundle.putString(replaceSpace(attributeKey), replaceSpace(attributeValue));
                        BlackApplication.getBlackApplication().getFirebaseAnalytics().logEvent(replaceSpace(eventLable), bundle);
                    }
                }
            }
        } catch (Exception e) {
            LoggerCustom.printStackTrace(e);
        }
    }
    private static String replaceSpace(String value){
        return value.replace(" ","_");
    }

}
