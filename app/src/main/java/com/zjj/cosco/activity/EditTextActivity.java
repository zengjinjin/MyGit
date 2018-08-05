package com.zjj.cosco.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.zjj.cosco.R;
import com.zjj.cosco.adapter.EditTextAdapter;
import com.zjj.cosco.entity.FieldInfoBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 2018/8/4.
 */

public class EditTextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        TextView tv = findViewById(R.id.tv);
        tv.setText("      fdf            ".trim());
        List<FieldInfoBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            list.add(new FieldInfoBean(""));
        }
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        EditTextAdapter adapter = new EditTextAdapter(list, this);
        rv.setAdapter(adapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.custom_divider));
        rv.addItemDecoration(divider);
    }
}
