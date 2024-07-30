package wallpaper.black.live.uhd.Fragments;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import wallpaper.black.live.uhd.Adapters.DoubleAdapter;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.DoublePagerScrollListener;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Interface.DoubleClickCallback;
import wallpaper.black.live.uhd.MainActivity;
import wallpaper.black.live.uhd.Model.DoubleWallData;
import wallpaper.black.live.uhd.Model.DoubleWallsModel;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.SingletonControl;
import wallpaper.black.live.uhd.databinding.FragmentDoubleWallpaperBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;


public class DoubleWallpaperFragment extends Fragment implements RetroCallbacks, DoubleClickCallback {

    private DoubleAdapter doubleAdapter;
    private List<DoubleWallsModel> doubleWallsModels;
    public static int currentPosition = -1;
    private MainActivity mainActivity;
    private int image_counter;
    private boolean swipeFlag = false;
    private String wall1, wall2;
    private int currentProgress;
    private int paginationCount;
    boolean isNeedToDisplayLoading = true, isNeedToReset = true;
    boolean isLoading;
    boolean isLastPage, moreData = false;
    private ThinDownloadManager downloadManagerThin;
    private static AlertDialog alertDialog;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 1;
    private InAppPreference inAppPreference;
    MyDownloadDownloadStatusListenerV1 myDownloadDownloadStatusListenerV1 = new MyDownloadDownloadStatusListenerV1();
    int downloadId1;
    private CircularProgressIndicator progressIndicator;
    private FragmentDoubleWallpaperBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDoubleWallpaperBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.swiperefresh.setEnabled(false);

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if ((AppUtility.isInterNetworkAvailable(getActivity()))) {
                    swipeFlag = true;
                    refreshContent();
                } else {
                    removeRefresh();
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        paginationCount = 12;
        inAppPreference = InAppPreference.getInstance(getContext());
        mainActivity = (MainActivity) getActivity();
        doubleWallsModels = new ArrayList<>();
//        CallToGetList();
    }

    private boolean isAPICalling;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(!isAPICalling && (doubleWallsModels ==null || doubleWallsModels.size()==0)) {
                if(binding.swiperefresh!=null)
                    binding.swiperefresh.setEnabled(false);
                CallToDataList();
            }
        }
    }

    public void refreshCall(boolean needToShowLoading) {

        if (!AppUtility.isInterNetworkAvailable(getActivity())) {
            removeRefresh();
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.swiperefresh != null)
            binding.swiperefresh.post(new Runnable() {
                @Override
                public void run() {
                    if (binding.swiperefresh != null)
                        binding.swiperefresh.setRefreshing(true);
                }
            });

        isNeedToDisplayLoading = needToShowLoading;
        if (needToShowLoading) {
            isNeedToReset = true;
            isNeedToDisplayLoading = false;
        }
        CallToDataList();
    }

    public void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    isNeedToReset = true;
                    isNeedToDisplayLoading = false;
                    refreshCall(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.REFRESH_TIME_OUT);
    }

    public void resetContent() {
        isLoading = true;
        isLastPage = false;
        moreData = true;
    }

    public void removeRefresh() {
        if (binding.swiperefresh != null)
            binding.swiperefresh.setRefreshing(false);
    }

    private void CallToDataList() {

        if (isNeedToReset) {
            resetContent();
            isNeedToReset = false;
        }

        if (AppUtility.isInterNetworkAvailable(getActivity())) {
            isAPICalling=true;
            getAPIListResponse();
        }else
            isAPICalling=false;
    }

    private void getAPIListResponse() {
        if (AppUtility.isInterNetworkAvailable(getActivity())) {
            isLoading = true;
            isAPICalling=true;
            RetroApiRequest.getDoubleWallpaperRetro(getContext(), getWallsUsedIds(), this);
        } else {
            isLoading = false;
            isAPICalling=false;
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private String getWallsUsedIds() {
        if (swipeFlag) {
            return "";
        }

        String finalString = null;
//		int counter = 0;
        StringBuffer ids = new StringBuffer();
        if (doubleWallsModels != null)
            for (int i = 0; i < doubleWallsModels.size(); i++) {
                if (!doubleWallsModels.get(i).getImg_id().equalsIgnoreCase("-99")) {
                    ids.append("'" + doubleWallsModels.get(i).getImg_id() + "'");
                    ids.append(",");
//					counter++;
                }
            }
//		Logger.e("counter:", "" + counter);
        finalString = ids.toString();
        if (!TextUtils.isEmpty(finalString)) {
            finalString = finalString.substring(0, finalString.length() - 1);
        }
        return finalString;
    }

    @Override
    public void onDataSuccess(IModel result) throws JSONException {

        if (result != null && getActivity() != null && !getActivity().isFinishing()) {

            removeRefresh();
            removeLoading();
            LoggerCustom.erorr("remove", "loading");

            isLoading = false;
            DoubleWallData model = (DoubleWallData) result;
            if (model.getDoubleList() != null) {

                if (swipeFlag) {
                    doubleWallsModels.clear();
                    swipeFlag = false;
                }

                doubleWallsModels.addAll(model.getDoubleList());
                LoggerCustom.erorr("size", doubleWallsModels.size() + "");
            }

            boolean pagination_end = false;
            int doubleLimit = Integer.parseInt(SingletonControl.getInstance().getDataList().getLogic().getPagination_limit_double());
            if (model.getDoubleList() == null || paginationCount != doubleLimit ) {
                pagination_end = true;
                Log.e("hello", "onSuccess: "+pagination_end);
            }

            if (!pagination_end) {
                isLastPage = false;
                moreData = true;
                Log.e("TAG", "onSuccess: "+moreData);
            } else {
                isLastPage = true;
                moreData = false;
            }

            if (doubleAdapter == null) {
                fillupdataAdapter();
            } else {
                if (doubleAdapter != null)
                    doubleAdapter.notifyDataSetChanged();
            }
        }
    }

    private void fillupdataAdapter() {

        binding.rlProgress.setVisibility(View.GONE);
        binding.rcycleDouble.setVisibility(View.VISIBLE);

        if (moreData) {
            DoubleWallsModel obj = new DoubleWallsModel();
            obj.setImg_id("-99");
            doubleWallsModels.add(obj);
        }

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.rcycleDouble);

        doubleAdapter = new DoubleAdapter(doubleWallsModels, getActivity(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcycleDouble.setLayoutManager(linearLayoutManager);
        binding.rcycleDouble.setAdapter(doubleAdapter);
        binding.rcycleDouble.setHasFixedSize(true);
        binding.rcycleDouble.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

                if ((firstVisibleItemPositions + visibleItemCount) >= totalItemCount
                        && firstVisibleItemPositions >= 0) {
                    if (!isLoading && !isLastPage)
                        onLoadMoreRequested();
                }
            }
        });

        binding.rcycleDouble.addOnScrollListener(new DoublePagerScrollListener(snapHelper, DoublePagerScrollListener.ON_SETTLED, true, new DoublePagerScrollListener.OnChangeListener() {
            @Override
            public void onSnapped(int position) {
                currentPosition = position;
                LoggerCustom.erorr("position", currentPosition + "");
                if (doubleAdapter != null)
                    doubleAdapter.notifyDataSetChanged();
            }
        }));

        binding.swiperefresh.setEnabled(true);
    }

    public void onLoadMoreRequested() {
        Log.e("DoubleWalls", "onLoadMoreRequested isLoading: " + "" + " isLastPage " + isLastPage + " currentPage " + "");
        if (!isLoading && !isLastPage) {
            isLoading = true;
            getAPIListResponse();
        }
    }

    private void removeLoading() {
        Log.e("DoubleWalls", "Remove function");
        if (doubleWallsModels != null && doubleWallsModels.size() > 0) {
            if (!TextUtils.isEmpty(doubleWallsModels.get(doubleWallsModels.size() - 1).getImg_id()) && doubleWallsModels.get(doubleWallsModels.size() - 1).getImg_id().equalsIgnoreCase("-99")) {
                doubleWallsModels.remove(doubleWallsModels.size() - 1);
            }
        }
    }

    @Override
    public void onDataError(String result) {
        LoggerCustom.erorr("Error", result);
        removeRefresh();
        Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadClick(int position) {
        if (position == -1) {
            Toast.makeText(getActivity(), R.string.select_wall, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }

        builder.setTitle(R.string.set_wallpaper);
        builder.setMessage(R.string.set_double);
        final DialogInterface.OnClickListener dialogButtonClickListener =
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_DOUBLE_WALLPAPER, "Set As", "Yes");
                        currentProgress = 0;
//                        if (activity.getProgress() != null) {
//                            activity.getProgress().setCompleted(false);
//                        }
                        setWallpaperUser(position);
                    }
                };

        builder.setPositiveButton(R.string.yes_btn, dialogButtonClickListener);
        builder.setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_DOUBLE_WALLPAPER, "Set As", "No");
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void setWallpaperUser(int position) {

        if (image_counter == 0) {
//            activity.showDownloadLoadingDialog("");
            showLoadingDialog(getContext());
        }

        try {
            DoubleWallsModel object = doubleWallsModels.get(position);
            String path;
            String filename = "";
            if (image_counter == 0) {
                path = inAppPreference.getImgUrl() + Constants.doubleImgPath + object.getImg1();
                filename = object.getImg1();
                if (object.getImg1().startsWith("http")) {
                    path = object.getImg1();
                    filename = path.substring(path.lastIndexOf("/") + 1);
                }
                LoggerCustom.erorr("path", "" + path);
                wall1 = AppUtility.createBaseDirectoryCacheDoubleWall() + "/" + filename;
                LoggerCustom.erorr("dst wall1", "" + wall1);
                File file = new File(wall1);
                if (file != null && file.exists()) {
                    image_counter++;
                    setWallpaperUser(position);
                } else
                    initializeDownloadFuc(wall1, path);

            } else {
                path = inAppPreference.getImgUrl() + Constants.doubleImgPath + object.getImg2();
                filename = object.getImg2();
                if (object.getImg2().startsWith("http")) {
                    path = object.getImg2();
                    filename = path.substring(path.lastIndexOf("/") + 1);
                }

                LoggerCustom.erorr("path", "" + path);
                wall2 = AppUtility.createBaseDirectoryCacheDoubleWall() + "/" + filename;
                LoggerCustom.erorr("dst", "" + wall2);

                File file = new File(wall2);
                if (file != null && file.exists()) {
                    setWallpapersFinal();
                } else
                    initializeDownloadFuc(wall2, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDownloadFuc(String pathToDownload, String downloadURL) {

        downloadManagerThin = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

        RetryPolicy retryPolicy = new DefaultRetryPolicy();

        Uri downloadUri = Uri.parse(downloadURL);
        Uri destinationUri = Uri.parse(pathToDownload);
        final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setRetryPolicy(retryPolicy)
                .setDownloadContext("Download1")
                .setStatusListener(myDownloadDownloadStatusListenerV1);

        if (downloadManagerThin.query(downloadId1) == com.thin.downloadmanager.DownloadManager.STATUS_NOT_FOUND) {
            downloadId1 = downloadManagerThin.add(downloadRequest1);
        }
    }

    private void setWallpapersFinal() {

        if (mainActivity == null)
            return;
        hideLoadingDialog();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        int screenWidht = displayMetrics.widthPixels;
        int screenheight = displayMetrics.heightPixels;

        binding.rlProgress.setVisibility(View.VISIBLE);
        image_counter = 0;

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LoggerCustom.erorr("setWallpapersFinal", "wall1:" + wall1 + " wall2:" + wall2);
                Bitmap bitmap1 = null;
                try {
                    bitmap1 = Glide
                            .with(mainActivity)
                            .asBitmap()
                            .load(wall1).apply(new RequestOptions().override(screenWidht, screenheight)).centerCrop()
                            .submit()
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bitmap bitmap2 = null;

                try {
                    bitmap2 = Glide
                            .with(mainActivity)
                            .asBitmap()
                            .load(wall2).apply(new RequestOptions().override(screenWidht, screenheight)).centerCrop()
                            .submit()
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();

                }

                final Bitmap finalBitmap = bitmap1;
                final Bitmap finalBitmap1 = bitmap2;
                if (mainActivity != null) {

                    WallpaperManager wallpaperManager =
                            WallpaperManager.getInstance(getActivity());
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (finalBitmap != null) {
                                wallpaperManager.setBitmap(finalBitmap, null, true, WallpaperManager.FLAG_LOCK);
                            }
                            if (finalBitmap1 != null)
                                wallpaperManager.setBitmap(finalBitmap1, null, true, WallpaperManager.FLAG_SYSTEM);
                        } else {
                            if (finalBitmap1 != null) {
                                wallpaperManager.setBitmap(finalBitmap1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mainActivity != null)
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (mainActivity == null || binding.rlProgress == null)
                                    return;

                                LoggerCustom.erorr("DoubleWallsFragment", "applied successfully");
                                //                        DoubleWallpaperFragment.dismissLoadingProgress();
                                try {
//                                    dialog.dismiss();
                                    binding.rlProgress.setVisibility(View.GONE);
                                    Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.wall_applied), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                }
            }
        }.start();
    }

    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

        @Override
        public void onDownloadComplete(DownloadRequest request) {

            final int id = request.getDownloadId();
            if (id == downloadId1) {
                LoggerCustom.erorr("onDownloadComplete", "onDownloadComplete");
                if (image_counter == 0) {
                    image_counter++;
                    setWallpaperUser(currentPosition);
                } else {
                    setWallpapersFinal();
                    if (mainActivity != null)
                        hideLoadingDialog();
//                        activity.dismissLoadingProgress();
                }
            }
        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            try {
                final int id = request.getDownloadId();
                if (id == downloadId1) {
//                    activity.dismissLoadingProgress();
                    hideLoadingDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();
            if (id == downloadId1) {
                try {
                    currentProgress = (int) (progress / 2);
                    if (image_counter == 1)
                        currentProgress += 50;
                    if(progressIndicator!=null){
                        progressIndicator.setCurrentProgress(currentProgress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showLoadingDialog(Context c) {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(c, R.style.CustomDialog);
        View customLayout = null;

        LayoutInflater li = LayoutInflater.from(c);
        // set the custom layout
        customLayout = li.inflate(R.layout.progress_layout, null);

        builder.setView(customLayout);
        progressIndicator = customLayout.findViewById(R.id.progress_download);
        progressIndicator.setProgress(0.0, 100.0);
        progressIndicator.setCurrentProgress(0.0);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    public void hideLoadingDialog() {

        if (alertDialog != null)
            alertDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (doubleAdapter != null)
            doubleAdapter.destroyResource();
        doubleAdapter = null;
        if (doubleWallsModels != null)
            doubleWallsModels.clear();
        doubleWallsModels = null;
        mainActivity = null;
        wall1 = null;
        wall2 = null;
        downloadManagerThin = null;
        myDownloadDownloadStatusListenerV1 = null;
        image_counter = 0;
        swipeFlag = false;
        currentProgress = 0;
        paginationCount = 0;
        isNeedToDisplayLoading = false;
        isNeedToReset = false;
        isLoading = false;
        isLastPage = false;
        moreData = false;
        downloadId1 = 0;
        inAppPreference = null;
    }
}

