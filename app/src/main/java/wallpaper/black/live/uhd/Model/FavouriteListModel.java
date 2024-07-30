package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FavouriteListModel implements IModel, Serializable {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("like")
    @Expose
    private List<WallpaperModel> wallpaper = null;
    @SerializedName("like_live")
    @Expose
    private List<WallpaperModel> videoWallpaper = null;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<WallpaperModel> getWallpaperList() {
        return wallpaper;
    }

    public void setWallpaperList(List<WallpaperModel> wallpaper) {
        this.wallpaper = wallpaper;
    }

    public List<WallpaperModel> getVideoWallpaperList() {
        return videoWallpaper;
    }

    public void setVideoWallpaperList(List<WallpaperModel> videoWallpaper) {
        this.videoWallpaper = videoWallpaper;
    }
}
