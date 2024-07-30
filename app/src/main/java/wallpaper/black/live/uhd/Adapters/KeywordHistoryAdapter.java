package wallpaper.black.live.uhd.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.SearchKeywordDBHelper;
import wallpaper.black.live.uhd.Model.SearchKeywordModel;
import wallpaper.black.live.uhd.R;


public class KeywordHistoryAdapter extends RecyclerView.Adapter<KeywordHistoryAdapter.HistoryViewHolder> {

    private Context mContext;
    private ArrayList<SearchKeywordModel> keywordlist;
    private SearchKeywordDBHelper dbHelper;
    private OnItemClicked onClick;
    int currentPosition = -1;

    public KeywordHistoryAdapter(Context context, ArrayList<SearchKeywordModel> keywordList, OnItemClicked onClick) {
        this.mContext = context;
        this.keywordlist = keywordList;
        this.onClick = onClick;
        dbHelper =new SearchKeywordDBHelper(context);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_keyword, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SearchKeywordModel modal = keywordlist.get(position);
        holder.keywords.setText(modal.getKeyword());
    }

    @Override
    public int getItemCount() {
        return keywordlist.size();
    }

    public void updateData(List<SearchKeywordModel> keywordList) {
        this.keywordlist = new ArrayList<>(keywordList);
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView keywords;
        ImageView delete_icon;
        RelativeLayout search_keyword_lyt;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            keywords = itemView.findViewById(R.id.keywords);
            delete_icon = itemView.findViewById(R.id.img_delete);
            search_keyword_lyt = itemView.findViewById(R.id.rl_search_keyword);
            search_keyword_lyt.setOnClickListener(this);
            delete_icon.setOnClickListener(this);
            currentPosition=getAdapterPosition();
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();
            currentPosition=getAdapterPosition();
            switch (id){
                case R.id.img_delete:
                    openDeleteDialog(currentPosition);
                    break;
                case R.id.rl_search_keyword:
                    try {
                        onClick.onItemClick(keywordlist.get(currentPosition).getKeyword(),currentPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("AdapterSearch", "onClick: ");
                    break;
            }

        }
    }

    public interface OnItemClicked {
        void onItemClick(String keyword,int currentPosition);
    }

    public void openDeleteDialog(int currentPosition){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.AlertDialogCustom);
        builder.setTitle(keywordlist.get(currentPosition).getKeyword());
        builder.setMessage(R.string.remove_search);
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dbHelper.deleteKeyWord(keywordlist.get(currentPosition).getId());
                    keywordlist.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void releaseResource(){
        mContext =null;
//        private ArrayList<KeywordModal> keywordList;
        dbHelper =null;
        onClick=null;
    }
}
