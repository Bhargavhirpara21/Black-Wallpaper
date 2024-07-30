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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.MoreWallpaperActivity;
import wallpaper.black.live.uhd.R;

public class StockWallpaperAdapter extends RecyclerView.Adapter<StockWallpaperAdapter.StockWallViewHolder> {

    private Context mContext;
    private List<CategoryDataModel> categoryModels;
    private InAppPreference inAppPreference;
    private int type;

    public StockWallpaperAdapter(Context context, List<CategoryDataModel> categoryModels, int type) {
        this.mContext = context;
        this.categoryModels = categoryModels;
        this.type = type;
        inAppPreference = InAppPreference.getInstance(context);
    }

    @NonNull
    @Override
    public StockWallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (type){
            case 0:
                View view = LayoutInflater.from(mContext).inflate(R.layout.stock_wall_item, parent, false);
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,(int) mContext.getResources().getDimension(R.dimen.double_content_padding),0,(int) mContext.getResources().getDimension(R.dimen.double_content_padding));
                view.setLayoutParams(layoutParams);
                return new StockWallViewHolder(view);
            case 1:
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.stock_wall_item, parent, false);
                 layoutParams = new RelativeLayout.LayoutParams((int) mContext.getResources().getDimension(R.dimen.stockwallwidth), ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.marginleft_category),0,(int) mContext.getResources().getDimension(R.dimen.marginleft_category),0);
                view1.setLayoutParams(layoutParams);
                return new StockWallViewHolder(view1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull StockWallViewHolder holder, int position) {

        CategoryDataModel model = categoryModels.get(position);

        holder.stockText.setText(model.getName());

        String imageUrl;
        if(type==0)
            imageUrl = inAppPreference.getImgUrl() + Constants.catImgPathStock + model.getImagePath();
        else
            imageUrl = inAppPreference.getImgUrl() + Constants.homeCatImgPath + model.getImagePath();

        Log.d("ImageUrl", "onBindViewHolder: "+imageUrl);

        Glide.with(mContext)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.1f)
                .apply(new RequestOptions())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.stockImg);

        holder.stockImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtility.isInterNetworkAvailable(mContext)) {
                    if(type==0)
                        FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_STOCK_WALLPAPER, FirebaseEventManager.ATR_KEY_STOCK_SELECTED, ""+model.getName());
                    else
                        FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_CATEGORY, FirebaseEventManager.ATR_KEY_CATEGORY_SELECTED, model.getName());

                    Intent intent = new Intent(mContext, MoreWallpaperActivity.class);
                    intent.putExtra("from", "category");
                    intent.putExtra("object", model);
                    mContext.startActivity(intent);
                } else {
                    AppUtility.showInterNetworkDialog(mContext, null);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(categoryModels !=null && categoryModels.size()>0)
            return categoryModels.size();
        else
            return 0;
    }

    public class StockWallViewHolder extends RecyclerView.ViewHolder {

        private ImageView stockImg;
        private TextView stockText;
        private CardView stockCardView;

        public StockWallViewHolder(@NonNull View v) {
            super(v);
            stockImg =v.findViewById(R.id.stockImg);
            stockText =v.findViewById(R.id.stockText);
            stockCardView =v.findViewById(R.id.item_stock_cardview);
//            if(type==1)
                stockCardView.setForeground(mContext.getResources().getDrawable(R.drawable.round_cardview_category));
        }
    }

    public void releaseResource(){
        mContext =null;
        inAppPreference =null;
    }
}
