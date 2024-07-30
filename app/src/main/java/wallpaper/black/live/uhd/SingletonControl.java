package wallpaper.black.live.uhd;

import java.util.List;

import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.DataListModel;
import wallpaper.black.live.uhd.Model.HomeDataModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;


public class SingletonControl {
    private List<CategoryDataModel> categoryList = null;
    private List<CategoryDataModel> stockcategoryList = null;
    private List<CategoryDataModel> featuredList = null;
    private List<CategoryDataModel> homeCategoryList = null;
    private List<WallpaperModel> homeLiveList = null;
    private HomeDataModel homeDataModel;
    private DataListModel dataListModel;

    private static SingletonControl ourInstance = null;

    public static SingletonControl getInstance() {
        if (ourInstance == null)
            ourInstance = new SingletonControl();
        return ourInstance;
    }

    public List<CategoryDataModel> getStockcategoryList() {
        return stockcategoryList;
    }

    public void setStockcategoryList(List<CategoryDataModel> stockcategoryList) {
        this.stockcategoryList = stockcategoryList;
    }

    public List<CategoryDataModel> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryDataModel> categoryList) {
        this.categoryList = categoryList;
    }

    public DataListModel getDataList() {
        return dataListModel;
    }

    public void setDataListModel(DataListModel dataListModel) {
        this.dataListModel = dataListModel;
    }

    public List<CategoryDataModel> getFeaturedList() {
        return featuredList;
    }

    public void setFeaturedList(List<CategoryDataModel> featuredList) {
        this.featuredList = featuredList;
    }

    public List<CategoryDataModel> getHomeCategoryList() {
        return homeCategoryList;
    }

    public void setHomeCategoryList(List<CategoryDataModel> homeCategoryList) {
        this.homeCategoryList = homeCategoryList;
    }

    public List<WallpaperModel> getHomeLiveList() {
        return homeLiveList;
    }

    public void setHomeLiveList(List<WallpaperModel> homeLiveList) {
        this.homeLiveList = homeLiveList;
    }

    public HomeDataModel getHomeDataModel() {
        return homeDataModel;
    }

    public void setHomeDataList(HomeDataModel homeData) {
        this.homeDataModel = homeData;
    }
}
