package com.zjj.cosco.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zjj.cosco.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by administrator on 2018/8/10.
 */

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MyViewHolder> {
    private List<String> list;
    private OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener = null;

    public void setmOnRecyclerviewItemClickListener(OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener) {
        this.mOnRecyclerviewItemClickListener = mOnRecyclerviewItemClickListener;
    }

    public ReplyAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public ReplyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReplyAdapter.MyViewHolder holder, final int position) {
        holder.tv.setText(list.get(position));
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerviewItemClickListener != null){
                    mOnRecyclerviewItemClickListener.onItemClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv)
        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRecyclerviewItemClickListener {
        void onItemClickListener(int position);
    }
}
