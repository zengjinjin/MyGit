package com.zjj.cosco.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.socks.library.KLog;
import com.zjj.cosco.entity.FallObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 2018/8/7.
 */

public class FallingView extends View {
    private Context mContext;
    private AttributeSet mAttrs;

    private int viewWidth;
    private int viewHeight;

    private static final int defaultWidth = 600;//默认宽度
    private static final int defaultHeight = 1000;//默认高度
    private static final int intervalTime = 5;//重绘间隔时间

    private int snowY;
    private List<FallObject> fallObjects;

    public FallingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public FallingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttrs = attrs;
        init();
    }

    private void init(){
        fallObjects = new ArrayList<>();
        snowY = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureSize(defaultWidth, widthMeasureSpec);
        int height = measureSize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);

        viewWidth = width;
        viewHeight = height;
    }

    private int measureSize(int defaultSize, int measureSpec){
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else if (specMode == MeasureSpec.AT_MOST){
            result = Math.min(defaultSize, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fallObjects.size() > 0){
            for (int i = 0; i < fallObjects.size(); i++){
                fallObjects.get(i).drawObject(canvas);
            }
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, intervalTime);
        }
    }

    public void addFallObject(final FallObject fallObject, final int num){
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < num; i++){
                    FallObject newFallObject = new FallObject(fallObject.builder, viewWidth, viewHeight);
                    fallObjects.add(newFallObject);
                }
                invalidate();
                return true;
            }
        });
    }
}






























