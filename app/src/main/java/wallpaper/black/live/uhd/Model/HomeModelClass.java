package wallpaper.black.live.uhd.Model;

import java.util.ArrayList;
import java.util.List;

public class HomeModelClass {

    public static final int TYPE_HOME_COROUSAL =2;
    public static final int TYPE_HORIZONTOL_DATA =0;
    public static final int TYPE_HOME_TAG =6;
    public static final int CATEGORY_LIST_DATA =5;
    public static final int TYPE_FEATURE_BANNER =7;

    public int type_data;
    public String name;
    private List<WallpaperModel> testlist= new ArrayList<WallpaperModel>();
    private List<BannerModel> bannerList= new ArrayList<BannerModel>();
    private List<CategoryDataModel> categoryList= new ArrayList<CategoryDataModel>();
    public String filter;
    public boolean isNormalWallpaper;

    public HomeModelClass(int type_data, String name, List<WallpaperModel> testlist, String filter, boolean isStatic) {
        this.type_data = type_data;
        this.name = name;
        this.isNormalWallpaper =isStatic;
        this.testlist = testlist;
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WallpaperModel> getTestlist() {
        return testlist;
    }

    public void setTestlist(List<WallpaperModel> testlist) {
        this.testlist = testlist;
    }

    public List<BannerModel> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerModel> bannerList) {
        this.bannerList = bannerList;
    }

    public List<CategoryDataModel> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryDataModel> categoryList) {
        this.categoryList = categoryList;
    }

    public BannerModel getBanner_localAd() {
        return banner_localAd;
    }

    public void setBanner_localAd(BannerModel banner_localAd) {
        this.banner_localAd = banner_localAd;
    }

    private BannerModel banner_localAd;
}
