package wallpaper.black.live.uhd.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.Model.DataListModel;
import wallpaper.black.live.uhd.Model.DoubleWallData;
import wallpaper.black.live.uhd.Model.FavouriteListModel;
import wallpaper.black.live.uhd.Model.IModelBase;
import wallpaper.black.live.uhd.Model.WallpaperListModel;

public interface RetroApiInterface {

    @POST(Constants.URL_GET_LIKE_WALLPAPER)
    Call<FavouriteListModel> getLikedWallList(@QueryMap Map<String, String> param);

    @POST(Constants.SET_FAVOURITE)
    Call<FavouriteListModel> setLikedWallList(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_HOME_DATA_lang)
    Call<Object> getHomeDataNewList(@QueryMap Map<String, String> param);

    @POST(Constants.URL_USER_UPDATE)
    Call<IModelBase> getInAppPurchaseData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_DISABLE_VIDEO)
    Call<WallpaperListModel> disableVideoList(@QueryMap Map<String, String> param);


    @POST(Constants.URL_DISABLE_IMG)
    Call<WallpaperListModel> disableImageList(@QueryMap Map<String, String> param);

    @POST(Constants.URL_DELETE_IMG)
    Call<WallpaperListModel> deleteImageData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_DELETE_DOUBLE_WALL)
    Call<WallpaperListModel> deleteDoubleWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_DELETE_VIDEO)
    Call<WallpaperListModel> deleteVideoWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_UPDATE_TOKEN)
    Call<WallpaperListModel> updateFCMTokenData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_SILENT_LOGIN)
    Call<DataListModel> logInApiData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_SEARCH_TAGS)
    Call<WallpaperListModel> getSearchApiData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_PITCHBLACK_WALLPAPER)
    Call<WallpaperListModel> getPitchBlackWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_DOUBLE_WALLPAPER)
    Call<DoubleWallData> getDoubleWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_AI_WALLPAPER)
    Call<WallpaperListModel> getTrendingWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_REWARDED_WALLPAPER)
    Call<WallpaperListModel> getRewardedWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_AUTO_WALLPAPER)
    Call<WallpaperListModel> getAutoWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_LIVE_WALLPAPER)
    Call<WallpaperListModel> getLiveWallData(@QueryMap Map<String, String> param);

    @POST(Constants.URL_GET_WALLPAPER)
    Call<WallpaperListModel> getWallData(@QueryMap Map<String, String> param);

}
