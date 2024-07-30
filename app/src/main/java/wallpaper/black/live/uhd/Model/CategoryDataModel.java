package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryDataModel implements IModel, Serializable {

    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("img_path_feature")
    @Expose
    private String imgPathFeature;
    @SerializedName("is_feature")
    @Expose
    private String isFeature;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("is_event")
    @Expose
    private String isEvent;


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImgPathFeature() {
        return imgPathFeature;
    }

    public void setImgPathFeature(String imgPathFeature) {
        this.imgPathFeature = imgPathFeature;
    }

    public String getIsFeature() {
        return isFeature;
    }

    public void setIsFeature(String isFeature) {
        this.isFeature = isFeature;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIsEvent() {
        return isEvent;
    }

    public void setIsEvent(String isEvent) {
        this.isEvent = isEvent;
    }

    boolean isNativeAd = false;

    public boolean isNativeTemplateAd() {
        return isNativeTemplateAd;
    }

    public void setNativeTemplateAd(boolean nativeTemplateAd) {
        isNativeTemplateAd = nativeTemplateAd;
    }

    boolean isNativeTemplateAd = false;

    public boolean isNativeAd() {
        return isNativeAd;
    }

    public void setNativeAd(boolean nativeAd) {
        isNativeAd = nativeAd;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    private String link = null;
}
