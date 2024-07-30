package wallpaper.black.live.uhd;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends AppCompatActivity {

    ActivityPrivacyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isPrivacy=getIntent().getBooleanExtra("isPrivacy",false);

        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if(isPrivacy)
            getSupportActionBar().setTitle(getString(R.string.menu_privacy_title));
        else
            getSupportActionBar().setTitle(getString(R.string.help));

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, null);
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(isPrivacy)
            binding.webview.loadUrl(Constants.URL_PRIVACY_POLICY);
        else
            binding.webview.loadUrl(Constants.URL_Help);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BlackApplication.getBlackApplication().isShareRate = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlackApplication.getBlackApplication().isShareRate = false;
    }
}