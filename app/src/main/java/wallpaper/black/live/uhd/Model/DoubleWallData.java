package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DoubleWallData implements IModel, Serializable  {

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("Wallpaper")
    @Expose
    private List<DoubleWallsModel> doubleList = null;


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

    public List<DoubleWallsModel> getDoubleList() {
        return doubleList;
    }

    public void setDoubleList(List<DoubleWallsModel> doubleList) {
        this.doubleList = doubleList;
    }
}
