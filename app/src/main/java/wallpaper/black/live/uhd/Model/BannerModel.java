package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BannerModel implements IModel, Serializable {
    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("type")
    @Expose
    private String type;

    public String getType_id() {
        return type_id;
    }

    @SerializedName("type_id")
    @Expose
    private String type_id;

    public String getCategory_id() {
        return category_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("cat_name")
    @Expose
    private String cat_name;

    public String getAction_url() {
        return action_url;
    }

    @SerializedName("action_url")
    @Expose
    private String action_url;
}
