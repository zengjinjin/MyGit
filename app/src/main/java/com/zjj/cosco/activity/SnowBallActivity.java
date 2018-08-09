package com.zjj.cosco.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.zjj.cosco.R;
import com.zjj.cosco.entity.FallObject;
import com.zjj.cosco.view.FallingView;

/**
 * Created by administrator on 2018/8/7.
 */

public class SnowBallActivity extends Activity{
    private FallingView fallingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snow_ball);
        Paint snowPaint = new Paint();
        snowPaint.setColor(Color.WHITE);
        snowPaint.setStyle(Paint.Style.FILL);
        Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(25, 25, 25, snowPaint);

        FallObject.Builder builder = new FallObject.Builder(ContextCompat.getDrawable(this, R.drawable.icon_snow));
        FallObject fallObject = builder.setSpeed(10, true).setSize(80, 80, true).build();
        fallingView = findViewById(R.id.fallingView);
        fallingView.addFallObject(fallObject, 50);//添加50个雪球对象
    }
}
