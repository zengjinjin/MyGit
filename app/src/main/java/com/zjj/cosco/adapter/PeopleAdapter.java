package com.zjj.cosco.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.zjj.cosco.R;
import com.zjj.cosco.entity.People;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by administrator on 2018/8/10.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.MyViewHolder> {
    private Context context;
    private List<People> list;
    private ReplyAdapter adapter;
    private ReplySubWdUserCallBack mReplySubWdUserCallBack;

    public PeopleAdapter(Context context, List<People> list){
        this.context = context;
        this.list = list;
    }

    public void setReplySubWdUser(ReplySubWdUserCallBack replySubWdUser){
        this.mReplySubWdUserCallBack = replySubWdUser;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_content.setText(list.get(position).getContent());
        holder.tv_zanNum.setText(list.get(position).getZanNum()+"");
        holder.iv.setImageResource("1".equals(list.get(position).getZan()) ? R.drawable.express_zan_press : R.drawable.express_zan_default);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(list.get(position).getZan())) {
                    list.get(position).setZan("0");
                } else{
                    list.get(position).setZan("1");
                }
                int zanNum = list.get(position).getZanNum();
                list.get(position).setZanNum(++zanNum);
                holder.tv_zanNum.setText(list.get(position).getZanNum()+"");
                holder.iv.setImageResource("1".equals(list.get(position).getZan()) ? R.drawable.express_zan_press : R.drawable.express_zan_default);
            }
        });
        adapter = new ReplyAdapter(list.get(position).getReply());
        holder.rv_replay.setLayoutManager(new LinearLayoutManager(context));
        holder.rv_replay.setAdapter(adapter);
        adapter.setmOnRecyclerviewItemClickListener(new ReplyAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(int innerPosition) {
                mReplySubWdUserCallBack.onReplySubWdUser(position, innerPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.rv_replay)
        RecyclerView rv_replay;
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.tv_zanNum)
        TextView tv_zanNum;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 评论子网点用户
     */
    public interface ReplySubWdUserCallBack{
        void onReplySubWdUser(int outPosition,int innerPosition);
    }
}
