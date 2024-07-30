package wallpaper.black.live.uhd.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.MoreWallpaperActivity;
import wallpaper.black.live.uhd.PitchWallpaperActivity;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.SingletonControl;
import wallpaper.black.live.uhd.StockWallpaperActivity;

public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int type;
    private Context mContext;
    private InAppPreference storePref;
    private LayoutInflater inflater;
    private List<CategoryDataModel> categoryModelArrayList = new ArrayList<>();
    public static final int STOCK_VIEW = 4;
    public static final int CATEGORY_VIEW = 1;
    public static final int CARAOUSAL_VIEW = 2;
    private StockCategoryViewHolder categoryViewHolder;
    private StockWallpaperAdapter stockWallpaperHolder;

    public CategoryListAdapter(Context context, List<CategoryDataModel> categoryList, int type) {
        this.mContext = context;
        this.type = type;
        inflater = LayoutInflater.from(context);
        this.categoryModelArrayList = categoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case CATEGORY_VIEW:
                view = inflater.inflate(R.layout.item_category, parent, false);
                return new photoViewHolder(view);

            case CARAOUSAL_VIEW:
                view = inflater.inflate(R.layout.item_carousal, parent, false);
                return new photoViewHolder(view);

            case STOCK_VIEW:
                if (categoryViewHolder != null) {
                    return categoryViewHolder;
                }
                View stockCategoryItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_item_carousal, null);
                categoryViewHolder = new StockCategoryViewHolder(stockCategoryItemLayoutView);
                return categoryViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryDataModel obj = categoryModelArrayList.get(position);
        storePref = InAppPreference.getInstance(mContext);

        if (!AppUtility.isValidContextForGlide(mContext)) {
            return;
        }

        if (obj != null && obj.getCategoryId().equalsIgnoreCase("-4")) {
            itemStockCategory((StockCategoryViewHolder) holder);
        } else {
            switch (type) {
                case CATEGORY_VIEW:
                    Glide.with(mContext)
                            .load(storePref.getImgUrl() + Constants.catImgPath_new_img + obj.getImagePath())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .thumbnail(0.1f)
                            .apply(new RequestOptions())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(((photoViewHolder) holder).img);

                    ((photoViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (AppUtility.isInterNetworkAvailable(mContext)) {
                                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_CATEGORY, FirebaseEventManager.ATR_KEY_CATEGORY_SELECTED, obj.getName());
                                Intent intent = new Intent(mContext, MoreWallpaperActivity.class);
                                intent.putExtra("from", "category");
                                intent.putExtra("object", obj);
                                mContext.startActivity(intent);
                            } else {
                                AppUtility.showInterNetworkDialog(mContext, null);
                            }
                        }
                    });
                    break;

                case CARAOUSAL_VIEW:

                    String imagepath;
                    if (obj.getCategoryId() != null) {
                        imagepath = storePref.getImgUrl() + Constants.catImgPath + obj.getImagePath();
                    } else {
                        imagepath = storePref.getImgUrl() + Constants.catImgPathBanner + obj.getImagePath();
                    }
                    Glide.with(mContext)
                            .load(imagepath)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .thumbnail(0.1f)
                            .apply(new RequestOptions())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(((photoViewHolder) holder).img);

                    ((photoViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (!AppUtility.isInterNetworkAvailable(mContext)) {
                                AppUtility.showInterNetworkDialog(mContext, null);
                                return;
                            }

                            if (obj != null && obj.getCategoryId().equalsIgnoreCase("-1")) {
                                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_PITCH_BLACK, FirebaseEventManager.ATR_KEY_CATEGORY_SELECTED, "Home");
                                Intent intent = new Intent(mContext, PitchWallpaperActivity.class);
                                Log.d("Catagory", "onClick: Pitch");
                                mContext.startActivity(intent);
                            }
//                            else if (obj != null && obj.getCategoryId().equalsIgnoreCase("-3")) {
//                                EventManager.sendEvent(EventManager.LBL_BLACK_SECREEN, EventManager.ATR_KEY_BLACK_SCREEN, "Home");
//                                Intent intent2 = new Intent(context, BlacklyScreenActivity.class);
//                                Log.d("Catagory", "onClick: Stock");
//                                context.startActivity(intent2);
//                            }
                            else if (obj != null && obj.getCategoryId().equalsIgnoreCase("-5")) {
                                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_PURE_BLACK, "Open", "Home");
                                //temp
//                                context.startActivity(new Intent(context, PureBlackWallpaperActivity.class));
                            } else {
                                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_CATEGORY, FirebaseEventManager.ATR_KEY_CATEGORY_SELECTED_CAROUSL, obj.getName());
                                Intent intent = new Intent(mContext, MoreWallpaperActivity.class);
                                intent.putExtra("from", "category");
                                intent.putExtra("object", obj);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    break;
            }
        }
    }

    private void itemStockCategory(StockCategoryViewHolder holder) {

        List<CategoryDataModel> stockCategoryList = SingletonControl.getInstance().getStockcategoryList();

        if (stockCategoryList != null && stockCategoryList.size() > 0) {
            holder.itemView.findViewById(R.id.rl_tag_item).setVisibility(View.VISIBLE);
            if (stockWallpaperHolder != null)
                return;
            RecyclerView stockWallRecView = holder.stockRecView;

            final LinearLayoutManager layout = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            stockWallRecView.setLayoutManager(layout);
            layout.setSmoothScrollbarEnabled(true);
            stockWallpaperHolder = new StockWallpaperAdapter(mContext, stockCategoryList, 1);
            stockWallRecView.setAdapter(stockWallpaperHolder);
//            stockWallRecView.setHasFixedSize(true);
            stockWallRecView.setLongClickable(true);

            holder.relativelayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_STOCK_WALLPAPER, FirebaseEventManager.ATR_KEY_STOCK_CATEGORY, "view all category");
                    Intent intent = new Intent(mContext, StockWallpaperActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.itemView.findViewById(R.id.rl_tag_item).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
       if (categoryModelArrayList != null && categoryModelArrayList.get(position).getCategoryId().equalsIgnoreCase("-4")) {
            return STOCK_VIEW;
        } else {
           return type;
        }
    }

    private class StockCategoryViewHolder extends RecyclerView.ViewHolder {

        RecyclerView stockRecView;
        private RelativeLayout relativelayout1;

        public StockCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            stockRecView = itemView.findViewById(R.id.stock_RecView);
            relativelayout1 = itemView.findViewById(R.id.rl_tag_item);
        }
    }

    public static class photoViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textName;

        public photoViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.item_category_titile);
            img = itemView.findViewById(R.id.stock_item_img);
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
        if (stockWallpaperHolder != null) {
            stockWallpaperHolder.releaseResource();
        }
        stockWallpaperHolder = null;
        inflater = null;
        mContext = null;
        categoryViewHolder = null;
    }

}
