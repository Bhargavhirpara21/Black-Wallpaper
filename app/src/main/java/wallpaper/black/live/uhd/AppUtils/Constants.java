package wallpaper.black.live.uhd.AppUtils;

import android.graphics.Bitmap;

import wallpaper.black.live.uhd.BuildConfig;

public class Constants {

    public static int ADS_PER_PAGE = 4;
    public static final String URL_PRIVACY_POLICY ="https://njapplications.com/Privacy_Policy_Bck.html";

    public static String smallPath="Small/";
    public static String thumbPath="Thumb/";
    public static String hdPath="UHD/";
    public static String catImgPathQureka="Category/";
    public static String catImgPath_new_img="Category/new_img/";
    public static String catImgPath="Category/new/";
    public static String catImgPathStock="Category/Stock/";
    public static String catImgPathBanner="Category/Banner/";
    public static String catFeaturesImgPath="Category/feature/";
    public static String doubleImgPath="double_uhd/";
    public static String doubleThumbImgPath="double_thumb/";
    public static String homeCatImgPath="Category/small/";
    public static String liveThumbPath="Live/img/";
    public static String liveVideoPath="Live/vid/";
    public static final String Domain = "https://njapplications.com/BlackWall/API/";
    public static final String URL_GET_WALLPAPER= Domain + "wallpaper_list.php";
    public static final String URL_GET_LIVE_WALLPAPER= Domain + "live_wallpaper_list.php";
    public static final String URL_GET_REWARDED_WALLPAPER= Domain + "reward_wallpaper.php";
    public static final String URL_GET_AUTO_WALLPAPER= Domain + "auto_changer_wallpaper.php";
    public static final String URL_GET_TRENDING_WALLPAPER= Domain + "trending_wallpapers.php";
    public static final String URL_GET_AI_WALLPAPER= Domain + "ai_wallpapers.php";
    public static final String URL_GET_PITCHBLACK_WALLPAPER= Domain + "pitchblack_wallpapers.php";
    public static final String URL_GET_SEARCH_TAGS= Domain + "search_wallpapers.php";
    public static final String URL_SILENT_LOGIN= Domain + "silent_login_final.php";
    public static final String URL_GET_LIKE_WALLPAPER= Domain + "get_fav_wall.php";
    public static final String SET_FAVOURITE = Domain + "set_favorite.php";
    public static final String URL_GET_HOME_DATA_lang = Domain + "home_screen_black_new.php";
    public static final String URL_GET_DOUBLE_WALLPAPER = Domain + "double_wallpaper.php";
    public static final String URL_Help =" https://njapplications.com/BlackWall/live_help.html";
    public static final String URL_UPDATE_TOKEN = Domain + "update_device_token.php";
    public static final String URL_DELETE_IMG = "http://njapplications.com/BlackWall/delete_wallpaper.php";
    public static final String URL_DELETE_DOUBLE_WALL = "http://njapplications.com/BlackWall/delete_double_wallpaper.php";
    public static final String URL_DELETE_VIDEO= "http://njapplications.com/BlackWall/delete_live_wallpaper.php";
    public static final String URL_DISABLE_IMG= "http://njapplications.com/BlackWall/disable_wallpaper.php";
    public static final String URL_DISABLE_VIDEO= "http://njapplications.com/BlackWall/disable_live_wallpaper.php";

    public final static String URL_USER_UPDATE = Domain+"update_user.php"; //done

    public static final long[] WALLPAPER_TIME_MILLI = new long[]{60 * 60 * 1000, 2 * 60 * 60 * 1000, 4 * 60 * 60 * 1000, 6 * 60 * 60 * 1000, 12 * 60 * 60 * 1000, 24 * 60 * 60 * 1000};

    public static final boolean IS_DELETE = BuildConfig.DEBUG;
    public static final boolean IS_DEBUG = BuildConfig.DEBUG;
    public static int TILE_COLUMN = 3;
    public static int TILE_COLUMN_PITCH = 2;
    public static final int REFRESH_TIME_OUT = 2000;
    public static boolean isWallSetCancel=true;
    public static final String DOWNLOAD_EXTENSION_VIDEO = ".mp4";
    public static final Bitmap.Config bitmapConfig8888 = Bitmap.Config.ARGB_8888;

    public static final boolean IS_TEST_ID=true;
    public static boolean IS_TEST_AD_ACTIVE = true;
}
