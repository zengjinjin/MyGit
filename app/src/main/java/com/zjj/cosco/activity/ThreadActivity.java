package com.zjj.cosco.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zjj.cosco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by administrator on 2018/6/1.
 */

public class ThreadActivity extends AppCompatActivity {
    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_add})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_add:
//                refreshUI1();
                refreshUI2();
//                refreshUI3();
                break;
        }
    }

    private void refreshUI1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                tv.post(new Runnable(){
                    @Override
                    public void run() {
                        //更新UI;
                        tv.setText("曾金金");
                    }
                });
            }
        }).start();
    }

    private void refreshUI2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable(){
                    @Override
                    public void run() {
                        //更新UI;
                        tv.setText("曾金金");
                    }
                });
            }
        }).start();
    }

    private void refreshUI3(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                tv.post(new Runnable(){
                    @Override
                    public void run() {
                        //更新UI;
                        tv.setText("曾金金");
                    }
                });
            }
        }).start();
    }
}
