package wallpaper.black.live.uhd;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import wallpaper.black.live.uhd.Adapters.CommonWallpaperAdapter;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.SpacesItemDecoration;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.databinding.ActivityMydownloadBinding;


public class MyDownloadActivity extends BaseActivity {

    ActivityMydownloadBinding binding;
    public List<WallpaperModel> list=new ArrayList<>();
    private CommonWallpaperAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMydownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(getString(R.string.downloads));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!isPermissionGranted()) {
            return;
        }
        makeListDownload();
    }

    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 1000;

    private boolean isPermissionGranted() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(this, AppUtility.permissions()[0]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( //Method of Fragment
                            AppUtility.permissions(),
                            REQUEST_PERMISSIONS_CODE_WRITE_STORAGE
                    );
                    return false;
                } else {
                    return true;
                }
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (grantResults != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeListDownload();
            } else if (grantResults != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, getString(R.string.storage_txt), Toast.LENGTH_SHORT).show();
                isPermissionGranted();
            }
        }
    }

    private void makeListDownload() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (list != null) {
                        list.clear();
                    }
                    list.addAll(FetchWallpaperImages());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                fillUpAdapterData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
        }.start();
    }

    private void fillUpAdapterData() {
        if (list != null && list.size() > 0) {
            hideLoading();
            binding.downloadRecycle.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new CommonWallpaperAdapter(list, this, false, "download", 4);
                binding.downloadRecycle.setHasFixedSize(true);
                GridLayoutManager linearLayoutManager = new GridLayoutManager(this, Constants.TILE_COLUMN);
                linearLayoutManager.setOrientation(androidx.recyclerview.widget.LinearLayoutManager.VERTICAL);
                binding.downloadRecycle.setLayoutManager(linearLayoutManager);

                int spacing = (int) getResources()
                        .getDimension(R.dimen.content_padding_recycle);
                binding.downloadRecycle.addItemDecoration(new SpacesItemDecoration(spacing));

                binding.downloadRecycle.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            noDataLayout();
        }
    }

    private void noDataLayout() {
        binding.txtNodata.setText(getString(R.string.letsdownload));
        binding.txtNodata.setVisibility(View.VISIBLE);
        binding.rlProgress.setVisibility(View.GONE);
        binding.downloadRecycle.setVisibility(View.GONE);
    }

    private ArrayList<WallpaperModel> FetchWallpaperImages() {

        ArrayList<WallpaperModel> filenames = new ArrayList<WallpaperModel>();
        String path = AppUtility.getSavedWallpaperPath();

        File directory = new File(path);
        File[] files = directory.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        for (int i = 0; i < files.length; i++) {
            String file_name = files[i].getAbsolutePath();
//            Logger.e("file_name", "" + file_name);
            // you can store name to arraylist and use it later
            WallpaperModel post = new WallpaperModel();
            post.setImgId("-100");
            post.setImgPath(file_name);
            filenames.add(post);
        }
        return filenames;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BlackApplication.getBlackApplication().isShareRate=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        BlackApplication.getBlackApplication().isShareRate=false;
    }

    private void showLoading() {
        binding.rlProgress.setVisibility(View.VISIBLE);
        binding.downloadRecycle.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.rlProgress.setVisibility(View.GONE);
        binding.downloadRecycle.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
