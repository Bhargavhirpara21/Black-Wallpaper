package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class HomeNewModel implements IModel, Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("filter_sel")
    @Expose
    private String filterSel;

    private HashMap<String, List<Object>> listHashMap = null;

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

    public HashMap<String, List<Object>> getListHashMap() {
        return listHashMap;
    }

    public void setListHashMap(HashMap<String, List<Object>> listHashMap) {
        this.listHashMap = listHashMap;
    }

    public String getFilterSel() {
        return filterSel;
    }
}
