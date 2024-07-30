package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WallpaperListModel implements IModel, Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("Wallpaper")
    @Expose
    private List<WallpaperModel> wallpaper = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<WallpaperModel> getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(List<WallpaperModel> wallpaper) {
        this.wallpaper = wallpaper;
    }
}
