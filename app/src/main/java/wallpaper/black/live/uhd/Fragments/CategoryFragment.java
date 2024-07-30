package wallpaper.black.live.uhd.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.Adapters.CategoryListAdapter;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.SingletonControl;
import wallpaper.black.live.uhd.databinding.FragmentCategoryBinding;

public class CategoryFragment extends Fragment {

    private Context mContext;
    private List<CategoryDataModel> categoryModels = new ArrayList<>();
    private CategoryListAdapter categoryListAdapter;

    FragmentCategoryBinding binding;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(RecyclerView.VERTICAL);
        binding.featureRvList.setLayoutManager(layout);
        setCategoryModels(SingletonControl.getInstance().getCategoryList());

        categoryListAdapter = new CategoryListAdapter(mContext, categoryModels, CategoryListAdapter.CATEGORY_VIEW);
        binding.featureRvList.setAdapter(categoryListAdapter);
    }

    public void setCategoryModels(List<CategoryDataModel> categoryModels) {
        if (this.categoryModels == null)
            this.categoryModels = new ArrayList<CategoryDataModel>();

        if (this.categoryModels != null)
            this.categoryModels.clear();

        if (categoryModels != null)
            this.categoryModels.addAll(new ArrayList<>(categoryModels));

    }

    public void resumeAdapterAd(boolean isPauseOriginal){
        if (categoryListAdapter != null) {
            categoryListAdapter.onResume(isPauseOriginal);
        }
    }
    public void pauseAdapterAd(boolean isPauseOriginal){
        if (categoryListAdapter != null) {
            categoryListAdapter.onPause(isPauseOriginal);
        }
    }

    @Override
    public void onPause() {
        LoggerCustom.erorr("CategoryListFragment", "onPause");
        pauseAdapterAd(true);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggerCustom.erorr("CategoryListFragment", "onResume");
        resumeAdapterAd(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoggerCustom.erorr("MainActivity","onDestroy CategoryFragment");
        if(categoryListAdapter !=null)
            categoryListAdapter.releaseResource();
        categoryListAdapter =null;
        mContext = null;
        categoryModels = null;
    }

}
