package wallpaper.black.live.uhd.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.LiveWallpaperDetailsActivity;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.WallpaperDetailsActivity;

public class CommonWallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WallpaperModel> wallpaperModelList;
    private Context mContext;
    private InAppPreference inAppPreference;
    private boolean isLiveWallpaper;
    public static final int WALLPAPER_TYPE = 0;
    public String from;
    private int photosType;
    public static final int LOADING = 1;
    public static final int PITCHBLACK_WALLPAPER = 3;
    private static boolean isSearch;

    public void setSearch(boolean search) {
        isSearch = search;
    }
    
    public CommonWallpaperAdapter(List<WallpaperModel> wallList, Context context, boolean isVideoWall, String from, int pType) {
        this.wallpaperModelList = wallList;
        this.mContext = context;
        this.isLiveWallpaper = isVideoWall;
        this.from = from;
        this.photosType = pType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case WALLPAPER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
                return new wallpaperViewHolder(view);

            case PITCHBLACK_WALLPAPER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pitch_list_item, parent, false);
                return new pitchblackViewHolder(view);
            case LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new loadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WallpaperModel object = wallpaperModelList.get(position);
        inAppPreference = InAppPreference.getInstance(mContext);
        final int type = getItemViewType(position);
        switch (type) {
            case WALLPAPER_TYPE:
                if (!isLiveWallpaper) {
                    if (from.equals("download")) {
                        Glide.with(mContext)
                                .load(object.getImgPath())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .thumbnail(0.1f)
                                .apply(new RequestOptions())
                                .centerCrop()
                                .into(((wallpaperViewHolder) holder).img);
                    } else {
                        Glide.with(mContext)
                                .load(inAppPreference.getImgUrl() + Constants.smallPath + object.getImgPath())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .thumbnail(0.1f)
                                .apply(new RequestOptions())
                                .centerCrop()
                                .into(((wallpaperViewHolder) holder).img);
                    }
                } else {
                    ((wallpaperViewHolder) holder).plybtn.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(inAppPreference.getImgUrl() + Constants.liveThumbPath + object.getImgPath())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .thumbnail(0.1f)
                            .apply(new RequestOptions())
                            .centerCrop()
                            .into(((wallpaperViewHolder) holder).img);
                }

                ((wallpaperViewHolder) holder).img.setOnClickListener(view -> {
                    if (AppUtility.isInterNetworkAvailable(mContext)) {
                        Intent intent=null;
                        if (isLiveWallpaper) {
                            intent = new Intent(mContext, LiveWallpaperDetailsActivity.class);
                            intent.putExtra("object", object);
                        } else {
                            intent = new Intent(mContext, WallpaperDetailsActivity.class);
                            intent.putExtra("object", object);
                            intent.putExtra("position", position + "");
                            intent.putExtra("photoType", photosType);
                            LoggerCustom.erorr("position", position + " : position");
                        }

                        if (!from.equals("download")) {
                            mContext.startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                            } else {
                                builder = new AlertDialog.Builder(mContext);
                            }
                            String[] animals = {mContext.getString(R.string.view), mContext.getString(R.string.set_wall), mContext.getString(R.string.share), mContext.getString(R.string.del)};
                            builder.setItems(animals, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // horse
                                            try {
                                                Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_VIEW);
                                                intent.setDataAndType(Uri.parse("file://" + object.getImgPath()), "image/*");
                                                mContext.startActivity(intent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case 1: // cow
                                            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                                            intent.setDataAndType(Uri.fromFile(new File(object.getImgPath())), "image/*");
                                            intent.putExtra("mimeType", "image/*");
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.setas)));
                                            break;
                                        case 2: // cow
                                            AppUtility.sharedWallpaper(mContext, "", object.getImgPath(), "", false);
                                            break;
                                        case 3: // cow
                                            deleteAlertDialog(mContext, object.getImgPath(), position);
                                            break;
                                    }
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    } else {
                        AppUtility.showInterNetworkDialog(mContext, null);
                    }
                });
                break;
            case LOADING:
                break;
            case PITCHBLACK_WALLPAPER:
                pitchblackViewHolder viewHolder = (pitchblackViewHolder) holder;
                viewHolder.txt_title.setText(object.getTags());
                Glide.with(mContext)
                        .load(inAppPreference.getImgUrl() + Constants.smallPath + object.getImgPath())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .thumbnail(0.1f)
                        .apply(new RequestOptions())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(viewHolder.images);
                viewHolder.images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, WallpaperDetailsActivity.class);
                        intent.putExtra("object", object);
                        intent.putExtra("position", position + "");
                        intent.putExtra("photoType", photosType);
                        mContext.startActivity(intent);
                    }
                });
                break;
        }
    }

    private void deleteAlertDialog(final Context context, final String dst, final int pos) {
        try {
            if (((Activity) context).isFinishing()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle(context.getString(R.string.del_dialog));
        builder.setMessage(context.getString(R.string.delete_txt));
        final DialogInterface.OnClickListener dialogButtonClickListener =
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            AppUtility.deletePostImage(context, dst);
                            dialog.dismiss();
                            wallpaperModelList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, wallpaperModelList.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

        builder.setPositiveButton(context.getString(R.string.yes_btn), dialogButtonClickListener);
        builder.setNegativeButton(context.getString(R.string.no_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public int getItemViewType(int position) {
        if (wallpaperModelList.get(position).getImgId().equalsIgnoreCase("-99")) {
            return LOADING;
        }else if (photosType == 5) {
            return PITCHBLACK_WALLPAPER;
        } else {
            return WALLPAPER_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperModelList.size();
    }

    public static class wallpaperViewHolder extends RecyclerView.ViewHolder {
        ImageView img, plybtn;

        public wallpaperViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.stock_item_img);
            plybtn = itemView.findViewById(R.id.img_play_icon);
        }
    }

    public static class loadingViewHolder extends RecyclerView.ViewHolder {
        public loadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class pitchblackViewHolder extends RecyclerView.ViewHolder {
        ImageView images;
        TextView txt_title;

        public pitchblackViewHolder(View view) {
            super(view);
            images = (ImageView) view.findViewById(R.id.list_pitch_imageView);
            txt_title = view.findViewById(R.id.txt_pitch_title);
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
        isSearch = false;
        mContext = null;
        inAppPreference = null;
    }
}
