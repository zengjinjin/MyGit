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

        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.putExtra("id", 12);
        intent1.putExtra("orderNumber", "568007150032");
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setPackage(getPackageName());
        Log.d("zjj", ">>>>>>>>>>intentStr<<<<<<<<<<< = " + intent1.toUri(Intent.URI_INTENT_SCHEME));
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
