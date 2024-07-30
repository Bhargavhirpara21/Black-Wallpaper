package wallpaper.black.live.uhd;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.WallpaperType;
import wallpaper.black.live.uhd.Fragments.HomeFragment;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.databinding.ActivityPitchWallpaperBinding;


public class PitchWallpaperActivity extends BaseActivity {

    private FragmentManager fragManager;
    private CategoryDataModel categoryDataModel;
    //0=category,1=downloads,2=pitchblack
    ActivityPitchWallpaperBinding binding;
    @Override
    public void onDestroy() {
        super.onDestroy();
        fragManager = null;
        categoryDataModel = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPitchWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(getString(R.string.pitch_black_category));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        categoryDataModel = new CategoryDataModel();
        categoryDataModel.setCategoryId("69");
        categoryDataModel.setName(getString(R.string.pitch_black_category));

            if (!AppUtility.isInterNetworkAvailable(PitchWallpaperActivity.this)) {
                AppUtility.showInterNetworkDialog(PitchWallpaperActivity.this, null);
            } else {
                fragManager = getSupportFragmentManager();
                HomeFragment categoryFragment = null;
                if (categoryFragment == null) {
                    try {
                        Fragment fragment = fragManager.findFragmentByTag("WallpaperListFragment");
                        if (fragment != null && fragment instanceof HomeFragment) {
                            categoryFragment = (HomeFragment) fragment;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (categoryFragment == null) {
                        categoryFragment = new HomeFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("wallpaperType", WallpaperType.PITCH_BLACK_TYPE);
                        bundle.putSerializable("object", categoryDataModel);
                        categoryFragment.setArguments(bundle);
                        addHideFragment(categoryFragment, "WallpaperListFragment");
                    }
                }
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Method to add and hide all of the fragments you need to. In my case I hide 4 fragments, while 1 is visible, that is the first one.
    private void addHideFragment(Fragment fragment, String tag) {
        fragManager.beginTransaction().add(R.id.container, fragment, tag).commit();
    }

}
