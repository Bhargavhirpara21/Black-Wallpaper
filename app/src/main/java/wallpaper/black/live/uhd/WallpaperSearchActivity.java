package wallpaper.black.live.uhd;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import wallpaper.black.live.uhd.Adapters.CommonWallpaperAdapter;
import wallpaper.black.live.uhd.Adapters.KeywordHistoryAdapter;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.AppUtils.SearchKeywordDBHelper;
import wallpaper.black.live.uhd.AppUtils.SpacesItemDecoration;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.SearchKeywordModel;
import wallpaper.black.live.uhd.Model.WallpaperListModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.databinding.ActivitySearchWallpaperBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class WallpaperSearchActivity extends BaseActivity implements RetroCallbacks, KeywordHistoryAdapter.OnItemClicked {

    private List<WallpaperModel> wallpaperModels;
    private String used_ids = "";
    private String search_last = "";
    private CommonWallpaperAdapter commonWallpaperAdapter;
    private static final String TAG = "SearchActivity1";
    private int pagination_count = 30;
    private int dataInsertedCount;
    private int currentPage = PAGE_START;
    private static final int PAGE_START = 1;
    private boolean isLastPage = false;
    private boolean isLoading;
    private boolean moreData = false;
    private int lastPos = 0;
    private SearchKeywordDBHelper searchKeywordDBHelper;
    private ArrayList<SearchKeywordModel> searchKeywordModels;
    private KeywordHistoryAdapter keywordHistoryAdapter;
    private boolean isKeyWordAdapter = true;
    private boolean isSpacing = true;
    ActivitySearchWallpaperBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wallpaperModels = new ArrayList<>();
        searchKeywordModels = new ArrayList<>();
        searchKeywordDBHelper = new SearchKeywordDBHelper(this);
        fillUpSearcKeywordList();

        findViewById(R.id.back1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.close_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editSearch.setText("");
                search_last = "";
            }
        });

        binding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.ATR_KEY_SEARCH_SCREEN, "Search Perform", "Click");
                    AppUtility.hideKeyboard(WallpaperSearchActivity.this);
                    if (v.getText().length() != 0) {
                        if (search_last != null && !search_last.isEmpty()) {
                            int id = searchKeywordDBHelper.InsertKeyWord(search_last);
                            SearchKeywordModel modal = new SearchKeywordModel(id, search_last);
                            searchKeywordModels.add(modal);
                            keywordHistoryAdapter.updateData(searchKeywordModels);
                        }
                        resetContent();
                        findViewById(R.id.txt_nodata).setVisibility(View.GONE);
                        getWallpaperData();
                    } else {
                        noDataLayout();
                    }
                    findViewById(R.id.txt_nodata).setVisibility(View.GONE);
                    if (binding.editSearch.getText().toString().isEmpty()) {
                        fillUpSearcKeywordList();
                    }
                    LoggerCustom.erorr(TAG, "click");
                    return true;
                }
                return false;
            }
        });

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LoggerCustom.erorr(TAG, "string" + s.toString().length());
                search_last = s.toString();
                if (binding.editSearch.getText().toString().isEmpty()) {
                    findViewById(R.id.search_RecView).setVisibility(View.VISIBLE);
                    if (wallpaperModels != null) {
                        Iterator<WallpaperModel> itr = wallpaperModels.iterator();
                        int pos = 0;
                        while (itr.hasNext()) {
                            itr.next();
                            if (pos >= 1) {
                                itr.remove();
                            }
                            pos++;
                        }
                        Log.e(TAG, "onSuccess size: " + wallpaperModels.size());
                        if (commonWallpaperAdapter != null) {
                            commonWallpaperAdapter.notifyDataSetChanged();
                        }
                        fillUpSearcKeywordList();
                    }
                    findViewById(R.id.txt_nodata).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fillUpSearcKeywordList() {
        searchKeywordModels = searchKeywordDBHelper.getAllKeywordData();
        Collections.reverse(searchKeywordModels);

        if (searchKeywordModels != null) {
            wallpaperModels.clear();
            Log.e(TAG, "FillUpKeywordList: " + searchKeywordModels);

//            if (hisAdapter == null) {
            isKeyWordAdapter = true;
            keywordHistoryAdapter = new KeywordHistoryAdapter(this, searchKeywordModels, this);
            binding.searchRecView.setHasFixedSize(true);
            GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 1);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//            recyclerView.addOnScrollListener(null);
            binding.searchRecView.setLayoutManager(linearLayoutManager);
            binding.searchRecView.setAdapter(keywordHistoryAdapter);
            hideLoading();
//            } else {
//                hisAdapter.notifyDataSetChanged();
//            }
        } else {
            noDataLayout();
        }
    }

    private void resetContent() {
        binding.searchRecView.scrollToPosition(0);
//        search_last = "";
        used_ids = "";
        isLastPage = false;
        currentPage = 1;
        moreData = true;
        isLoading = true;
        lastPos = 0;
    }

    private void noDataLayout() {
        TextView v = findViewById(R.id.txt_nodata);
        v.setText(getString(R.string.no_data_search));
        v.setVisibility(View.VISIBLE);
        findViewById(R.id.rl_Progress).setVisibility(View.GONE);
        findViewById(R.id.search_RecView).setVisibility(View.GONE);
        isLastPage = true;
        moreData = false;
    }

    private void fillUpWallpaperAdapter() {
        if (wallpaperModels != null && wallpaperModels.size() > 0) {
            if (moreData) {
                WallpaperModel loading = new WallpaperModel();
                loading.setImgId("-99");
                wallpaperModels.add(loading);
                dataInsertedCount++;
            }


            if (commonWallpaperAdapter == null) {
                commonWallpaperAdapter = new CommonWallpaperAdapter(wallpaperModels,this,false,"",0);
                commonWallpaperAdapter.setSearch(true);
                lastPos = wallpaperModels.size() + 1;
                GridLayoutManager linearLayoutManager = new GridLayoutManager(this, Constants.TILE_COLUMN);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                if (linearLayoutManager != null)
                    linearLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            try {
                                if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-99"))
                                    return Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-13"))
                                    return Constants.TILE_COLUMN;
                                else if (wallpaperModels != null && wallpaperModels.size() > 0 && !TextUtils.isEmpty(wallpaperModels.get(position).getImgId()) && wallpaperModels.get(position).getImgId().equalsIgnoreCase("-5"))
                                    return Constants.TILE_COLUMN;
                                else
                                    return 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return 1;
                        }
                    });
                binding.searchRecView.setLayoutManager(linearLayoutManager);
                if (isSpacing){
                    int spacing = (int) getResources().getDimension(R.dimen.content_padding_recycle);
                    binding.searchRecView.addItemDecoration(new SpacesItemDecoration(spacing));
                    binding.searchRecView.setItemAnimator(null);
                    isSpacing=false;
                }

                binding.searchRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                        if ((firstVisibleItemPositions + visibleItemCount) >= totalItemCount && firstVisibleItemPositions >= 0) {
                            onLoadMoreRequested();
                        }
                    }
                });
                binding.searchRecView.setAdapter(commonWallpaperAdapter);
                hideLoading();
            } else {
                if (currentPage == 1) {
                    commonWallpaperAdapter.notifyDataSetChanged();
                    binding.searchRecView.scrollToPosition(0);
                } else
                    commonWallpaperAdapter.notifyItemRangeInserted(lastPos, dataInsertedCount);
                lastPos = wallpaperModels.size() + 1;
            }
            isLoading = false;
        } else {
            noDataLayout();
        }
    }

    @Override
    public void onDataSuccess(IModel result) throws JSONException {
        if (currentPage == 1) {
            wallpaperModels.clear();
        }
        if (result != null) {
            WallpaperListModel model = (WallpaperListModel) result;
            hideLoading();
            if (model != null && model.getWallpaper()!=null) {
                removeBottomLoading();

                if (model != null && model.getWallpaper() != null) {
                    findViewById(R.id.search_RecView).setVisibility(View.VISIBLE);
                    searchKeywordModels.clear();
                    wallpaperModels.addAll(model.getWallpaper());
                    dataInsertedCount = model.getWallpaper().size();
                    findViewById(R.id.txt_nodata).setVisibility(View.GONE);
                }

                if (model.getWallpaper() == null) {
                    Log.e(TAG, "onSuccess: Get Wallpaper Is null now -- >" + model.getWallpaper().size());
                    noDataLayout();
                }
                boolean pagination_end = false;
                if (model.getWallpaper() == null || pagination_count != model.getWallpaper().size()) {
                    pagination_end = true;
                    Log.e(TAG, "onSuccess: " + model.getWallpaper().size());
                }

                Log.e(TAG, "onSuccess: " + pagination_end);

                if (!pagination_end) {
                    isLastPage = false;
                    moreData = true;
                } else {
                    isLastPage = true;
                    moreData = false;
                }
                used_ids = getUsedIds();
                Log.e(TAG, "onSuccess: " + used_ids);
                if (currentPage >= 1 && (model.getWallpaper() == null || model.getWallpaper().size() == 0)) {
                    if (commonWallpaperAdapter != null)
                        commonWallpaperAdapter.notifyDataSetChanged();
                } else {
                    if (isKeyWordAdapter) {
                        commonWallpaperAdapter = null;
                    }
                    isKeyWordAdapter = false;
                    fillUpWallpaperAdapter();
                }
            } else {
                if (commonWallpaperAdapter != null) {
                    commonWallpaperAdapter.notifyDataSetChanged();
                }
                noDataLayout();
            }
        }
    }

    @Override
    public void onDataError(String result) throws Exception {
        Log.e(TAG, "onError: " + result);
        noDataLayout();
    }


    @Override
    public void onItemClick(String keyword, int currentPosition) {
        showLoading();
        search_last = keyword;
        binding.editSearch.setText(search_last);
        Log.e(TAG, "onItemClick: " + search_last);
        Log.e(TAG, "onItemClick: " + keyword);
        resetContent();
        getWallpaperData();
    }

    private void removeBottomLoading() {
        try {
            if (wallpaperModels != null && wallpaperModels.size() > 0) {
                if (!TextUtils.isEmpty(wallpaperModels.get(wallpaperModels.size() - 1).getImgId()) && wallpaperModels.get(wallpaperModels.size() - 1).getImgId().equalsIgnoreCase("-99")) {
                    wallpaperModels.remove(wallpaperModels.size() - 1);
                    dataInsertedCount--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void showLoading() {
        if(currentPage==1) {
            findViewById(R.id.rl_Progress).setVisibility(View.VISIBLE);
            findViewById(R.id.search_RecView).setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        findViewById(R.id.rl_Progress).setVisibility(View.GONE);
        findViewById(R.id.search_RecView).setVisibility(View.VISIBLE);
    }

    private String getUsedIds() {
        String finalString = null;
        StringBuffer ids = new StringBuffer();
        try {
            if (wallpaperModels != null)
                for (int i = 0; i < wallpaperModels.size(); i++) {
                    if (!TextUtils.isEmpty(wallpaperModels.get(i).getImgId()) && !wallpaperModels.get(i).getImgId().equalsIgnoreCase("-99")) {
                        ids.append("'" + wallpaperModels.get(i).getImgId() + "'");
                        ids.append(",");
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finalString = ids.toString();
        if (!TextUtils.isEmpty(finalString)) {
            finalString = finalString.substring(0, finalString.length() - 1);
        }
        return finalString;
    }

    private void onLoadMoreRequested() {
        Log.e(TAG, "onLoadMoreRequested: Load " + isLoading + "  isLastPage:----->" + isLastPage);
        if (!isLoading && !isLastPage) {
            currentPage += 1;
            getWallpaperData();
        }
    }

    public void getWallpaperData() {
        showLoading();
        if (AppUtility.isInterNetworkAvailable(this)) {
            isLoading = true;
            Log.e(TAG, "getData: " + used_ids);
            RetroApiRequest.getSearchApiRequestRetro(this, used_ids, search_last.trim(),true, this);
        } else {
            AppUtility.showInterNetworkDialog(this, new InternetNetworkInterface() {
                @Override
                public void onOkClick() throws Exception {
                    finish();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(commonWallpaperAdapter !=null)
            commonWallpaperAdapter.releaseResource();
        commonWallpaperAdapter = null;
        used_ids = null;
        pagination_count = 0;
        dataInsertedCount = 0;
        isLastPage = false;
        moreData = false;
        lastPos = 0;
        isLoading = false;
        if(wallpaperModels !=null)
            wallpaperModels.clear();
        wallpaperModels = null;
        search_last=null;
        searchKeywordDBHelper =null;
        if(keywordHistoryAdapter !=null)
            keywordHistoryAdapter.releaseResource();
        keywordHistoryAdapter =null;
        if(searchKeywordModels !=null)
            searchKeywordModels.clear();
        searchKeywordModels =null;

    }

}
