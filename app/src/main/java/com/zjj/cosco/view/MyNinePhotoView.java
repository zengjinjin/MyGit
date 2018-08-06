package com.zjj.cosco.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.socks.library.KLog;
import com.zjj.cosco.utils.GuideUtil;

/**
 * Created by administrator on 2018/8/4.
 */

public class MyNinePhotoView extends ViewGroup {

    public MyNinePhotoView(Context context) {
        this(context, null);
    }

    public MyNinePhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //调用系统的onMeasure一般是测量自己(当前ViewGroup)的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //wrap_content
        int realWidth = 0;
        int realHeight = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE){
                continue;
            }
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()){//需要换行
                realWidth = Math.max(realWidth, lineWidth);//上一行的宽度
                realHeight = realHeight + lineHeight;//上一行的高度
                lineWidth = childWidth;//当前行的宽度
                lineHeight = childHeight;//当前行的高度
            }else {
                lineWidth = lineWidth + childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (i == childCount - 1){
                realWidth = Math.max(realWidth, lineWidth);
                realHeight = realHeight + lineHeight;
            }
        }
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : realWidth + getPaddingLeft() + getPaddingRight(),
                heightMode == MeasureSpec.EXACTLY ? heightSize : realHeight + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mViewGroupWidth = getMeasuredWidth();//当前ViewGroup的总宽度
        KLog.i("zjj","mViewGroupWidth="+GuideUtil.px2dp(getContext(),mViewGroupWidth));
        KLog.i("zjj","l="+l);
        KLog.i("zjj","t="+t);
        int x = getPaddingLeft();//当前绘制光标的X坐标
        int y = getPaddingTop();//当前绘制光标的Y坐标
        int lineHeight = 0;
        KLog.i("zjj","x="+ GuideUtil.px2dp(getContext(), x)+",y="+GuideUtil.px2dp(getContext(), y));
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            if (x + childWidth + params.leftMargin + params.rightMargin + getPaddingRight() > mViewGroupWidth){
                x = getPaddingLeft();
                y = y + lineHeight;
            }else {
                lineHeight = Math.max(lineHeight, childHeight + params.topMargin + params.bottomMargin);
            }
            childView.layout(x + params.leftMargin, y + params.topMargin,
                    x + childWidth + params.leftMargin, y + childHeight + params.topMargin);
            x = x + params.leftMargin + childWidth + params.rightMargin;//下一次开始绘制的X坐标
        }
    }

    private static class MyLayoutParams extends MarginLayoutParams{

        public MyLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MyLayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public MyLayoutParams(LayoutParams arg0) {
            super(arg0);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MyLayoutParams(MyLayoutParams.WRAP_CONTENT, MyLayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MyLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MyLayoutParams;
    }
}
