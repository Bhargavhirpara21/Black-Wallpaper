package wallpaper.black.live.uhd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;

public class RedirectActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Intent intent;
			LoggerCustom.erorr("AlertActivity","" + BlackApplication.isAppRunning);
			if(BlackApplication.isAppRunning){
				intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
						Intent.FLAG_ACTIVITY_SINGLE_TOP);
			}else{
				intent = new Intent(this, SplashScreenActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}

			if(getIntent()!=null && getIntent().getExtras()!=null) {
				Bundle bundle = getIntent().getBundleExtra("bundle");
				if (bundle != null) {
					intent.putExtras(bundle);
				}
			}else{
			}
			startActivity(intent);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
