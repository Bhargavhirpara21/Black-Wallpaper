package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DoubleWallsModel implements IModel, Serializable {

    @SerializedName("img_id")
    @Expose
    private String img_id;

    @SerializedName("img1")
    @Expose
    private String img1;

    @SerializedName("img2")
    @Expose
    private String img2;

    @SerializedName("download")
    @Expose
    private String download;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("priority")
    @Expose
    private String priority;


    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
