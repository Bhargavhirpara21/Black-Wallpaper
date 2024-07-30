package wallpaper.black.live.uhd.AppUtils;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class CarousalRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final CarousalRecyclerPager carousalRecyclerPager;
    RecyclerView.Adapter<VH> vhAdapter;

    public CarousalRecyclerAdapter(CarousalRecyclerPager viewPager, RecyclerView.Adapter<VH> adapter) {
        vhAdapter = adapter;
        carousalRecyclerPager = viewPager;
        if (vhAdapter != null)
            setHasStableIds(vhAdapter.hasStableIds());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return vhAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        vhAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        vhAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        vhAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        return vhAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        vhAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);
        vhAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        vhAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        vhAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        vhAdapter.onBindViewHolder(holder, position);
        final View itemView = holder.itemView;
        ViewGroup.LayoutParams lp;
        if (itemView.getLayoutParams() == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = itemView.getLayoutParams();
            if (carousalRecyclerPager.getLayoutManager().canScrollHorizontally()) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        itemView.setLayoutParams(lp);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        vhAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public int getItemCount() {
        return vhAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return vhAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return vhAdapter.getItemId(position);
    }
}
