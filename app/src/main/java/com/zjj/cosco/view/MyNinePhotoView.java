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
        //测量所有子控件的宽和高,只有先测量了所有子控件的尺寸，后面才能使用child.getMeasuredWidth()
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        //调用系统的onMeasure一般是测量自己(当前ViewGroup)的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mViewGroupWidth = getMeasuredWidth();//当前ViewGroup的总宽度
        KLog.i("zjj","mViewGroupWidth="+GuideUtil.px2dp(getContext(),mViewGroupWidth));
        KLog.i("zjj","l="+l);
        KLog.i("zjj","t="+t);
        int x = l + getPaddingLeft();//当前绘制光标的X坐标
        int y = getPaddingTop();//当前绘制光标的Y坐标
        KLog.i("zjj","x="+ GuideUtil.px2dp(getContext(), x)+",y="+GuideUtil.px2dp(getContext(), y));
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            MyLayoutParams params = (MyLayoutParams) childView.getLayoutParams();
            if (x + childWidth + params.leftMargin + params.rightMargin + getPaddingRight() > mViewGroupWidth){
                x = l + getPaddingLeft();
                y = y + childHeight + params.topMargin + params.bottomMargin;
            }
            childView.layout(x + params.leftMargin, y + params.topMargin,
                    x + childWidth + params.leftMargin, y + childHeight + params.topMargin);
            x = x + childWidth + params.rightMargin;//下一次开始绘制的x坐标
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
