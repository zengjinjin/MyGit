package com.zjj.cosco.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zjj.cosco.R;
import com.zjj.cosco.entity.FieldInfoBean;

import java.util.List;

/**
 * Created by administrator on 2018/8/4.
 */

public class EditTextAdapter extends RecyclerView.Adapter<EditTextAdapter.EditTextItemHolder> {
    List<FieldInfoBean> fieldsList;//存放数据
    Context context;

    public EditTextAdapter(List<FieldInfoBean> list, Context context) {
        this.fieldsList = list;
        this.context = context;
    }

    @Override
    public EditTextItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EditTextItemHolder holder = new EditTextItemHolder(LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false));
        return holder;
    }

    //在这里可以获得每个子项里面的控件的实例，比如这里的TextView,子项本身的实例是itemView，
// 在这里对获取对象进行操作
    //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
    //position是点击位置
    @Override
    public void onBindViewHolder(EditTextItemHolder holder,final  int position) {
        final FieldInfoBean bean = fieldsList.get(position);
        if (holder instanceof EditTextItemHolder) {
            //1、为了避免TextWatcher在第2步被调用，提前将他移除。
            if (((EditTextItemHolder) holder).et_text.getTag() instanceof TextWatcher) {
                ((EditTextItemHolder) holder).et_text.removeTextChangedListener((TextWatcher) (((EditTextItemHolder) holder).et_text.getTag()));
            }

            // 第2步：移除TextWatcher之后，设置EditText的Text。
            ((EditTextItemHolder) holder).et_text.setText(bean.getValue());

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        bean.setValue("");
                    } else {
                        bean.setValue(editable.toString());
                    }
                }
            };
            ((EditTextItemHolder) holder).et_text.addTextChangedListener(watcher);
            ((EditTextItemHolder) holder).et_text.setTag(watcher);
        }
    }

    //要显示的子项数量
    @Override
    public int getItemCount() {
        return fieldsList.size();
    }

    //这里定义的是子项的类，不要在这里直接对获取对象进行操作
    public class EditTextItemHolder extends RecyclerView.ViewHolder {

        EditText et_text;

        public EditTextItemHolder(View itemView) {
            super(itemView);
            et_text = itemView.findViewById(R.id.edittext);
        }
    }
}
