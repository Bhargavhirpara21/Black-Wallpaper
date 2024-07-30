package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeDataModel implements IModel, Serializable {

    @SerializedName("category_feature")
    @Expose
    private List<CategoryDataModel> featureList = null;
    @SerializedName("category_home")
    @Expose
    private List<CategoryDataModel> homeCategoryList = null;
    @SerializedName("popular_tag")
    @Expose
    private String popular_tag = null;
    @SerializedName("Live_wallpaper")
    @Expose
    private List<WallpaperModel> homeLiveList = null;
    @SerializedName("stock_category")
    @Expose
    private String stockCategory = null;

    public String getDouble_wallpaper() {
        return double_wallpaper;
    }

    @SerializedName("double_wallpaper")
    @Expose
    private String double_wallpaper = null;

    @SerializedName("Walllist")
    @Expose
    private List<WallpaperModel> wallList = null;

    public List<CategoryDataModel> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<CategoryDataModel> featureList) {
        this.featureList = featureList;
    }

    public List<CategoryDataModel> getHomeCategoryList() {
        return homeCategoryList;
    }

    public void setHomeCategoryList(List<CategoryDataModel> homeCategoryList) {
        this.homeCategoryList = homeCategoryList;
    }

    public String getPopular_tag() {
        return popular_tag;
    }

    public void setPopular_tag(String popular_tag) {
        this.popular_tag = popular_tag;
    }

    public List<WallpaperModel> getHomeLiveList() {
        return homeLiveList;
    }

    public void setHomeLiveList(List<WallpaperModel> homeLiveList) {
        this.homeLiveList = homeLiveList;
    }

    public String getStockCategory() {
        return stockCategory;
    }

    public void setStockCategory(String stockCategory) {
        this.stockCategory = stockCategory;
    }

    public List<WallpaperModel> getWallList() {
        return wallList;
    }

    public void setWallList(List<WallpaperModel> wallList) {
        this.wallList = wallList;
    }
}
