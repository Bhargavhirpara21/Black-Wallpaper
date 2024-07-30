package wallpaper.black.live.uhd;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.WallpaperType;
import wallpaper.black.live.uhd.Fragments.HomeFragment;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.databinding.ActivityMoreWallpaperBinding;

public class MoreWallpaperActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private CategoryDataModel categoryDataModel;
    private int from;//0=category,1=downloads,2=viewall
    private List<WallpaperModel> modelArrayList = new ArrayList<WallpaperModel>();
    public String selectedFilter;
    ActivityMoreWallpaperBinding binding;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentManager = null;
        categoryDataModel = null;
        from = 0;
        if (modelArrayList != null)
            modelArrayList.clear();
        modelArrayList = null;
        selectedFilter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoreWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String tags = null;

        if (getIntent() != null) {
            String from1 = getIntent().getStringExtra("from");
            tags = getIntent().getStringExtra("tags");
            if (from1 != null && from1.equals("download")) {
                from = 1;
            } else if (from1 != null && from1.equals("viewall")) {
                from = 2;
                modelArrayList = (List<WallpaperModel>) getIntent().getSerializableExtra("list");
                selectedFilter = getIntent().getStringExtra("filter");
            } else {
                from = 0;
                try {
                    categoryDataModel = (CategoryDataModel) getIntent().getSerializableExtra("object");
                } catch (Exception e) {
                }
            }
        }

        if (!AppUtility.isInterNetworkAvailable(this) && from != 1) {
            AppUtility.showInterNetworkDialog(this, null);
            return;
        }
        if (!TextUtils.isEmpty(tags)) {
            setupToolbar(tags);
        } else {
            if (categoryDataModel == null) {
                finish();
                return;
            }
            setupToolbar(categoryDataModel.getName());
        }

        mFragmentManager = getSupportFragmentManager();
        HomeFragment categoryFragment = null;
        if (categoryFragment == null) {
            try {
                Fragment fragment = mFragmentManager.findFragmentByTag("WallpaperListFragment");
                if (fragment != null && fragment instanceof HomeFragment) {
                    categoryFragment = (HomeFragment) fragment;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (categoryFragment == null) {
                categoryFragment = new HomeFragment();
                Bundle bundle = new Bundle();
                if (from == 2) {
                    bundle.putSerializable("wallpaperType", WallpaperType.HOME_TYPE);
                    bundle.putSerializable("selectedFilter", selectedFilter);
                    bundle.putSerializable("list", (Serializable) modelArrayList);
                } else {
                    bundle.putSerializable("wallpaperType", WallpaperType.CATEGORY_TYPE);
                    bundle.putSerializable("object", categoryDataModel);
                    bundle.putString("tags", tags);
                }
                categoryFragment.setArguments(bundle);
                addHideFragment(categoryFragment, "WallpaperListFragment");
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

    private void addHideFragment(Fragment fragment, String tag) {
        mFragmentManager.beginTransaction().add(R.id.container, fragment, tag).commit();
    }

    private void setupToolbar(String text) {
        getSupportActionBar().setTitle(text);
    }
}
