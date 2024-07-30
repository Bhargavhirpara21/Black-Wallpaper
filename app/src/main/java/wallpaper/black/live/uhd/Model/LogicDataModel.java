package wallpaper.black.live.uhd.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogicDataModel implements IModel, Serializable {

    @SerializedName("version_code")
    private String versionCode;
    @SerializedName("need_to_show_ads")
    private String needToShowAds;
    @SerializedName("share_text")
    private String shareText;
    @SerializedName("rate_text")
    private String rateText;
    @SerializedName("needToForceUpdate")
    private String needToForceUpdate;
    @SerializedName("is_update_available")
    private String isUpdateAvailable;
    @SerializedName("under_maintenance")
    private String underMaintenance;
    @SerializedName("ad_disable")
    private String adDisable;
    @SerializedName("apply_playstore")
    private String applyPlaystore;
    @SerializedName("ad_flag")
    private String adFlag;

    public String getAdFreqCountNew() {
        return adFreqCountNew;
    }

    @SerializedName("ad_freq_count_new")
    private String adFreqCountNew;

    @SerializedName("Advertise_link")
    @Expose
    private String Advertise_link;

    @SerializedName("Advertise_img")
    @Expose
    private String Advertise_img;

    @SerializedName("ad_freq_time")
    private String adFreqTime;

    @SerializedName("pagination_limit")
    private String paginationLimit;

    public String getQureka_url() {
        return qureka_url;
    }

    @SerializedName("qureka_url")
    private String qureka_url;

    public String getQureka_link() {
        return qureka_link;
    }

    @SerializedName("qureka_link")
    private String qureka_link;

    @SerializedName("pagination_limit_double")
    private String pagination_limit_double;

    public String getPagination_limit_double() {
        return pagination_limit_double;
    }

    public void setPagination_limit_double(String pagination_limit_double) {
        this.pagination_limit_double = pagination_limit_double;
    }

    @SerializedName("app_version")
    private String appVersion;
    @SerializedName("update_redirect_link")
    private String updateRedirectLink;
    @SerializedName("admob_banner_new_id")
    private String admobBannerNewId;

    public String getAdmobBannerHome() {
        return admobBannerHome;
    }

    @SerializedName("admob_banner_id_home")
    private String admobBannerHome;

    public String getAdmob_banner_id_onboard() {
        return admob_banner_id_onboard;
    }

    @SerializedName("admob_banner_id_onboard")
    private String admob_banner_id_onboard;

    public String getAdmobBannerDetail() {
        return admobBannerDetail;
    }

    @SerializedName("admob_banner_id_detail")
    private String admobBannerDetail;

    @SerializedName("is_banner_detail")
    private String is_banner_detail;

    @SerializedName("is_banner_onboard")
    private String is_banner_onboard;
    @SerializedName("is_banner_langauge")
    private String is_banner_langauge;

    public String getAdmob_rect_banner_id() {
        return admob_rect_banner_id;
    }

    @SerializedName("admob_rect_banner_id")
    private String admob_rect_banner_id;

    public String getAdmob_rect_banner_id_exit() {
        return admob_rect_banner_id_exit;
    }

    @SerializedName("admob_rect_banner_id_exit")
    private String admob_rect_banner_id_exit;


    @SerializedName("admob_interstitial_id")
    private String admobInterstitialId;
    @SerializedName("admob_native_id")
    private String admobNativeId;

    public String getAdmob_native_template_id() {
        return admob_native_template_id;
    }

    @SerializedName("admob_native_template_id")
    private String admob_native_template_id;

    public String getAdmob_exit_native_id() {
        return admob_exit_native_id;
    }

    @SerializedName("admob_exit_native_id")
    private String admob_exit_native_id;

    @SerializedName("admob_rewarded_id")
    private String admobRewardedId;
    @SerializedName("native_ads_show_count")
    private String nativeAdsShowCount;
    @SerializedName("native_ads_frequency_count")
    private String nativeAdsFrequencyCount;
    @SerializedName("check_vpn")
    private String checkVpn;
    @SerializedName("package")
    private String _package;
    @SerializedName("transfer")
    private String transfer;
    @SerializedName("ADS_PER_PAGE")
    private String adsPerPage;
    @SerializedName("admob_open_ad_id")
    private String admobOpenAdId;
    @SerializedName("filter_home")
    private String filterHome;
    @SerializedName("filter_live")
    private String filterLive;

    public String getTransfer_msg() {
        return transfer_msg;
    }

    @SerializedName("transfer_msg")
    private String transfer_msg;

    public String getIs_search() {
        return is_search;
    }

    public String getIs_ad_download() {
        return is_ad_download;
    }

    @SerializedName("is_ad_download")
    private String is_ad_download;

    @SerializedName("is_search")
    private String is_search;

    @SerializedName("is_showcaseshow")
    private String is_showcaseshow;

    public String getIs_auto_changer() {
        return is_auto_changer;
    }

    @SerializedName("is_auto_changer")
    private String is_auto_changer;

    public String getIs_gallery() {
        return is_gallery;
    }

    @SerializedName("is_gallery")
    private String is_gallery;

    @SerializedName("is_in_app")
    private String is_in_app;

    public String getIs_counter_plus() {
        return is_counter_plus;
    }

    @SerializedName("is_counter_plus")
    private String is_counter_plus;

    @SerializedName("admob_splash_inter_id")
    private String admobSplashInterId;
    @SerializedName("img_path")
    private String imgPath;
    @SerializedName("is_update_custom")
    private String isUpdateCustom;
    @SerializedName("is_rating_custom")
    private String isRatingCustom;

    @SerializedName("is_auto_refresh")
    @Expose
    private String is_auto_refresh;

    @SerializedName("OPEN_AD_COUNT")
    private String OPEN_AD_COUNT;

    public String getAm_banner_id() {
        return am_banner_id;
    }

    @SerializedName("am_banner_id")
    @Expose
    private String am_banner_id;

    public String getAm_banner_home() {
        return am_banner_home;
    }

    @SerializedName("am_banner_home")
    @Expose
    private String am_banner_home;

    public String getAm_banner_onboard() {
        return am_banner_onboard;
    }

    @SerializedName("am_banner_onboard")
    @Expose
    private String am_banner_onboard;

    public String getAm_banner_detail() {
        return am_banner_detail;
    }

    @SerializedName("am_banner_detail")
    @Expose
    private String am_banner_detail;

    public String getAm_open_id() {
        return am_open_id;
    }

    @SerializedName("am_open_id")
    @Expose
    private String am_open_id;

    public String getAm_native_template_id() {
        return am_native_template_id;
    }

    @SerializedName("am_native_template_id")
    @Expose
    private String am_native_template_id;

    public String getAm_native_direct_id() {
        return am_native_direct_id;
    }

    @SerializedName("am_native_direct_id")
    @Expose
    private String am_native_direct_id;

    @SerializedName("is_interstitial_splash")
    @Expose
    private String is_interstitial_splash;

    @SerializedName("native_new_logic")
    private String native_new_logic;

    @SerializedName("need_display_remove_dlg")
    private String need_display_remove_dlg;

    @SerializedName("is_language_show")
    private String is_language_show;

    @SerializedName("api_call_lang_change")
    private String api_call_lang_change;

    @SerializedName("lang_show_india")
    private String lang_show_india;

    public boolean getlang_show_india() {
        try {
            return lang_show_india.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getis_in_app() {
        try {
            return is_in_app.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getApi_call_lang_change() {
        try {
            return api_call_lang_change.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getIs_language_show_home() {
        try {
            return is_language_show_home.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SerializedName("is_edit_show")
    private String is_edit_show;

    public boolean is_edit_show() {
        try {
            return is_edit_show.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SerializedName("is_language_show_home")
    private String is_language_show_home;

    @SerializedName("is_first_language_set")
    private String is_first_language_set;
    public boolean is_first_language_set() {
        try {
            return is_first_language_set.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean is_language_show() {
        try {
            return is_language_show.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean IsNativeNewLogic() {
        try {
            return native_new_logic.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean IsDisplayDlgRemove() {
        try {
            return need_display_remove_dlg.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean Is_banner_detail() {
        try {
            return is_banner_detail.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean Is_banner_onboard() {
        try {
            return is_banner_onboard.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean Is_banner_Languege() {
        try {
            return is_banner_langauge.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean IsInterstiitalSplash() {
        try {
            return is_interstitial_splash.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean is_Showcaseshow() {
        try {
            return is_showcaseshow.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean is_exit_dlg() {
        try {
            return is_exit_dlg.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean is_exit_Native_Ad() {
        try {
            return is_exit_Native_ad.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean is_notication_ask_disable() {
        try {
            return is_notification_ask_disable.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SerializedName("is_exit_Native_ad")
    @Expose
    private String is_exit_Native_ad;


    @SerializedName("is_notification_ask_disable")
    @Expose
    private String is_notification_ask_disable;

    @SerializedName("is_exit_dlg")
    @Expose
    private String is_exit_dlg;

    @SerializedName("is_rect_video")
    @Expose
    private String is_rect_video;

    @SerializedName("is_stock_home")
    @Expose
    private String is_stock_home;

    @SerializedName("is_stock_category")
    @Expose
    private String is_stock_category;

    @SerializedName("is_black_screen_unable")
    @Expose
    private String is_black_screen_unable;

    @SerializedName("keyword")
    @Expose
    private String keyword;


    public String getPitchblack_img() {
        return pitchblack_img;
    }

    public String getBlackscreen_img() {
        return blackscreen_img;
    }

    public String getStock_banner() {
        return stock_banner;
    }

    @SerializedName("pitchblack_img")
    @Expose
    private String pitchblack_img;

    @SerializedName("blackscreen_img")
    @Expose
    private String blackscreen_img;

    @SerializedName("stock_banner")
    @Expose
    private String stock_banner;


    public String getDouble_banner() {
        return double_banner;
    }

    @SerializedName("double_banner")
    @Expose
    private String double_banner;

    public String getPureblack_img() {
        return pureblack_img;
    }

    @SerializedName("pureblack_img")
    @Expose
    private String pureblack_img;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean getis_black_unable() {
        try {
            return is_black_screen_unable.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getis_stock_home() {
        try {
            return is_stock_home.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getis_stock_category() {
        try {
            return is_stock_category.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SerializedName("am_splash_interstitial")
    @Expose
    private String am_splash_interstitial;

    public String getApplovin_intertitial() {
        return applovin_intertitial;
    }

    @SerializedName("applovin_intertitial")
    @Expose
    private String applovin_intertitial;

    public String getApplovin_Banner() {
        return applovin_Banner;
    }

    @SerializedName("applovin_Banner")
    @Expose
    private String applovin_Banner;

    public String getAm_interstitial_id() {
        return am_interstitial_id;
    }

    public String getAm_splash_interstitial() {
        return am_splash_interstitial;
    }

    @SerializedName("am_interstitial_id")
    @Expose
    private String am_interstitial_id;

    public String getAm_rect_banner() {
        return am_rect_banner;
    }

    @SerializedName("is_reward_gift")
    @Expose
    private String is_reward_gift;

    public String get_is_reward_gift() {
//        return "1";
        return is_reward_gift;
    }

    @SerializedName("am_rect_banner")
    @Expose
    private String am_rect_banner;

    public String getApplovin_rect_banner() {
        return applovin_rect_banner;
    }

    @SerializedName("applovin_rect_banner")
    @Expose
    private String applovin_rect_banner;

    public String getAm_rect_banner_exit() {
        return am_rect_banner_exit;
    }

    @SerializedName("am_rect_banner_exit_new")
    @Expose
    private String am_rect_banner_exit;

    public boolean isAutoRefreshAdmobBanner() {
        try {
            return is_auto_refresh.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setIsUpdateCustom(String isUpdateCustom) {
        this.isUpdateCustom = isUpdateCustom;
    }

    public void setIsRatingCustom(String isRatingCustom) {
        this.isRatingCustom = isRatingCustom;
    }

    public String getIsUpdateCustom() {
        return isUpdateCustom;
    }

    public String getIsRatingCustom() {
        return isRatingCustom;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getNeedToShowAds() {
        return needToShowAds;
    }

    public void setNeedToShowAds(String needToShowAds) {
        this.needToShowAds = needToShowAds;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getRateText() {
        return rateText;
    }

    public void setRateText(String rateText) {
        this.rateText = rateText;
    }

    public String getNeedToForceUpdate() {
        return needToForceUpdate;
    }

    public void setNeedToForceUpdate(String needToForceUpdate) {
        this.needToForceUpdate = needToForceUpdate;
    }

    public String getIsUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setIsUpdateAvailable(String isUpdateAvailable) {
        this.isUpdateAvailable = isUpdateAvailable;
    }

    public String getUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(String underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public String getAdDisable() {
        return adDisable;
    }

    public void setAdDisable(String adDisable) {
        this.adDisable = adDisable;
    }

    public String getApplyPlaystore() {
        return applyPlaystore;
    }

    public void setApplyPlaystore(String applyPlaystore) {
        this.applyPlaystore = applyPlaystore;
    }

    public String getAdFlag() {
        return adFlag;
    }

    public void setAdFlag(String adFlag) {
        this.adFlag = adFlag;
    }

    public String getAdFreqTime() {
        return adFreqTime;
    }

    public void setAdFreqTime(String adFreqTime) {
        this.adFreqTime = adFreqTime;
    }

    public String getPaginationLimit() {
        return paginationLimit;
    }

    public void setPaginationLimit(String paginationLimit) {
        this.paginationLimit = paginationLimit;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getUpdateRedirectLink() {
        return updateRedirectLink;
    }

    public void setUpdateRedirectLink(String updateRedirectLink) {
        this.updateRedirectLink = updateRedirectLink;
    }

    public String getAdmobBannerNewId() {
        return admobBannerNewId;
    }

    public void setAdmobBannerNewId(String admobBannerNewId) {
        this.admobBannerNewId = admobBannerNewId;
    }

    public String getAdmobInterstitialId() {
        return admobInterstitialId;
    }

    public void setAdmobInterstitialId(String admobInterstitialId) {
        this.admobInterstitialId = admobInterstitialId;
    }

    public String getAdmobNativeId() {
//        return "dd";
        return admobNativeId;
    }

    public void setAdmobNativeId(String admobNativeId) {
        this.admobNativeId = admobNativeId;
    }

    public String getAdmobRewardedId() {
        return admobRewardedId;
    }

    public void setAdmobRewardedId(String admobRewardedId) {
        this.admobRewardedId = admobRewardedId;
    }

    public int getNativeAdsShowCount() {
        try {
            return Integer.parseInt(nativeAdsShowCount);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return -1;
    }
    @SerializedName("am_native_id")
    @Expose
    private String am_native_id;

    public String getAm_native_id() {
        return am_native_id;
    }

    public void setNativeAdsShowCount(String nativeAdsShowCount) {
        this.nativeAdsShowCount = nativeAdsShowCount;
    }

    public String getAm_exit_native_id() {
        return am_exit_native_id;
    }

    @SerializedName("am_exit_native_id")
    @Expose
    private String am_exit_native_id;
    @SerializedName("is_live_exoplayer")
    @Expose
    private String is_live_exoplayer;

    public String getIs_live_exoplayer() {
        return is_live_exoplayer;
//        return "0";
    }

    public void setIs_live_exoplayer(String is_live_exoplayer) {
        this.is_live_exoplayer = is_live_exoplayer;
    }

    public int getNativeAdsFrequencyCount() {
        try {
            return Integer.parseInt(nativeAdsFrequencyCount);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setNativeAdsFrequencyCount(String nativeAdsFrequencyCount) {
        this.nativeAdsFrequencyCount = nativeAdsFrequencyCount;
    }

    public String getCheckVpn() {
        return checkVpn;
    }

    public void setCheckVpn(String checkVpn) {
        this.checkVpn = checkVpn;
    }

    public String get_package() {
        return _package;
    }

    public void set_package(String _package) {
        this._package = _package;
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public int getAdsPerPage() {
        int temp = 1;
        try {
            temp = Integer.parseInt(adsPerPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public int getOpenAdCount() {
        int temp = 1;
        try {
            temp = Integer.parseInt(OPEN_AD_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }


    public void setAdsPerPage(String adsPerPage) {
        this.adsPerPage = adsPerPage;
    }

    public String getAdmobOpenAdId() {
        return admobOpenAdId;
    }

    public void setAdmobOpenAdId(String admobOpenAdId) {
        this.admobOpenAdId = admobOpenAdId;
    }

    public String getFilterHome() {
        return filterHome;
    }

    public void setFilterHome(String filterHome) {
        this.filterHome = filterHome;
    }

    public String getFilterLive() {
        return filterLive;
    }

    public void setFilterLive(String filterLive) {
        this.filterLive = filterLive;
    }

    public String getAdmobSplashInterId() {
        return admobSplashInterId;
    }

    public void setAdmobSplashInterId(String admobSplashInterId) {
        this.admobSplashInterId = admobSplashInterId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @SerializedName("is_secret_key_passed")
    @Expose
    private String is_secret_key_passed;

    public boolean IsSecretPassed() {
        try {
            return is_secret_key_passed.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getAdvertise_img() {
        return Advertise_img;
    }

    public String getAdvertise_link() {
        return Advertise_link;
    }

    @SerializedName("is_ironsource_splash")
    @Expose
    private String is_ironsource_splash;

    @SerializedName("is_ironsource_second")
    @Expose
    private String is_ironsource_second;

    @SerializedName("is_ironsource_display")
    @Expose
    private String is_ironsource_display;

    public boolean isIronSourceSplash() {
        try {
            return is_ironsource_splash.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isIronSourceSecond() {
        try {
            return is_ironsource_second.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isIronSourceEnable() {
        try {
            return is_ironsource_display.equalsIgnoreCase("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
