package wallpaper.black.live.uhd.AppUtils;
import com.google.gson.Gson;

public class GsonUtility {
    public static Gson gson;

    public static Gson getInstant() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
