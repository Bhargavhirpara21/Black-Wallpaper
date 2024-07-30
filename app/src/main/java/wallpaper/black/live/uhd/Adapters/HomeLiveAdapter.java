package wallpaper.black.live.uhd.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.LiveWallpaperDetailsActivity;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.WallpaperDetailsActivity;

public class HomeLiveAdapter extends RecyclerView.Adapter<HomeLiveAdapter.HomeLiveWallsViewHolder>{

    private Context mContext;
    private List<WallpaperModel> wallpaperModelList;
    private boolean isLiveWallpaper;
    private InAppPreference inAppPreference;

    public void setLiveWallpaper(boolean liveWallpaper) {
        isLiveWallpaper = liveWallpaper;
    }

    public HomeLiveAdapter(Context context, List<WallpaperModel> liveWallsList) {
        this.mContext = context;
        this.wallpaperModelList = liveWallsList;
        inAppPreference = InAppPreference.getInstance(context);
    }

    @NonNull
    @Override
    public HomeLiveWallsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(mContext).inflate(R.layout.adapter_home_live, parent, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        layoutParams.setMargins(0,(int) context.getResources().getDimension(R.dimen.marginleft),0,0);
        layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.marginleft_category),0,(int) mContext.getResources().getDimension(R.dimen.marginleft_category),0);
        view1.setLayoutParams(layoutParams);
        return new HomeLiveWallsViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeLiveWallsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WallpaperModel wallpaper = wallpaperModelList.get(position);
        holder.item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLiveWallpaper){
                    Intent intent = new Intent(mContext, LiveWallpaperDetailsActivity.class);
                    intent.putExtra("object", wallpaper);
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, WallpaperDetailsActivity.class);
                    intent.putExtra("object", wallpaper);
                    intent.putExtra("position", position + "");
                    mContext.startActivity(intent);
                }
            }
        });
        String imageUrl;
        if(isLiveWallpaper)
            imageUrl = inAppPreference.getImgUrl() + Constants.liveThumbPath + wallpaper.getImgPath();
        else
            imageUrl = inAppPreference.getImgUrl() + Constants.smallPath + wallpaper.getImgPath();

        if (isLiveWallpaper)
            holder.img_play_icon.setVisibility(View.VISIBLE);
        else
            holder.img_play_icon.setVisibility(View.GONE);

        Log.d("ImageUrl", "onBindViewHolder: "+imageUrl);

        Glide.with(mContext)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.1f)
                .apply(new RequestOptions())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.item_img);
    }

    @Override
    public int getItemCount() {
        return wallpaperModelList.size();
    }

    public class HomeLiveWallsViewHolder extends RecyclerView.ViewHolder {

        private ImageView item_img, img_play_icon;

        public HomeLiveWallsViewHolder(@NonNull View itemView) {
            super(itemView);
            item_img = itemView.findViewById(R.id.stock_item_img);
            img_play_icon = itemView.findViewById(R.id.img_play_icon);
            CardView cardview=itemView.findViewById(R.id.item_stock_cardview);
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) cardview.getLayoutParams();
            layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.marginrightRecharcle), 0);
            cardview.requestLayout();

            item_img.getLayoutParams().width= (int) mContext.getResources().getDimension(R.dimen.livewallwidth);
            item_img.getLayoutParams().height= (int) mContext.getResources().getDimension(R.dimen.livewallheight);
            item_img.requestLayout();
        }
    }
}
