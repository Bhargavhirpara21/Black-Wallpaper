package wallpaper.black.live.uhd;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import wallpaper.black.live.uhd.Adapters.StockWallpaperAdapter;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.databinding.ActivityStockWallpaperBinding;

public class StockWallpaperActivity extends BaseActivity {

    ActivityStockWallpaperBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStockWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(getString(R.string.stock_wall));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<CategoryDataModel> stockCataList = SingletonControl.getInstance().getStockcategoryList();

        StockWallpaperAdapter adapter = new StockWallpaperAdapter(this,stockCataList,0);
        binding.stockRecView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
