package wallpaper.black.live.uhd;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.Fragments.MyFavouriteFragment;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.Model.FavouriteListModel;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.databinding.ActivityMyFavoriteBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class MyFavoriteActivity extends BaseActivity implements RetroCallbacks {

    private InAppPreference inAppPreference;
    private ViewPagerAdapter viewPagerAdapter;
    private List<WallpaperModel> liveWallpaperList = new ArrayList<WallpaperModel>();
    private List<WallpaperModel> wallpaperList = new ArrayList<WallpaperModel>();
    ActivityMyFavoriteBinding binding;

    @Override
    public void onDestroy() {
        super.onDestroy();
        inAppPreference = null;
        viewPagerAdapter = null;
        liveWallpaperList = null;
        wallpaperList = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inAppPreference = InAppPreference.getInstance(this);

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(getString(R.string.fav));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, new InternetNetworkInterface() {
                @Override
                public void onOkClick() throws Exception {
                    finish();
                }

            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavoriteData() {
        if (!AppUtility.isInterNetworkAvailable(this)) {
            AppUtility.showInterNetworkDialog(this, null);
        } else {
            RetroApiRequest.retroGetLikeWall(this, inAppPreference.getUserId(), this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFavoriteData();
    }

    private void setupTabIcons() {
        binding.tabs.getTabAt(0).setText(getString(R.string.wallpaper));
        binding.tabs.getTabAt(1).setText(getString(R.string.live_wall));
    }

    public void dismissDialogue() {
        binding.rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onDataSuccess(IModel result) throws JSONException {

        liveWallpaperList.clear();
        wallpaperList.clear();

        if (result != null) {
            FavouriteListModel model = (FavouriteListModel) result;

            wallpaperList.addAll(model.getWallpaperList());
            liveWallpaperList.addAll(model.getVideoWallpaperList());

            dismissDialogue();

            if (viewPagerAdapter != null) {
                MyFavouriteFragment wallPaperFragment = (MyFavouriteFragment) viewPagerAdapter.getItem(0);
                wallPaperFragment.updateDataAndRecyclerview(wallpaperList);

                MyFavouriteFragment liveWallFragment = (MyFavouriteFragment) viewPagerAdapter.getItem(1);
                liveWallFragment.updateDataAndRecyclerview(liveWallpaperList);
            } else {
                setupFavViewPager(binding.viewpager);
                binding.tabs.setupWithViewPager(binding.viewpager);
                setupTabIcons();

            }
        }
    }

    private void setupFavViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(2);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        MyFavouriteFragment wallPaperFragment = null;
        MyFavouriteFragment liveWallFragment = null;

        Bundle b = new Bundle();

        if (wallPaperFragment == null) {
            b = new Bundle();
            wallPaperFragment = new MyFavouriteFragment();
            b.putBoolean("isVideoWall", false);
            b.putSerializable("list", (Serializable) wallpaperList);
            wallPaperFragment.setArguments(b);
            viewPagerAdapter.addFragment(wallPaperFragment, "Wallpaper");

        }

        if (liveWallFragment == null) {
            liveWallFragment = new MyFavouriteFragment();
            b = new Bundle();
            b.putBoolean("isVideoWall", true);
            b.putSerializable("list", (Serializable) liveWallpaperList);
            liveWallFragment.setArguments(b);
            viewPagerAdapter.addFragment(liveWallFragment, "Live Wallpaper");
            viewPager.setAdapter(viewPagerAdapter);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

    @Override
    public void onDataError(String result) throws Exception {

    }
}
