package wallpaper.black.live.uhd.retrofit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.GsonUtility;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.BlackApplication;
import wallpaper.black.live.uhd.Model.BannerModel;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.DataListModel;
import wallpaper.black.live.uhd.Model.DoubleWallData;
import wallpaper.black.live.uhd.Model.FavouriteListModel;
import wallpaper.black.live.uhd.Model.HomeNewModel;
import wallpaper.black.live.uhd.Model.IModelBase;
import wallpaper.black.live.uhd.Model.WallpaperListModel;

import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.SingletonControl;

public class RetroApiRequest {

    private static final String TAG = "ApiRequest";
    public static final String GET_USER_INFO_OPERATION_ID = "silent_login";
    public static final String GET_wallpaper_list = "wallpaper_list";
    public static final String GET_live_wallpaper_list = "live_wallpaper_list";
    public static final String GET_trending_wallpapers = "trending_wallpapers";
    public static final String GET_pitchblack_wallpapers = "pitchblack_wallpapers";
    public static final String GET_get_fav_wall = "get_fav_wall";
    public static final String GET_home_screen_new = "home_screen_new";
    public static final String GET_double_wallpaper = "double_wallpaper";


    //AppConstant.URL_GET_HOME_DATA_lang;
    public static void getHomeDataNewRetro(Context c, RetroCallbacks callback) {
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);

        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<Object> mediaDetails = RetrofitInstance.getInstance().apiInterface.getHomeDataNewList(params);

        mediaDetails.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                String responseBody = response.body().toString();
                LoggerCustom.erorr("hiiii Respone is here", responseBody);
                preferenceStore.setSaveResponse(GET_home_screen_new, responseBody);
//                HomeCheckModelClass conversationListModel = new HomeCheckModelClass();
                Gson gson = GsonUtility.getInstant();
                Object mUser = response.body();
                String json = new Gson().toJson(mUser);
                HomeNewModel homeNewModel = new Gson().fromJson(json, HomeNewModel.class);

                try {
//                    Gson gson = GsonUtil.getInstant();// new Gson();
//                    HomeNewModel conversationListModel = gson.fromJson(responseBody, HomeNewModel.class);

//                    conversationListModel = response.body();

                    HashMap<String, List<Object>> listHashMap = new LinkedHashMap<>();
                    Log.e(TAG, "onResponse: json --> "+json );
                    JSONObject jsonObject = new JSONObject(json);
                    Log.e(TAG, "onResponse: jsonObject --> "+jsonObject );
                    JSONArray jsonArray = jsonObject.getJSONArray("HomeData");

                    //This should be the iterator you want.
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject temp = jsonArray.getJSONObject(i);
                        Iterator<String> iter = temp.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            String tempJSONArray = temp.getString(key);
                            LoggerCustom.erorr("key", key);
                            LoggerCustom.erorr("tempJSONArray", tempJSONArray);
//                                    listHashMap.put(key,iter)
                            JSONArray jsonArray1 = new JSONArray(tempJSONArray);
                            List<Object> posts = null;
                            if (jsonArray1.getJSONObject(0).has("type_id")) {
                                Type listType = new TypeToken<List<BannerModel>>() {
                                }.getType();
                                posts = gson.fromJson(tempJSONArray, listType);
                            } else if (jsonArray1.getJSONObject(0).has("is_feature")) {
                                Type listType = new TypeToken<List<CategoryDataModel>>() {
                                }.getType();
                                posts = gson.fromJson(tempJSONArray, listType);
                            } else {
                                Type listType = new TypeToken<List<WallpaperModel>>() {
                                }.getType();
                                posts = gson.fromJson(tempJSONArray, listType);
                            }
                            listHashMap.put(key, posts);
                        }
                    }
                    homeNewModel.setListHashMap(listHashMap);

                    callback.onDataSuccess(homeNewModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//E/hiiii Respone is here: {"status":1,"msg":"Home Data","HomeData":[{"Feature Category":[{"category_id":"21","Name":"Anim","type":"static","image_path":"anime.png","img_path_feature":"anime.jpg","is_event":"0","img_count":"1","Priority":"13","country_code":"","is_active":"1","i
//E/hiiii Respone is here: {status=1.0, msg=Home Data, HomeData=[{Feature Category=[{category_id=21, Name=Anim, type=static, image_path=anime.png, img_path_feature=anime.jpg, is_event=0, img_count=1, Priority=13, country_code=, is_active=1, is_feature=1, tags=Anime#anime#Anim#anime art,


            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                String response = preferenceStore.getSaveResponse(GET_home_screen_new);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Gson gson = GsonUtility.getInstant();// new Gson();
                        HomeNewModel conversationListModel = gson.fromJson(response, HomeNewModel.class);

                        HashMap<String, List<Object>> listHashMap = new LinkedHashMap<>();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("HomeData");

                        //This should be the iterator you want.
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            Iterator<String> iter = temp.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                String tempJSONArray = temp.getString(key);
                                LoggerCustom.erorr("key", key);
                                LoggerCustom.erorr("tempJSONArray", tempJSONArray);
//                                    listHashMap.put(key,iter)
                                JSONArray jsonArray1 = new JSONArray(tempJSONArray);
                                List<Object> posts = null;
                                if (jsonArray1.getJSONObject(0).has("type_id")) {
                                    Type listType = new TypeToken<List<BannerModel>>() {
                                    }.getType();
                                    posts = gson.fromJson(tempJSONArray, listType);
                                } else if (jsonArray1.getJSONObject(0).has("is_feature")) {
                                    Type listType = new TypeToken<List<CategoryDataModel>>() {
                                    }.getType();
                                    posts = gson.fromJson(tempJSONArray, listType);
                                } else {
                                    Type listType = new TypeToken<List<WallpaperModel>>() {
                                    }.getType();
                                    posts = gson.fromJson(tempJSONArray, listType);
                                }
                                listHashMap.put(key, posts);
                            }
                        }
                        conversationListModel.setListHashMap(listHashMap);

                        callback.onDataSuccess(conversationListModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    AppConstant.URL_UPDATE_TOKEN;
    public static void updateFCMTokenRetro(Context c, String user_id, String fcm, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("user_device_token", fcm);
        params.put("user_id", user_id);
        Call<WallpaperListModel> updateFCMTokenData = RetrofitInstance.getInstance().apiInterface.updateFCMTokenData(params);

        updateFCMTokenData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);
                InAppPreference inAppPreference =  InAppPreference.getInstance(c);
                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (("" + model.getStatus()).equalsIgnoreCase("1")) {
                    try {
                        inAppPreference.setFCMTokenSend(true);
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });
    }

    //AppConstant.URL_SILENT_LOGIN;
    public static void loginApiRetro(Context c, String userid, RetroCallbacks callback) {
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("user_id", userid);
        params.put("device_id", AppUtility.getHardwareId(c));
        params.put("model_number", AppUtility.getDeviceTextName());
        params.put("os_version", AppUtility.getOsVersion());
        params.put("fcm_id", InAppPreference.getInstance(c).getFCM());
//                params.put("time_in_millis", System.currentTimeMillis() + "");
        params.put("user_device_serial_no", "");
        params.put("referer", InAppPreference.getInstance(c).getReferId());
        Call<DataListModel> updateFCMTokenData = RetrofitInstance.getInstance().apiInterface.logInApiData(params);

        updateFCMTokenData.enqueue(new Callback<DataListModel>() {
            @Override
            public void onResponse(Call<DataListModel> call, retrofit2.Response<DataListModel> response) {
                String str = (String) response.body().toString();
                LoggerCustom.erorr("Response", "" + str);
                preferenceStore.setSaveResponse(GET_USER_INFO_OPERATION_ID, str);
                DataListModel model = new DataListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DataListModel> call, Throwable t) {
                System.out.println("Failed");

                String response = preferenceStore.getSaveResponse(GET_USER_INFO_OPERATION_ID);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    LoggerCustom.erorr("onErrorResponse", "from DB");
                    DataListModel model = new DataListModel();
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, DataListModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //AppConstant.URL_GET_SEARCH_TAGS;
    public static void getSearchApiRequestRetro(Context c, String usedId, String tags, boolean isFromSearch, RetroCallbacks callback) {
        InAppPreference store = InAppPreference.getInstance(c);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("user_id", store.getUserId());
        params.put("used_ids", usedId);
        params.put("device_id", AppUtility.getHardwareId(c));
        params.put("keyword", tags);
        params.put("keyword_save", isFromSearch ? "1" : "0");
        Call<WallpaperListModel> updateFCMTokenData = RetrofitInstance.getInstance().apiInterface.getSearchApiData(params);

        updateFCMTokenData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);
                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
                try {
                    callback.onDataError(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    AppConstant.URL_GET_PITCHBLACK_WALLPAPER;
    public static void getPitchBlackWallpaperRetro(Context c, String usedId, RetroCallbacks callback) {
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
//                params.put("limit", "50");
        params.put("used_ids", usedId);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<WallpaperListModel> updateFCMTokenData = RetrofitInstance.getInstance().apiInterface.getPitchBlackWallData(params);

        updateFCMTokenData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String str = (String) response.body().toString();
                LoggerCustom.erorr("Response", "" + str);
                preferenceStore.setSaveResponse(GET_pitchblack_wallpapers, str);
                WallpaperListModel model = new WallpaperListModel();

                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                String response = preferenceStore.getSaveResponse(GET_pitchblack_wallpapers);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    WallpaperListModel model = new WallpaperListModel();
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, WallpaperListModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //AppConstant.URL_GET_DOUBLE_WALLPAPER;
    public static void getDoubleWallpaperRetro(Context c, String usedId, RetroCallbacks callback) {
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
//                params.put("limit", "50");
        params.put("used_ids", usedId);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<DoubleWallData> updateFCMTokenData = RetrofitInstance.getInstance().apiInterface.getDoubleWallData(params);

        updateFCMTokenData.enqueue(new Callback<DoubleWallData>() {
            @Override
            public void onResponse(Call<DoubleWallData> call, retrofit2.Response<DoubleWallData> response) {
                String str = (String) response.body().toString();
                LoggerCustom.erorr("Response", "" + str);
                preferenceStore.setSaveResponse(GET_double_wallpaper, str);
                DoubleWallData model = new DoubleWallData();

                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DoubleWallData> call, Throwable t) {
                String response = preferenceStore.getSaveResponse(GET_double_wallpaper);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    DoubleWallData model = new DoubleWallData();
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, DoubleWallData.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    //AppConstant.URL_GET_AI_WALLPAPER;
    public static void getTrendingWallpaperRetro(Context c, String usedId, RetroCallbacks callback) {
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("filter", "2");
        params.put("used_ids", usedId);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<WallpaperListModel> trendingWallData = RetrofitInstance.getInstance().apiInterface.getTrendingWallData(params);

        trendingWallData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String str = (String) response.toString();
                LoggerCustom.erorr("Response", "" + str);
                preferenceStore.setSaveResponse(GET_trending_wallpapers, str);
                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                String response = preferenceStore.getSaveResponse(GET_double_wallpaper);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    WallpaperListModel model = new WallpaperListModel();
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, WallpaperListModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //AppConstant.URL_GET_REWARDED_WALLPAPER;
    public static void getRewardedWallpaperRetro(Context c, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<WallpaperListModel> trendingWallData = RetrofitInstance.getInstance().apiInterface.getRewardedWallData(params);

        trendingWallData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String str = (String) response.toString();
                LoggerCustom.erorr("Response", "" + str);
                WallpaperListModel model = new WallpaperListModel();

                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                try {
                    callback.onDataError(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // AppConstant.URL_GET_AUTO_WALLPAPER;
    public static void getAutoWallpaperChangerRetro(Context c, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<WallpaperListModel> trendingWallData = RetrofitInstance.getInstance().apiInterface.getAutoWallData(params);

        trendingWallData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String str = (String) response.toString();
                LoggerCustom.erorr("Response", "" + str);
                WallpaperListModel model = new WallpaperListModel();

                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                try {
                    callback.onDataError(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void getLiveWallpaperRetro(Context c, String usedId, String filter, RetroCallbacks callback) {

        String url = Constants.URL_GET_LIVE_WALLPAPER;
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        LoggerCustom.erorr(TAG, "url:" + url);

        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("filter", filter + "");
        params.put("used_ids", usedId);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<WallpaperListModel> trendingWallData = RetrofitInstance.getInstance().apiInterface.getLiveWallData(params);

        trendingWallData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String str = (String) response.toString();
                LoggerCustom.erorr("Response", "" + str);
                WallpaperListModel model = new WallpaperListModel();
                preferenceStore.setSaveResponse(GET_live_wallpaper_list + "_" + filter, str);
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                String response = preferenceStore.getSaveResponse(GET_live_wallpaper_list + "_" + filter);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    WallpaperListModel model = new WallpaperListModel();
                    LoggerCustom.erorr("onErrorResponse", "from DB");
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, WallpaperListModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void getWallpaperRetro(Context c, String category_id, String usedId, String filter, RetroCallbacks callback) {

        String url = Constants.URL_GET_WALLPAPER;
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        LoggerCustom.erorr(TAG, "url: " + url);

        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("filter", filter + "");
        params.put("used_ids", usedId);
        params.put("category_id", category_id);
        params.put("device_id", AppUtility.getHardwareId(c));
        Call<WallpaperListModel> trendingWallData = RetrofitInstance.getInstance().apiInterface.getWallData(params);

        trendingWallData.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String str = (String) response.toString();
                LoggerCustom.erorr("Response", "" + str);
                WallpaperListModel model = new WallpaperListModel();
                if (TextUtils.isEmpty(category_id)) {
                    preferenceStore.setSaveResponse(GET_wallpaper_list + "_F" + filter, str);
                } else
                    preferenceStore.setSaveResponse(GET_wallpaper_list + "_C" + category_id, str);

                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    callback.onDataSuccess(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                String response = "";
                if (TextUtils.isEmpty(category_id)) {
                    response = preferenceStore.getSaveResponse(GET_wallpaper_list + "_F" + filter);
                } else
                    response = preferenceStore.getSaveResponse(GET_wallpaper_list + "_C" + category_id);

                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    WallpaperListModel model = new WallpaperListModel();
                    LoggerCustom.erorr("onErrorResponse", "from DB");
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, WallpaperListModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public static void getInAppPurchasedRetro(String device_id, String user_id, String in_app_purchase_id, String purchaseToken, final RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("device_id", device_id);
        params.put("user_id", user_id);
        params.put("in_app_purchase_id", in_app_purchase_id);
        params.put("purchaseToken", "" + purchaseToken);
        Call<IModelBase> purchaseDetails = RetrofitInstance.getInstance().apiInterface.getInAppPurchaseData(params);

        purchaseDetails.enqueue(new Callback<IModelBase>() {
            @Override
            public void onResponse(Call<IModelBase> call, retrofit2.Response<IModelBase> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                IModelBase model = new IModelBase();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    callback.onDataSuccess(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<IModelBase> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });
    }

    //AppConstant.URL_DISABLE_VIDEO;
    public static void disableVideoRetro(Context c, String img_id, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params.put("img_id", img_id);
        Call<WallpaperListModel> purchaseDetails = RetrofitInstance.getInstance().apiInterface.disableVideoList(params);

        purchaseDetails.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    callback.onDataSuccess(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });
    }

    //AppConstant.URL_DISABLE_IMG;
    public static void disableImageRetro(Context context, String img_id, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params.put("img_id", img_id);
        Call<WallpaperListModel> disableImageList = RetrofitInstance.getInstance().apiInterface.disableImageList(params);

        disableImageList.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    callback.onDataSuccess(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });
    }

    //AppConstant.URL_DELETE_IMG;
    public static void deleteImgRetro(Context context, String img_id, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("img_id", img_id);
        Call<WallpaperListModel> deleteImageList = RetrofitInstance.getInstance().apiInterface.deleteImageData(params);

        deleteImageList.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (("" + model.getStatus()).equalsIgnoreCase("1")) {
                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });

    }

    //AppConstant.URL_DELETE_DOUBLE_WALL;
    public static void deleteDoubleRetro(Context context, String img_id, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("img_id", img_id);
        Call<WallpaperListModel> deleteDoubleWall = RetrofitInstance.getInstance().apiInterface.deleteDoubleWallData(params);

        deleteDoubleWall.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (("" + model.getStatus()).equalsIgnoreCase("1")) {
                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });

    }

    //AppConstant.URL_DELETE_VIDEO;
    public static void deleteVideoRetro(Context context, String img_id, RetroCallbacks callback) {
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("img_id", img_id);
        Call<WallpaperListModel> deleteVideoWall = RetrofitInstance.getInstance().apiInterface.deleteVideoWallData(params);

        deleteVideoWall.enqueue(new Callback<WallpaperListModel>() {
            @Override
            public void onResponse(Call<WallpaperListModel> call, retrofit2.Response<WallpaperListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                WallpaperListModel model = new WallpaperListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (("" + model.getStatus()).equalsIgnoreCase("1")) {
                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<WallpaperListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });
    }


    //AppConstant.URL_GET_LIKE_WALLPAPER;
    public static void retroGetLikeWall(Context c, String userid, RetroCallbacks callback) {
        final InAppPreference preferenceStore = InAppPreference.getInstance(BlackApplication.bContext);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("user_id", userid);
        Call<FavouriteListModel> mediaDetails = RetrofitInstance.getInstance().apiInterface.getLikedWallList(params);

        mediaDetails.enqueue(new Callback<FavouriteListModel>() {
            @Override
            public void onResponse(Call<FavouriteListModel> call, retrofit2.Response<FavouriteListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                preferenceStore.setSaveResponse(GET_get_fav_wall, res);
                FavouriteListModel model = new FavouriteListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    callback.onDataSuccess(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FavouriteListModel> call, Throwable t) {
                System.out.println("Failed");

                LoggerCustom.erorr("error", t.getMessage());
                String response = preferenceStore.getSaveResponse(GET_get_fav_wall);
                if (TextUtils.isEmpty(response)) {
                    try {
                        callback.onDataError(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    FavouriteListModel model = new FavouriteListModel();
                    try {
                        Gson gson = GsonUtility.getInstant();//new Gson();
                        model = gson.fromJson(response, FavouriteListModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        callback.onDataSuccess(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    //AppConstant.SET_FAVOURITE;
    public static void setLikeRetro(Context c, String img_id, String value, String isStaticWall, RetroCallbacks callback) {
        InAppPreference store = InAppPreference.getInstance(c);
        Map<String, String> params = new HashMap<>();
        params = getCommonParam(params);
        params.put("user_id", store.getUserId());
        params.put("img_id", img_id);
        params.put("value", value);
        params.put("isStaticWall", isStaticWall);
        Call<FavouriteListModel> mediaDetails = RetrofitInstance.getInstance().apiInterface.setLikedWallList(params);

        mediaDetails.enqueue(new Callback<FavouriteListModel>() {
            @Override
            public void onResponse(Call<FavouriteListModel> call, retrofit2.Response<FavouriteListModel> response) {
                String res = String.valueOf(response);
                LoggerCustom.erorr("Response", "" + res);

                FavouriteListModel model = new FavouriteListModel();
                try {
                    model = response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    callback.onDataSuccess(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FavouriteListModel> call, Throwable t) {
                System.out.println("Failed");
                LoggerCustom.erorr("error", t.getMessage());
            }
        });
    }


    private static Map<String, String> getCommonParam(Map<String, String> params) {
        try {
            boolean isEnable = false;
            try {
                if (SingletonControl.getInstance().getDataList().getLogic().IsSecretPassed()) {
                    isEnable = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isEnable) {
                Calendar calendar = Calendar.getInstance();
                long timeInMillis = calendar.getTimeInMillis();
                String hashKey = AppUtility.getHashKeyApp(BlackApplication.bContext);
                hashKey = AppUtility.md5(timeInMillis + AppUtility.getHardwareId(BlackApplication.bContext) + hashKey);

                params.put("app_secret_key", hashKey);
                params.put("time_in_millis", "" + timeInMillis);
                params.put("user_device_serial_no", "" + AppUtility.getHardwareId(BlackApplication.bContext));
            }
            params.put("pkg_name", "" + BlackApplication.bContext.getPackageName());
            params.put("app_version", "" + AppUtility.appVertion(BlackApplication.bContext));
            InAppPreference inAppPreference = InAppPreference.getInstance(BlackApplication.bContext);

            String sel_lang = "";
            if (!TextUtils.isEmpty(inAppPreference.getSelLang()))
                sel_lang = inAppPreference.getSelLang();
            else {
                sel_lang = AppUtility.getLandCodeSystem(BlackApplication.bContext);
            }
            params.put("lang_code", "" + sel_lang);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


}
