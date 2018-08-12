package com.zjj.cosco.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/* 
 * 获取、设置控件信息 
 */
public class WidgetController {

    /* 
     * 获取控件宽 
     */
    public static int getWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    /* 
     * 获取控件高 
     */
    public static int getHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }

    /* 
     * 设置控件所在的位置X，并且不改变宽高， 
     * X为绝对位置，此时Y可能归0 
     */
    public static void setLayoutX(View view, int x) {
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, margin.topMargin, x + margin.width, margin.bottomMargin);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    /* 
     * 设置控件所在的位置Y，并且不改变宽高， 
     * Y为绝对位置，此时X可能归0 
     */
    public static void setLayoutY(final View view, final int y) {
        // 当view加载完成后再调用
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
                margin.setMargins(margin.leftMargin, y, margin.rightMargin,margin.bottomMargin);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
                view.setLayoutParams(layoutParams);
            }
        });
    }

    public static void setLayoutYLinearLayout(final View view, final int y) {
        // 当view加载完成后再调用
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
                margin.setMargins(margin.leftMargin, y, margin.rightMargin,margin.bottomMargin);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
                view.setLayoutParams(layoutParams);
            }
        });
    }

    public static void setLayoutY_CoordinatorLayout(final View view, final int y) {
        // 当view加载完成后再调用
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
                margin.setMargins(margin.leftMargin, y, margin.rightMargin,margin.bottomMargin);
                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(margin);
                view.setLayoutParams(layoutParams);
            }
        });

    }


    public static void setLayoutYOfFrameLayoutLayout(final View view, final int y){
        // 当view加载完成后再调用
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
                margin.setMargins(margin.leftMargin, y, margin.rightMargin, margin.bottomMargin);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
                layoutParams.gravity = Gravity.NO_GRAVITY;
                view.setLayoutParams(layoutParams);
            }
        });
    }


    public static void setLayout_Y(View view, int y) {
        ViewGroup.LayoutParams vl = view.getLayoutParams();
        MarginLayoutParams marginLayoutParams;
        if (vl instanceof MarginLayoutParams) {
            marginLayoutParams = (MarginLayoutParams) vl;
        } else {
            marginLayoutParams = new MarginLayoutParams(vl);
        }
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin, y, marginLayoutParams.rightMargin, y + marginLayoutParams.height);
        view.setLayoutParams(marginLayoutParams);
    }


    /* 
     * 设置控件所在的位置YY，并且不改变宽高， 
     * XY为绝对位置 
     */
    public static void setLayout(View view, int x, int y) {
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}  
