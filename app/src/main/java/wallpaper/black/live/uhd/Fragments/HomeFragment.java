package wallpaper.black.live.uhd.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.Adapters.CommonWallpaperAdapter;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.AppUtils.SpacesItemDecoration;
import wallpaper.black.live.uhd.AppUtils.WallpaperType;
import wallpaper.black.live.uhd.BlackApplication;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.MainActivity;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.EventModelClass;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperListModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.SingletonControl;
import wallpaper.black.live.uhd.databinding.FragmentHomeBinding;
import wallpaper.black.live.uhd.notifier.EventNotifier;
import wallpaper.black.live.uhd.notifier.EventState;
import wallpaper.black.live.uhd.notifier.EventTypes;
import wallpaper.black.live.uhd.notifier.IEventListener;
import wallpaper.black.live.uhd.notifier.ListenerPriority;
import wallpaper.black.live.uhd.notifier.NotifierFactory;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class HomeFragment extends Fragment implements RetroCallbacks, IEventListener {

    private Context mContext;
    private CommonWallpaperAdapter commonWallpaperAdapter;
    public List<WallpaperModel> wallpaperModels;
    private WallpaperType wallpaperType;
    private int pType = 0;
    private GridLayoutManager gridLayoutManager;
    private int pagination_count = 30;
    private int dataInsertedCount;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private static final int PAGE_START = 1;
    private int last_Pos = 0;
    private boolean more_data = false;
    private String wall_used_ids = "";
    private boolean isLoading;
    private CategoryDataModel categoryModel;
    private boolean isNeedToReset;
    private int home_filter;
    private String tags;
    private static final String TAG="HomeFragment";
    public void setHome_filter(int home_filter) {
        this.home_filter = home_filter;
    }
    public int getHome_filter() {
        return home_filter;
    }

    FragmentHomeBinding binding;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }
    private List<WallpaperModel> tempList = new ArrayList<WallpaperModel>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoggerCustom.erorr(TAG,"onCreate:");
        if (getArguments() != null) {
            wallpaperType = (WallpaperType) getArguments().getSerializable("wallpaperType");
            tags = getArguments().getString("tags");
            if(getArguments().containsKey("selectedFilter"))
                home_filter = Integer.parseInt(getArguments().getString("selectedFilter"));
            else
                home_filter = BlackApplication.selectedFilter_l;

            tempList = (List<WallpaperModel>) getArguments().getSerializable("list");
        }
        if (!TextUtils.isEmpty(tags)) {
            wallpaperType = WallpaperType.POPULARTAG_TYPE;
        }
        wall_used_ids = "";
        if (wallpaperType != null && wallpaperType == WallpaperType.HOME_TYPE)
            pType = 0;
        else if (wallpaperType != null && wallpaperType == WallpaperType.TRENDING_TYPE){
            pType = 1;
        }else if (wallpaperType != null && wallpaperType == WallpaperType.LIVE_TYPE){
            pType = 2;
        }else if (wallpaperType != null && wallpaperType == WallpaperType.CATEGORY_TYPE) {
            pType = 3;
            categoryModel = (CategoryDataModel) getArguments().getSerializable("object");
        } else if (wallpaperType != null && wallpaperType == WallpaperType.DOWNLOAD_TYPE) {
            pType = 4;
        } else if (wallpaperType != null && wallpaperType == WallpaperType.PITCH_BLACK_TYPE) {
            pType = 5;
        } else if (wallpaperType != null && wallpaperType == WallpaperType.STOCK_WALLPAPER_TYPE) {
            pType = 6;
        }else if (wallpaperType != null && wallpaperType == WallpaperType.BLACK_SCREEN_TYPE) {
            pType = 7;
        }else if (wallpaperType != null && wallpaperType == WallpaperType.POPULARTAG_TYPE) {
            pType = 8;
            Log.e("MAINFRAGMENT", "onCreate: Main"+tags);
        }

        try {
            Constants.ADS_PER_PAGE = SingletonControl.getInstance().getDataList().getLogic().getAdsPerPage();
            pagination_count = Integer.parseInt(SingletonControl.getInstance().getDataList().getLogic().getPaginationLimit());
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerUpdateListListener();
    }

    private boolean isPendingForLoad;
    private boolean isAPICalling;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        LoggerCustom.erorr(TAG,"setUserVisibleHint:"+wallpaperType+ " hidden:"+isVisibleToUser);
        if(isVisibleToUser){
            // Your fragment is visible
            if(wallpaperType==null)
                isPendingForLoad=true;

            if(!isAPICalling && (wallpaperModels ==null || wallpaperModels.size()==0) && wallpaperType!=null) {
                if(binding.swiperefreshCommon!=null)
                    binding.swiperefreshCommon.setEnabled(false);
                getWallpaperData();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoggerCustom.erorr(TAG,"onViewCreated:");

        binding.swiperefreshCommon.setEnabled(false);

        wallpaperModels = new ArrayList<>();

            showLoading();
            if(tempList!=null && tempList.size()>0){
                isAPICalling=false;
                wall_used_ids = getUsedIds();
                WallpaperListModel object = new WallpaperListModel();
                object.setStatus(1);
                object.setWallpaper(tempList);
                object.setMsg("success");
                try {
                    onDataSuccess(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                if(getActivity()!=null && getActivity() instanceof MainActivity){
                    if(isPendingForLoad) {
                        isPendingForLoad=false;
                        getWallpaperData();
                    }
                }else
                    getWallpaperData();

            }

            if (binding.swiperefreshCommon != null) {
                binding.swiperefreshCommon.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshWallContent();
                    }
                });
            }
            binding.fabUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.commonRvList.smoothScrollToPosition(0);
                    }
                });
    }

    public void refreshCall() {
        if (binding.swiperefreshCommon != null)
            binding.swiperefreshCommon.post(new Runnable() {
                @Override
                public void run() {
                    if (binding.swiperefreshCommon != null)
                        binding.swiperefreshCommon.setRefreshing(true);
                }
            });

        isNeedToReset = true;
        LoggerCustom.erorr("log", "refresh");
        getWallpaperData();
    }

    public void removeRefresh() {
        if (binding.swiperefreshCommon != null)
            binding.swiperefreshCommon.setRefreshing(false);
    }

    public void refreshWallContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    isNeedToReset = true;
                    wall_used_ids = "";
                    getWallpaperData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.REFRESH_TIME_OUT);
    }

    public void resetContentData() {
        wall_used_ids = "";
//        list.clear();
        currentPage = 1;
        isLoading = true;
        isLastPage = false;
        more_data = true;
        last_Pos = 0;
        dataInsertedCount=0;
    }

    public void getWallpaperData() {

        if (AppUtility.isInterNetworkAvailable(getActivity())) {
            isLoading = true;
            if (isNeedToReset) {
                resetContentData();
                isNeedToReset = false;
            }
            isAPICalling=true;
            if (pType == 0) {
                RetroApiRequest.getWallpaperRetro(mContext, "", wall_used_ids, home_filter + "", this);
            } else if (pType == 1) {
                RetroApiRequest.getTrendingWallpaperRetro(mContext, wall_used_ids, this);
            } else if (pType == 3) {
                RetroApiRequest.getWallpaperRetro(mContext, categoryModel.getCategoryId(), wall_used_ids, "", this);
            } else if (pType == 2) {
                RetroApiRequest.getLiveWallpaperRetro(mContext, wall_used_ids, home_filter + "", this);
            } else if (pType == 5) {
                RetroApiRequest.getPitchBlackWallpaperRetro(mContext, wall_used_ids, this);
            }else if (pType == 8) {
                RetroApiRequest.getSearchApiRequestRetro(mContext, wall_used_ids, tags,false,this);
            }
        } else {
            AppUtility.showInterNetworkDialog(getActivity(), new InternetNetworkInterface() {
                @Override
                public void onOkClick() throws Exception {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.showLastFragment(0);
                    binding.swiperefreshCommon.setRefreshing(false);
                }
            });
        }
    }

    private void onLoadMoreRequested() {
        if (!isLoading && !isLastPage) {
            currentPage += 1;
            getWallpaperData();
        }
    }

    @Override
    public void onDataSuccess(IModel result) throws JSONException {
        WallpaperListModel model = (WallpaperListModel) result;
        if (model.getWallpaper() != null) {
            removeRefresh();
            removeBottomLoading();

            if (currentPage == 1) {
                wallpaperModels.clear();
            }

            wallpaperModels.addAll(model.getWallpaper());

            dataInsertedCount = model.getWallpaper().size();

            boolean pagination_end = false;
            if (model.getWallpaper() == null || pagination_count != model.getWallpaper().size()) {
                pagination_end = true;
            }

            if (!pagination_end) {
                isLastPage = false;
                more_data = true;
            } else {
                isLastPage = true;
                more_data = false;
            }

            wall_used_ids = getUsedIds();
            if (currentPage >= 1 && (model.getWallpaper() == null || model.getWallpaper().size() == 0)) {
                if (commonWallpaperAdapter != null)
                    commonWallpaperAdapter.notifyDataSetChanged();
            } else
                fillUpHomeAdapter();

//            } else {
//                noDataLayout();
//            }
        } else {
            if (commonWallpaperAdapter != null) {
                removeBottomLoading();
                commonWallpaperAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showLoading() {
        binding.rlProgress.setVisibility(View.VISIBLE);
        binding.commonRvList.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.rlProgress.setVisibility(View.GONE);
        binding.commonRvList.setVisibility(View.VISIBLE);
    }

    private void removeBottomLoading() {

        if (wallpaperModels != null && wallpaperModels.size() > 0) {
            if (!TextUtils.isEmpty(wallpaperModels.get(wallpaperModels.size() - 1).getImgId()) && wallpaperModels.get(wallpaperModels.size() - 1).getImgId().equalsIgnoreCase("-99")) {
                wallpaperModels.remove(wallpaperModels.size() - 1);
                dataInsertedCount--;
            }
        }
    }

    private String getUsedIds() {
        String finalString = null;

        StringBuffer ids = new StringBuffer();
        if (wallpaperModels != null)
            for (int i = 0; i < wallpaperModels.size(); i++) {
                if (!TextUtils.isEmpty(wallpaperModels.get(i).getImgId()) && !wallpaperModels.get(i).getImgId().equalsIgnoreCase("-99")) {
                    ids.append("'" + wallpaperModels.get(i).getImgId() + "'");
                    ids.append(",");
                }
            }
        finalString = ids.toString();
        if (!TextUtils.isEmpty(finalString)) {
            finalString = finalString.substring(0, finalString.length() - 1);
        }
        return finalString;
    }

    private void fillUpHomeAdapter() {
        if (wallpaperModels != null && wallpaperModels.size() > 0) {
            if (more_data) {
                WallpaperModel loading = new WallpaperModel();
                loading.setImgId("-99");
                wallpaperModels.add(loading);
                dataInsertedCount++;
            }

            binding.swiperefreshCommon.setEnabled(true);
            binding.commonRvList.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
            binding.rlProgress.setVisibility(View.GONE);

            if (commonWallpaperAdapter == null) {

                boolean isVideo;
                isVideo = pType == 2;

                commonWallpaperAdapter = new CommonWallpaperAdapter(wallpaperModels, mContext, isVideo, "", pType);
                last_Pos = wallpaperModels.size() + 1;

                if (pType == 5) {
                    gridLayoutManager = new GridLayoutManager(getActivity(), Constants.TILE_COLUMN_PITCH);
                } else {
                    gridLayoutManager = new GridLayoutManager(getActivity(), Constants.TILE_COLUMN);
                }

                gridLayoutManager.setOrientation(androidx.recyclerview.widget.GridLayoutManager.VERTICAL);
                if (gridLayoutManager != null)
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            try {
                                if (wallpaperModels != null && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equals("-1000")) {
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                } else if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-99"))
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-1"))
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-5"))
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
//                                else if (list != null && list.size() > 0 && !TextUtils.isEmpty(list.get(position).getImgId()) && list.get(position).getImgId().equalsIgnoreCase("-3"))
//                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-7"))
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && wallpaperModels.get(position).isNative())
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && wallpaperModels.get(position).getNativeAd()!=null)
                                    return pType == 5 ? Constants.TILE_COLUMN_PITCH : Constants.TILE_COLUMN;
                                else
                                    return 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return 1;
                        }
                    });

                binding.commonRvList.setLayoutManager(gridLayoutManager);

                int spacing = (int) getResources()
                        .getDimension(R.dimen.content_padding_recycle);
                binding.commonRvList.addItemDecoration(new SpacesItemDecoration(spacing));
                binding.commonRvList.setItemAnimator(null);
                binding.commonRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int visibleItemCount = gridLayoutManager.getChildCount();
                        int totalItemCount = gridLayoutManager.getItemCount();
                        int firstVisibleItemPositions = gridLayoutManager.findFirstVisibleItemPosition();

                        if ((firstVisibleItemPositions + visibleItemCount) >= totalItemCount && firstVisibleItemPositions >= 0) {
                            onLoadMoreRequested();
                        }

                        if (firstVisibleItemPositions == 0) {
//                            fadeOutAnimation(fab_up, 3000);
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
                binding.commonRvList.setAdapter(commonWallpaperAdapter);
            } else {

                if (currentPage == 1) {
                    commonWallpaperAdapter.notifyDataSetChanged();
                    binding.commonRvList.scrollToPosition(0);
                } else
                    commonWallpaperAdapter.notifyItemRangeInserted(last_Pos, dataInsertedCount);
                last_Pos = wallpaperModels.size() + 1;
            }
            isLoading = false;
        } else {
            noDataLayoutSet();
        }
    }

    private void noDataLayoutSet() {
        if (pType == 4) {
            binding.txtNoData.setText(getString(R.string.letsdownload));
        } else {
            binding.txtNoData.setText(getString(R.string.no_data));
        }
        binding.txtNoData.setVisibility(View.VISIBLE);
        binding.rlProgress.setVisibility(View.GONE);
        binding.swiperefreshCommon.setVisibility(View.GONE);
        binding.commonRvList.setVisibility(View.GONE);
    }

    public WallpaperType getType() {
        return wallpaperType;
    }

    @Override
    public void onDataError(String result) throws Exception {
    }

    @Override
    public void onDestroy() {
        LoggerCustom.erorr("MainActivity", "onDestroy MainFragment");
        super.onDestroy();
        if(commonWallpaperAdapter !=null)
            commonWallpaperAdapter.releaseResource();

        mContext = null;
        commonWallpaperAdapter = null;
        if(wallpaperModels !=null)
            wallpaperModels.clear();
        wallpaperModels = null;
        if(tempList!=null)
            tempList.clear();
        tempList=null;
        wallpaperType = null;
        pType = 0;
        gridLayoutManager = null;
        pagination_count = 0;
        dataInsertedCount = 0;
        isLastPage = false;
        last_Pos = 0;
        more_data = false;
        wall_used_ids = "";
        isLoading = false;
        categoryModel = null;
        isNeedToReset = false;
        unregisterUpdateListListener();
    }

    public void setWallpaperModels(List<WallpaperModel> lst) {
        if (wallpaperModels != null) {
            wallpaperModels.clear();
        }
        wallpaperModels = new ArrayList<>();
        if (wallpaperModels.size() == 0) {
            wallpaperModels.addAll(lst);
            fillUpHomeAdapter();
        }
    }

    private void registerUpdateListListener() {
        EventNotifier notifier = NotifierFactory.getInstance().getNotifier(NotifierFactory.EVENT_NOTIFIER_UPDATE_LIST);
        notifier.registerListener(this, ListenerPriority.PRIORITY_HIGH);

        notifier = NotifierFactory.getInstance().getNotifier(NotifierFactory.EVENT_NOTIFIER_LIST);
        notifier.registerListener(this, ListenerPriority.PRIORITY_HIGH);
    }

    private void unregisterUpdateListListener() {
        EventNotifier notifier = NotifierFactory.getInstance().getNotifier(NotifierFactory.EVENT_NOTIFIER_UPDATE_LIST);
        notifier.unRegisterListener(this);

        notifier = NotifierFactory.getInstance().getNotifier(NotifierFactory.EVENT_NOTIFIER_LIST);
        notifier.unRegisterListener(this);
    }

    @Override
    public int eventNotify(int eventType, Object eventObject) {
        LoggerCustom.erorr("main fragment", "entered");
        int eventState = EventState.EVENT_IGNORED;
        if (eventType == EventTypes.EVENT_INFO_UPDATED) {
            EventModelClass model = (EventModelClass) eventObject;
            if (pType == model.getFragmentType()) {

                LoggerCustom.erorr("main fragment", "entered");

                if (wallpaperModels != null)
                    wallpaperModels.clear();

                if (wallpaperModels.size() == 0) {

                    wallpaperModels.addAll((List<WallpaperModel>) model.getWallpaper());
                    commonWallpaperAdapter.notifyDataSetChanged();

                    binding.commonRvList.smoothScrollToPosition(model.getPosition());

                    LoggerCustom.erorr("main fragment", "entered" + model.getPosition());
                }
            }
        }
        return eventState;
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

    public void resumeAdapterAd(boolean isPauseOriginal){
        if (commonWallpaperAdapter != null) {
            commonWallpaperAdapter.onResume(isPauseOriginal);
        }
    }
    public void pauseAdapterAd(boolean isPauseOriginal){
        if (commonWallpaperAdapter != null) {
            commonWallpaperAdapter.onPause(isPauseOriginal);
        }
    }
}
