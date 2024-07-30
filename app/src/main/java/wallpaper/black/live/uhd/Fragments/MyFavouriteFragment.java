package wallpaper.black.live.uhd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.Adapters.CommonWallpaperAdapter;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.SpacesItemDecoration;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.databinding.FragmentMyFavouriteBinding;

public class MyFavouriteFragment extends Fragment {

    private List<WallpaperModel> favorite_List = new ArrayList<WallpaperModel>();
    private boolean isLiveWall;
    private CommonWallpaperAdapter commonWallpaperAdapter;
    FragmentMyFavouriteBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLiveWall = getArguments().getBoolean("isVideoWall");
            favorite_List = (List<WallpaperModel>) getArguments().getSerializable("list");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillupAdapter();
    }

    public void updateDataAndRecyclerview(List<WallpaperModel> wallpaperList) {
        if (commonWallpaperAdapter != null) {
            if (favorite_List.size() != 0) {
                favorite_List = wallpaperList;
                commonWallpaperAdapter.notifyDataSetChanged();
            } else {
                if (isLiveWall) {
                    noDataLayout(getString(R.string.no_fav_live));
                } else {
                    noDataLayout(getString(R.string.no_fav_wall));
                }
            }
        }
    }

    private void fillupAdapter() {
        if (favorite_List != null && favorite_List.size() > 0) {
            binding.rlNoContent.setVisibility(View.GONE);
            if (commonWallpaperAdapter == null) {
                commonWallpaperAdapter = new CommonWallpaperAdapter(favorite_List, getActivity(), isLiveWall, "", 99);
                binding.favortieRecycle.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), Constants.TILE_COLUMN);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                binding.favortieRecycle.setLayoutManager(linearLayoutManager);

                int spacing = (int) getResources().getDimension(R.dimen.content_padding_recycle);
                binding.favortieRecycle.addItemDecoration(new SpacesItemDecoration(spacing));

                binding.favortieRecycle.setAdapter(commonWallpaperAdapter);
            } else {
                commonWallpaperAdapter.notifyDataSetChanged();
            }
        } else {
            if (isLiveWall) {
                noDataLayout(getString(R.string.no_fav_live));
            } else {
                noDataLayout(getString(R.string.no_fav_wall));
            }
        }
    }

    private void noDataLayout(String msg) {
        String[] values = new String[]{msg};
        binding.rlNoContent.setVisibility(View.VISIBLE);
        binding.txtNo.setText(values[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        favorite_List = null;
        isLiveWall = false;
        if(commonWallpaperAdapter !=null)
            commonWallpaperAdapter.releaseResource();
        commonWallpaperAdapter = null;
    }
}
