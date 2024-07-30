package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataListModel implements IModel, Serializable {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private DataListClass data;
    @SerializedName("logic")
    @Expose
    private LogicDataModel logic;

    public LogicDataModel getLogic() {
        return logic;
    }

    public void setLogic(LogicDataModel logic) {
        this.logic = logic;
    }

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

    public DataListClass getData() {
        return data;
    }

    public void setData(DataListClass data) {
        this.data = data;
    }
}
