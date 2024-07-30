package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WallpaperModel implements IModel, Serializable {

    public Object getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(Object nativeAd) {
        this.nativeAd = nativeAd;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean aNative) {
        isNative = aNative;
    }

    private boolean isNative;
    transient private Object nativeAd;

    @SerializedName("img_id")
    @Expose
    private String imgId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("img_path")
    @Expose
    private String imgPath;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("source_link")
    @Expose
    private String sourceLink;
    @SerializedName("license")
    @Expose
    private String license;
    @SerializedName("download")
    @Expose
    private String download;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("vid_path")
    @Expose
    private String vidPath;

    @SerializedName("tags")
    @Expose
    private String tags;

    public String getVidPath() {
        return vidPath;
    }

    public void setVidPath(String vidPath) {
        this.vidPath = vidPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getTags() {
        return tags;
    }
}
