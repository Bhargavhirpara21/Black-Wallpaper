package wallpaper.black.live.uhd.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.CarousalRecyclerPager;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.AppUtils.TagsFlowLayout;
import wallpaper.black.live.uhd.AutoChangerActivity;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.MainActivity;
import wallpaper.black.live.uhd.Model.BannerModel;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.HomeModelClass;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.MoreWallpaperActivity;
import wallpaper.black.live.uhd.PitchWallpaperActivity;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.SingletonControl;
import wallpaper.black.live.uhd.StockWallpaperActivity;

public class FeatureHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HomeModelClass> homeModelClasses;
    private Context mContext;
    private InAppPreference inAppPreference;
    private CarousalViewHolderMain carousalViewHolderMain;
    private List<CategoryDataModel> categoryModels = new ArrayList<CategoryDataModel>();
    private CategoryListAdapter carousalAdapter;
    private CarousalRecyclerPager carousalRecyclerPager;
    private StockWallpaperAdapter stockcataAdapter;
    public FeatureHomeAdapter(List<HomeModelClass> list, Context context) {
        this.homeModelClasses = list;
        this.mContext = context;
        setCategory();
    }

    public void setCategory() {
        if (this.categoryModels == null)
            this.categoryModels = new ArrayList<CategoryDataModel>();

        if (this.categoryModels != null)
            this.categoryModels.clear();

        if (homeModelClasses != null) {
            if(homeModelClasses.get(0).getCategoryList()!=null && homeModelClasses.get(0).getCategoryList().size()>0)
                this.categoryModels.addAll(homeModelClasses.get(0).getCategoryList());
        }

        CategoryDataModel temp = new CategoryDataModel();
        temp.setCategoryId("-1");
        temp.setName("Pitch Black Wallpaper");
        try {
            temp.setImagePath(SingletonControl.getInstance().getDataList().getLogic().getPitchblack_img());
        } catch (Exception e) {
            e.printStackTrace();
            temp.setImagePath("pitchgrey_black.jpg");
        }
        this.categoryModels.add(temp);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HomeModelClass.TYPE_HOME_COROUSAL:
                if (carousalViewHolderMain != null) {
                    return carousalViewHolderMain;
                }
                View carousalItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category_carousal, null);
                carousalViewHolderMain = new CarousalViewHolderMain(carousalItemLayoutView);
                carousalItemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return carousalViewHolderMain;

            case HomeModelClass.TYPE_FEATURE_BANNER:
                View stockCarousalItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_banner, parent, false);
                StockViewHolderMain stockCarousalViewHolder = new StockViewHolderMain(stockCarousalItemLayoutView);
                stockCarousalItemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return stockCarousalViewHolder;

            case HomeModelClass.CATEGORY_LIST_DATA:

                View homeCateItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_item_carousal, parent, false);
                HomeCategoriesViewHolder homeCategoriesViewHolder = new HomeCategoriesViewHolder(homeCateItemLayoutView);
                homeCateItemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return homeCategoriesViewHolder;

            case HomeModelClass.TYPE_HORIZONTOL_DATA:
                View liveWallsItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_item_carousal, parent, false);
                HomeLiveWallsViewHolder liveWallsViewHolder = new HomeLiveWallsViewHolder(liveWallsItemLayoutView);
                liveWallsItemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return liveWallsViewHolder;

            case HomeModelClass.TYPE_HOME_TAG:
                View popularTagsItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_tag, parent, false);
                HomePopularTagsViewHolder popularTagsViewHolder = new HomePopularTagsViewHolder(popularTagsItemLayoutView);
                popularTagsItemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return popularTagsViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeModelClass object = homeModelClasses.get(position);
        inAppPreference = InAppPreference.getInstance(mContext);
        final int type = getItemViewType(position);

        switch (type) {
            case HomeModelClass.TYPE_HOME_COROUSAL:
                initViewPager((CarousalViewHolderMain) holder);
                break;
            case HomeModelClass.TYPE_FEATURE_BANNER:
                StockViewHolderMain stockViewHolder = (StockViewHolderMain) holder;
                BannerModel banner=object.getBannerList().get(0);
                stockViewHolder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(banner.getType_id().equalsIgnoreCase("5")){ // stock
                            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_STOCK_WALLPAPER, FirebaseEventManager.ATR_KEY_STOCK_CATEGORY, "view all category");
                            Intent intent = new Intent(mContext, StockWallpaperActivity.class);
                            mContext.startActivity(intent);
                        }else if(banner.getType_id().equalsIgnoreCase("3")){ //double
                            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.ATR_KEY_HOME, "View All", "Double Banner");

                            ((MainActivity) mContext).ChangeFragment(2);
                        }else if(banner.getType_id().equalsIgnoreCase("8")){ //Pure black
                            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_PURE_BLACK, "Open", "Home");
                            // temp
//                            context.startActivity(new Intent(context, PureBlackWallpaperActivity.class));
                        }else if(banner.getType_id().equalsIgnoreCase("6")){ // auto wall
                            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_POLICY);
                            if (AppUtility.isInterNetworkAvailable(mContext)) {
                                mContext.startActivity(new Intent(mContext, AutoChangerActivity.class));
                            } else {
                                AppUtility.showInterNetworkDialog(mContext, new InternetNetworkInterface() {
                                    @Override
                                    public void onOkClick() throws Exception {
                                    }
                                });
                            }
                        }else if(banner.getType_id().equalsIgnoreCase("7")){ // Pitchblack
                            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_PITCH_BLACK, FirebaseEventManager.ATR_KEY_CATEGORY_SELECTED, "Home");
                            Intent intent = new Intent(mContext, PitchWallpaperActivity.class);
                            Log.d("Catagory", "onClick: Pitch");
                            mContext.startActivity(intent);
                        }
//                        else if(banner.getType_id().equalsIgnoreCase("9")){ // From Gallery
//                            EventManager.sendEvent(EventManager.LBL_GALLERY, "Open", "Home");
//                            context.startActivity(new Intent(context, GalleryActivity.class));
//                        }
                        else if(banner.getType_id().equalsIgnoreCase("10")){ // From Gallery
                            FirebaseEventManager.sendEventFirebase(FirebaseEventManager.ATR_KEY_HOME, "Category", "Open");
                            Intent intent = new Intent(mContext, MoreWallpaperActivity.class);
                            intent.putExtra("from", "category");
                            CategoryDataModel categoryModel=new CategoryDataModel();
                            categoryModel.setCategoryId(banner.getCategory_id());
                            categoryModel.setName(banner.getCat_name());
                            intent.putExtra("object", categoryModel);
                            mContext.startActivity(intent);
                        }
                    }
                });

                Glide.with(mContext)
                        .load(banner.getPath())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .thumbnail(0.1f)
                        .apply(new RequestOptions())
                        .into(stockViewHolder.img);
                break;
            case HomeModelClass.CATEGORY_LIST_DATA:
                homeCategoriesInitViewPager((HomeCategoriesViewHolder) holder,object.getCategoryList(),object.getName());
                break;
            case HomeModelClass.TYPE_HORIZONTOL_DATA:
                liveWallInitViewPager((HomeLiveWallsViewHolder) holder,object);
                break;
            case HomeModelClass.TYPE_HOME_TAG:
                homePopularTags((HomePopularTagsViewHolder) holder,object);
                break;
        }
    }

    protected void initViewPager(final CarousalViewHolderMain carousalViewHolder) {
        if (categoryModels != null && categoryModels.size() > 0) {
            carousalViewHolder.itemView.findViewById(R.id.ll_CarousalParent).setVisibility(View.VISIBLE);
            if (carousalAdapter != null)
                return;
            carousalRecyclerPager = carousalViewHolder.vpRecyclerView;

            final LinearLayoutManager layout = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            carousalRecyclerPager.setLayoutManager(layout);
            layout.setSmoothScrollbarEnabled(true);
            carousalAdapter = new CategoryListAdapter(mContext, categoryModels, CategoryListAdapter.CARAOUSAL_VIEW);
            carousalRecyclerPager.setAdapter(carousalAdapter);
//            mRecyclerView.setHasFixedSize(true);
            carousalRecyclerPager.setLongClickable(true);
            carousalRecyclerPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean isUserScrolled = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            System.out.println("Spotlight Scroll SCROLL_STATE_IDLE ::::" + isUserScrolled);
                            isUserScrolled = false;
                            //updateTopBottomColor();
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            isUserScrolled = true;
                            System.out.println("Spotlight Scroll SCROLL_STATE_DRAGGING ::::" + isUserScrolled);
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            System.out.println("Spotlight Scroll SCROLL_STATE_SETTLING ::::" + isUserScrolled);
                            break;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {

                }
            });
            carousalRecyclerPager.addOnPageChangedListener(new CarousalRecyclerPager.OnPageChangedListener() {
                @Override
                public void OnPageChanged(int oldPosition, int newPosition) {
                    Log.d("test", "oldPosition:" + oldPosition + " newPosition:" + newPosition);
                    if (newPosition == 0) {
                        carousalViewHolder.vpRecyclerView.setMillisecondsPerInch(100f);
                    }
                    startCarouselScroll();
                }
            });

            //if (homeListingData != null && homeListingData.getContent() != null && homeListingData.getContent().size() > 1) {
            startCarouselScroll();
            //}HomeFragment
        } else {
            carousalViewHolder.itemView.findViewById(R.id.ll_CarousalParent).setVisibility(View.GONE);
        }
    }

    protected void homeCategoriesInitViewPager(final HomeCategoriesViewHolder homeCategoriesViewHolder, List<CategoryDataModel> stockCategoryList, String titile) {

        if (stockCategoryList != null && stockCategoryList.size() > 0) {
            homeCategoriesViewHolder.itemView.findViewById(R.id.rl_tag_item).setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(titile)) {
//                if(titile.equalsIgnoreCase("Top Category")){
//                    homeCategoriesViewHolder.title_txt.setText(R.string.top_categoryy);
//                }else
                    homeCategoriesViewHolder.title_txt.setText("" + titile);
            }
            if (stockcataAdapter != null)
                return;
            RecyclerView stockWallRecView = homeCategoriesViewHolder.stockVpRecView;

            final LinearLayoutManager layout = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            stockWallRecView.setLayoutManager(layout);
            layout.setSmoothScrollbarEnabled(true);

            stockcataAdapter = new StockWallpaperAdapter(mContext, stockCategoryList, 1);
            stockWallRecView.setAdapter(stockcataAdapter);
//            stockWallRecView.setHasFixedSize(true);
            stockWallRecView.setLongClickable(true);
            homeCategoriesViewHolder.relativelayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.ATR_KEY_HOME, "View All", "Category");
                    ((MainActivity) mContext).ChangeFragment(3);
                }
            });
        } else {
            homeCategoriesViewHolder.itemView.findViewById(R.id.rl_tag_item).setVisibility(View.GONE);
        }
    }

    protected void liveWallInitViewPager(final HomeLiveWallsViewHolder liveWallsViewHolder, HomeModelClass object) {

        List<WallpaperModel> wallpaperList = object.getTestlist();

        if (wallpaperList != null && wallpaperList.size() > 0) {
            liveWallsViewHolder.itemView.findViewById(R.id.rl_tag_item).setVisibility(View.VISIBLE);
            RecyclerView stockWallRecView = liveWallsViewHolder.stockVpRecView;

            final LinearLayoutManager layout = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            stockWallRecView.setLayoutManager(layout);
            layout.setSmoothScrollbarEnabled(true);

            HomeLiveAdapter homeLiveAdapter;
            if (!object.isNormalWallpaper) {
                homeLiveAdapter = new HomeLiveAdapter(mContext, wallpaperList);
                homeLiveAdapter.setLiveWallpaper(true);
            }else {
                homeLiveAdapter = new HomeLiveAdapter(mContext, wallpaperList);
            }

            stockWallRecView.setAdapter(homeLiveAdapter);
            stockWallRecView.setLongClickable(true);
            liveWallsViewHolder.title_txt.setText(object.getName());

            liveWallsViewHolder.relativelayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!object.isNormalWallpaper) {
                        FirebaseEventManager.sendEventFirebase(FirebaseEventManager.ATR_KEY_HOME, "View All", "Live Wallpaper");
                        ((MainActivity) mContext).ChangeFragment(1);
                    } else if (object.isNormalWallpaper){
                        Intent intent = new Intent(mContext, MoreWallpaperActivity.class);
                        intent.putExtra("from", "viewall");
                        intent.putExtra("list", (Serializable) object.getTestlist());
                        intent.putExtra("filter", object.getFilter());
                        intent.putExtra("tags", object.name);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            liveWallsViewHolder.itemView.findViewById(R.id.rl_tag_item).setVisibility(View.GONE);
        }
    }

    protected void homePopularTags(final HomePopularTagsViewHolder popularTagsViewHolder, HomeModelClass object) {
        String tags;
        if (SingletonControl.getInstance() != null && SingletonControl.getInstance().getDataList() != null && SingletonControl.getInstance().getDataList().getLogic() != null) {
            tags = SingletonControl.getInstance().getDataList().getLogic().getKeyword();
        } else {
            tags = inAppPreference.getKeywordTags();
        }
        if (!TextUtils.isEmpty(tags))
            InAppPreference.getInstance(mContext).setKeywordTags(tags);

        Log.e("WALLPAPERADAPTER", "popularTags: " + tags);
        popularTagsViewHolder.txt_tags_title.setText(object.getName());
        popularTagsViewHolder.itemView.findViewById(R.id.rl_pupularTag).setVisibility(View.VISIBLE);
        TagsFlowLayout flowLayout = popularTagsViewHolder.itemView.findViewById(R.id.flow_tags);
        List<String> populartags1 = Arrays.asList(tags.split("#"));
        if (flowLayout != null) {
            flowLayout.removeAllViews();
        }
        fillAutoSpacingLayout(populartags1, flowLayout);
        Log.e("WALLPAPERADAPTER", "popularTags: " + populartags1);
    }

    private void fillAutoSpacingLayout(List<String> populartags, TagsFlowLayout flowLayout) {
        for (String text : populartags) {
            TextView textView = buildLabel(text);
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            flowLayout.addView(textView);
        }
    }

    private TextView buildLabel(String text) {
        TextView textView = new TextView(mContext);
        textView.setText(text.replace("#", ""));
        textView.setTag(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setPadding((int) dpToPx(14), (int) dpToPx(4), (int) dpToPx(14), (int) dpToPx(4));
        textView.setBackgroundResource(R.drawable.label_bg);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tgs = (String) view.getTag();
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.ATR_KEY_HOME, FirebaseEventManager.LBL_HOME_TAGS, "" + tgs);
                Intent intent = new Intent(mContext, MoreWallpaperActivity.class);
                intent.putExtra("tags", tgs);
                mContext.startActivity(intent);
            }
        });
        return textView;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }

    public void startCarouselScroll() {
        if (carousalAdapter != null && carousalViewHolderMain != null && carousalViewHolderMain.vpRecyclerView != null && carousalAdapter.getItemCount() > 1) {
            stopCarouselScroll();
            LoggerCustom.i("HomeContentListFragment", "Carousel Scrolling Start");
            handler.postDelayed(runnable, AppUtility.CAROUSEL_ROTATE_TIME);
        }
    }

    public void stopCarouselScroll() {
        LoggerCustom.i("HomeContentListFragment", "Carousel Scrolling Stop");
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (carousalAdapter != null && carousalViewHolderMain != null && carousalViewHolderMain.vpRecyclerView != null && categoryModels != null && categoryModels.size() > 0) {
                    if (carousalAdapter.getItemCount() == carousalViewHolderMain.vpRecyclerView.getCurrentPosition() + 1) {
                        if (AppUtility.CAROUSEL_AUTO_ROTATE) {
                            carousalViewHolderMain.vpRecyclerView.setMillisecondsPerInch(25f);
                            carousalViewHolderMain.vpRecyclerView.smoothScrollToPosition(0);
                            //carousalViewHolder.vpRecyclerView.getLayoutManager().smoothScrollToPosition();scrollToPosition(0);
                        }
                        return;
                    }
                    carousalViewHolderMain.vpRecyclerView.smoothScrollToPosition(carousalViewHolderMain.vpRecyclerView.getCurrentPosition() + 1);
                    //startCarouselScroll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public int getItemViewType(int position) {
        switch (homeModelClasses.get(position).type_data) {
            case 0:
                return HomeModelClass.TYPE_HORIZONTOL_DATA;
            case 2:
                return HomeModelClass.TYPE_HOME_COROUSAL;
            case 5:
                return HomeModelClass.CATEGORY_LIST_DATA;
            case 6:
                return HomeModelClass.TYPE_HOME_TAG;
            case 7:
                return HomeModelClass.TYPE_FEATURE_BANNER;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return homeModelClasses.size();
    }

    public static class StockViewHolderMain extends RecyclerView.ViewHolder {
        ImageView img;
        private TextView filterName;
        RelativeLayout liveFilterLyt;

        public StockViewHolderMain(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.stock_item_img);
            filterName = itemView.findViewById(R.id.filtername);
            liveFilterLyt = itemView.findViewById(R.id.liveFilterLyt);
            filterName.setVisibility(View.GONE);
            liveFilterLyt.setVisibility(View.GONE);
        }
    }


    private class CarousalViewHolderMain extends RecyclerView.ViewHolder {
        public CarousalRecyclerPager vpRecyclerView;

        public CarousalViewHolderMain(View itemView) {
            super(itemView);
            vpRecyclerView = itemView.findViewById(R.id.category_recyclerView);

            int screenWidth = AppUtility.getScreenWidth((Activity) mContext);// - 2 * ((int) activity.getResources().getDimension(R.dimen.padding_carousal_item));
            int height = (int) (screenWidth / 2.7f);

            LinearLayout llCarousalParent = itemView.findViewById(R.id.ll_CarousalParent);
            llCarousalParent.getLayoutParams().height = height;

        }
    }

    private class HomeCategoriesViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView stockVpRecView;
        public TextView  title_txt;
        private RelativeLayout relativelayout1;
        public HomeCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            relativelayout1 = itemView.findViewById(R.id.rl_tag_item);
            stockVpRecView = itemView.findViewById(R.id.stock_RecView);
            title_txt = itemView.findViewById(R.id.title_txt);
            stockVpRecView.setClipToPadding(false);
        }
    }

    private class HomeLiveWallsViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView stockVpRecView;
        public TextView  title_txt;
        private RelativeLayout relativelayout1;

        public HomeLiveWallsViewHolder(@NonNull View itemView) {
            super(itemView);
            stockVpRecView = itemView.findViewById(R.id.stock_RecView);
            relativelayout1 = itemView.findViewById(R.id.rl_tag_item);
            title_txt = itemView.findViewById(R.id.title_txt);
            stockVpRecView.setClipToPadding(false);
            stockVpRecView.setPadding((int) mContext.getResources().getDimension(R.dimen.home_padding_left),0,0,0);

        }
    }

    public static class HomePopularTagsViewHolder extends RecyclerView.ViewHolder {
        TextView txt_tags_title;
        public HomePopularTagsViewHolder(@NonNull View tagview) {
            super(tagview);
            txt_tags_title = tagview.findViewById(R.id.txt_tags_title);
        }
    }

    public void onPause(boolean isPauseOriginal) {
//        try {
//            if (mAdManagerAdView != null && isPauseOriginal) {
//                Logger.e("CategoryListAdapter", "onPause");
//                mAdManagerAdView.pause();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onResume(boolean isPauseOriginal) {
//        try {
//            if (mAdManagerAdView != null && isPauseOriginal) {
//                Logger.e("CategoryListAdapter", "onResume");
//                mAdManagerAdView.resume();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void releaseResource() {
        if (stockcataAdapter != null) {
            stockcataAdapter.releaseResource();
        }
        mContext = null;
        inAppPreference = null;
        carousalViewHolderMain = null;
    }
}
