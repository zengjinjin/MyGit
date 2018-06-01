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
import cosco.greendao.entity.Student;

/**
 * Created by administrator on 2018/5/31.
 */

public class GreenDaoAdapter extends RecyclerView.Adapter<GreenDaoAdapter.ViewHolder> {
    private List<Student> stuList;
    public GreenDaoAdapter(List<Student> stuList) {
        this.stuList = stuList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_greendao_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_id.setText(stuList.get(position).getId());
        holder.tv_name.setText(stuList.get(position).getName());
        holder.tv_age.setText(stuList.get(position).getAge()+"");
    }

    @Override
    public int getItemCount() {
        return stuList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_id)
        TextView tv_id;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_age)
        TextView tv_age;
        @BindView(R.id.tv_sex)
        TextView tv_sex;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
