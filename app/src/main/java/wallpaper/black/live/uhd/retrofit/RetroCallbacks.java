package wallpaper.black.live.uhd.retrofit;


import org.json.JSONException;

import wallpaper.black.live.uhd.Model.IModel;

public interface RetroCallbacks {
    void onDataSuccess(IModel result) throws JSONException;
    void onDataError(String result) throws Exception;
}
