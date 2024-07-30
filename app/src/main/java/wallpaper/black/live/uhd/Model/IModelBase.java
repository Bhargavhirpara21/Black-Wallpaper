package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IModelBase implements IModel, Serializable {

	public IModelBase(){
	}

	@SerializedName("result")
	@Expose
	private Boolean status;
	@SerializedName("msg")
	@Expose
	private String msg;


	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}
