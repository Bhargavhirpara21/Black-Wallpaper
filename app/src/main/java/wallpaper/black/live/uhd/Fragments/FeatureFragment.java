package wallpaper.black.live.uhd.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wallpaper.black.live.uhd.Adapters.FeatureHomeAdapter;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.MainActivity;
import wallpaper.black.live.uhd.Model.BannerModel;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.HomeModelClass;
import wallpaper.black.live.uhd.Model.HomeNewModel;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.SingletonControl;
import wallpaper.black.live.uhd.databinding.FragmentFeatureBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class FeatureFragment extends Fragment implements RetroCallbacks {

    private Context mContext;
    private FeatureHomeAdapter featureHomeAdapter;
    private List<HomeModelClass> homeModelClasses = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int filter_data;

    public void setFilter_data(int filter_data) {
        this.filter_data = filter_data;
    }

    public int getFilter_data() {
        return filter_data;
    }

    FragmentFeatureBinding binding;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Constants.ADS_PER_PAGE = SingletonControl.getInstance().getDataList().getLogic().getAdsPerPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeatureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.swiperefresh.setEnabled(false);
        homeModelClasses = new ArrayList<>();

        showLoading();
        getData();
        if (binding.swiperefresh != null) {
            binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshContent();
                }
            });
        }

        binding.fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.featureRvList.smoothScrollToPosition(0);
            }
        });
    }

    public void refreshCall() {
        if (binding.swiperefresh != null)
            binding.swiperefresh.post(new Runnable() {
                @Override
                public void run() {
                    if (binding.swiperefresh != null)
                        binding.swiperefresh.setRefreshing(true);
                }
            });
        LoggerCustom.erorr("log", "refresh");
        getData();
    }

    public void removeRefresh() {
        if (binding.swiperefresh != null)
            binding.swiperefresh.setRefreshing(false);
    }

    public void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    getData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.REFRESH_TIME_OUT);
    }

    public void getData() {

        if (AppUtility.isInterNetworkAvailable(getActivity())) {
            RetroApiRequest.getHomeDataNewRetro(mContext, this);
        } else {
            AppUtility.showInterNetworkDialog(getActivity(), new InternetNetworkInterface() {
                @Override
                public void onOkClick() throws Exception {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.showLastFragment(0);
                    binding.swiperefresh.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onDataSuccess(IModel result) throws JSONException {
        HomeNewModel model = (HomeNewModel) result;

        if (model.getListHashMap() != null) {
            removeRefresh();

            if(homeModelClasses !=null)
                homeModelClasses.clear();
            String[] filter_array = model.getFilterSel().split(",");
            HashMap<String, List<Object>> listHashMap = model.getListHashMap();

            int position = 0;

            for (String key : listHashMap.keySet()){
                LoggerCustom.erorr("name", key);

                List<Object> tempList=listHashMap.get(key);

                if(tempList.get(0) instanceof WallpaperModel){
                    List<WallpaperModel> finalList=new ArrayList<>();
                    for (Object obj:tempList) {
                        finalList.add((WallpaperModel) obj);
                    }
                    if (finalList != null && finalList.size()>0 && !TextUtils.isEmpty(finalList.get(0).getVidPath())) {
                        homeModelClasses.add(new HomeModelClass(HomeModelClass.TYPE_HORIZONTOL_DATA, key, finalList, filter_array[position], false));
                        LoggerCustom.erorr("tag", "equals charging animation");
                    } else {
                        LoggerCustom.erorr("fiter:", ""+filter_array[position]+ " pos:"+position);
                        homeModelClasses.add(new HomeModelClass(HomeModelClass.TYPE_HORIZONTOL_DATA, key, finalList, filter_array[position], true));
                    }
                }else if(tempList.get(0) instanceof BannerModel){
                    List<BannerModel> finalList=new ArrayList<>();
                    for (Object obj:tempList) {
                        finalList.add((BannerModel) obj);
                    }
                    if(finalList.get(0).getType().equalsIgnoreCase("banner_Ad")){
//                        if (AppUtility.isStoreVersion(getActivity()) && !BlackZyApp.getBlackLyAppContext().isProUser()) {
//                            homeList.add(new HomeModelClass(HomeModelClass.TYPE_DATA_AD, "", null, "-100", true));
//                        }
                    }else if(finalList.get(0).getType().equalsIgnoreCase("native")){
//                        if (AppUtility.isStoreVersion(getActivity()) && !BlackZyApp.getBlackLyAppContext().isProUser()) {
//                            homeList.add(new HomeModelClass(HomeModelClass.TYPE_NATIVE_AD, "", null, "-100", true));
//                        }
                    }else if(finalList.get(0).getType().equalsIgnoreCase("local_ad")){
//                        if (AppUtility.isStoreVersion(getActivity()) && !BlackZyApp.getBlackLyAppContext().isProUser()) {
//                            HomeModelClass homeModelClass =new HomeModelClass(HomeModelClass.ADVERTISE_TYPE, key, null, filter_array[position], false);
//                            homeModelClass.setBanner_localAd(finalList.get(0));
//                            homeList.add(homeModelClass);
//                        }
                    } else if(finalList.get(0).getType().equalsIgnoreCase("tags")){
                        homeModelClasses.add(new HomeModelClass(HomeModelClass.TYPE_HOME_TAG, key, null, "-100", true));
                    }else {
                        HomeModelClass homeModelClass = new HomeModelClass(HomeModelClass.TYPE_FEATURE_BANNER, key, null, filter_array[position], false);
                        homeModelClass.setBannerList(finalList);
                        homeModelClasses.add(homeModelClass);
                    }
                }else if(tempList.get(0) instanceof CategoryDataModel){
                    List<CategoryDataModel> finalList=new ArrayList<>();
                    for (Object obj:tempList) {
                        finalList.add((CategoryDataModel) obj);
                    }
                    if(position==0){
                        HomeModelClass homeModelClass = new HomeModelClass(HomeModelClass.TYPE_HOME_COROUSAL, key, null, "-99", true);
                        homeModelClass.setCategoryList(finalList);
                        homeModelClasses.add(position,homeModelClass);
//                        ControllerSingleton.getInstance().setFeaturedList(model.getHomeData().getFeatureList());
                    }else {
                        HomeModelClass homeModelClass = new HomeModelClass(HomeModelClass.CATEGORY_LIST_DATA, key, null, "", true);
                        homeModelClass.setCategoryList(finalList);
                        homeModelClasses.add(homeModelClass);
                    }
                }
                position++;
            }

            fillUpAdapter();
        } else {
            if (featureHomeAdapter != null) {
                featureHomeAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showLoading() {
        binding.rlProgress.setVisibility(View.VISIBLE);
        binding.featureRvList.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.rlProgress.setVisibility(View.GONE);
        binding.featureRvList.setVisibility(View.VISIBLE);
    }

    private void fillUpAdapter() {
        if (homeModelClasses != null && homeModelClasses.size() > 0) {

            binding.swiperefresh.setEnabled(true);
            binding.featureRvList.setVisibility(View.VISIBLE);
            binding.txtNodata.setVisibility(View.GONE);
            binding.rlProgress.setVisibility(View.GONE);

            if (featureHomeAdapter == null) {

                featureHomeAdapter = new FeatureHomeAdapter(homeModelClasses, mContext);
                linearLayoutManager = new LinearLayoutManager(getActivity());

                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                binding.featureRvList.setLayoutManager(linearLayoutManager);
                binding.featureRvList.setItemViewCacheSize(homeModelClasses.size());
                binding.featureRvList.setItemAnimator(null);
                binding.featureRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();
                        int firstVisibleItemPositions = linearLayoutManager.findFirstVisibleItemPosition();

                        if (firstVisibleItemPositions == 0) {
//                            fadeOutAnimation(fab_up, 1000);
                            binding.fabUp.setVisibility(View.GONE);
                        } else if (dy > 0) { // scrolling down
                            // delay of 2 seconds before hiding the fab
//                            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fab_up, "alpha", 0f, .3f);
//                            fadeOut.setDuration(3000);
//                            fadeOutAnimation(fab_up, 1000);
                            binding.fabUp.setVisibility(View.GONE);

                        } else if (dy < 0) { // scrolling up
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                            Animation fadeIn = new AlphaAnimation(0, 1);
//                            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
//                            fadeIn.setDuration(1000);
//                            fadeInAnimation(fab_up, 1000);

                            binding.fabUp.setVisibility(View.VISIBLE);
//                                }
//                            }, 2000);
                        }
                    }
                });

                binding.featureRvList.setAdapter(featureHomeAdapter);
            } else {
                featureHomeAdapter.notifyDataSetChanged();
            }
        } else {
            noDataLayout();
        }
    }

    private void noDataLayout() {
        binding.txtNodata.setText(getString(R.string.no_data));
        binding.txtNodata.setVisibility(View.VISIBLE);
        binding.rlProgress.setVisibility(View.GONE);
        binding.swiperefresh.setVisibility(View.GONE);
        binding.featureRvList.setVisibility(View.GONE);
    }

    @Override
    public void onDataError(String result) throws Exception {
    }

    @Override
    public void onDestroy() {
        LoggerCustom.erorr("MainActivity", "onDestroy MainFragment");
        super.onDestroy();
        if (featureHomeAdapter != null)
            featureHomeAdapter.releaseResource();
        mContext = null;
        featureHomeAdapter = null;
        homeModelClasses = null;
        linearLayoutManager = null;
    }

    @Override
    public void onPause() {
        LoggerCustom.erorr("MainFragment", "onPause");
        pauseAdapterAd(true);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggerCustom.erorr("MainFragment", "onResume");
        resumeAdapterAd(true);
    }

    public void resumeAdapterAd(boolean isPauseOriginal) {
        if (featureHomeAdapter != null) {
            featureHomeAdapter.onResume(isPauseOriginal);
        }
    }

    public void pauseAdapterAd(boolean isPauseOriginal) {
        if (featureHomeAdapter != null) {
            featureHomeAdapter.onPause(isPauseOriginal);
        }
    }
}
