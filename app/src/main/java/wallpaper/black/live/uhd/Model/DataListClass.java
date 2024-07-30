package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DataListClass implements IModel, Serializable {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("like_id")
    @Expose
    private String likeWallId;
    @SerializedName("like_live_id")
    @Expose
    private String likeLiveId;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("category")
    @Expose
    private List<CategoryDataModel> category = null;

    public boolean getIs_pro() {
        try {
            return is_pro.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @SerializedName("is_pro")
    @Expose
    private String is_pro;

    public List<CategoryDataModel> getCategory_stock() {
        return category_stock;
    }

    @SerializedName("category_stock")
    @Expose
    private List<CategoryDataModel> category_stock = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<CategoryDataModel> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryDataModel> category) {
        this.category = category;
    }

    public String getLikeWallId() {
        return likeWallId;
    }

    public void setLikeWallId(String likeWallId) {
        this.likeWallId = likeWallId;
    }

    public String getLikeLiveId() {
        return likeLiveId;
    }

    public void setLikeLiveId(String likeLiveId) {
        this.likeLiveId = likeLiveId;
    }
}
