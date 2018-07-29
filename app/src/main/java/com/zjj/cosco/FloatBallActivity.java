package com.zjj.cosco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.zjj.cosco.service.FloatViewService;
import com.zjj.cosco.utils.ScreenParam;

/**
 * Created by administrator on 2018/7/17.
 */

public class FloatBallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_ball);
        ScreenParam.getInstance().init(this);
        Intent intent = new Intent(FloatBallActivity.this, FloatViewService.class);
        //启动FloatViewService
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁悬浮窗
        Intent intent = new Intent(FloatBallActivity.this, FloatViewService.class);
        //终止FloatViewService
        stopService(intent);
    }
}
