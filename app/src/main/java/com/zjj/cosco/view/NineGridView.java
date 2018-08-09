package com.zjj.cosco.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;

/**
 * Created by administrator on 2018/8/6.
 */

public class NineGridView extends ViewGroup {
    private OnTagClickListener onTagClickListener;

    public NineGridView(Context context) {
        this(context, null);
    }

    public NineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //调用系统的onMeasure一般是测量自己(当前ViewGroup)的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        KLog.i("zjj","widthSize="+widthSize+",heightSize="+heightSize);

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
            MyLayoutParams params = (MyLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()){//需要换行
                realWidth = Math.max(realWidth, lineWidth);//上一行的宽度
                realHeight = realHeight + lineHeight;//上一行的高度
                lineWidth = childWidth;//当前行的宽度
                lineHeight = childHeight;//当前行的高度
                params.left = getPaddingLeft() + params.leftMargin;//子View左上角的X坐标
                params.top = getPaddingTop() + realHeight + params.topMargin;//子View左上角的Y坐标
            }else {
                params.left = getPaddingLeft() + lineWidth + params.leftMargin;
                params.top = getPaddingTop() + realHeight + params.topMargin;
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
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE){
                continue;
            }
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            MyLayoutParams params = (MyLayoutParams) childView.getLayoutParams();
            childView.layout(params.left, params.top, params.left + childWidth, params.top + childHeight);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        KLog.i("zjj","event.getAction()="+event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                View view = findChildView(x, y);
                int position = findChildViewPosition(view);
                if (onTagClickListener != null){
                    onTagClickListener.onTagClick(position);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private View findChildView(int x, int y){
        for (int i = 0; i < getChildCount(); i++){
            View childView = getChildAt(i);
            if (childView.getVisibility() == VISIBLE){
                Rect rect = new Rect();
                childView.getHitRect(rect);
                if (rect.contains(x, y)){
                    return childView;
                }
            }
        }
        return null;
    }

    private int findChildViewPosition(View view){
        if (view == null){
            return -1;
        }
        for (int i = 0; i < getChildCount(); i++){
            if (view == getChildAt(i)){
                return i;
            }
        }
        return -1;
    }

    public interface OnTagClickListener {

        boolean onTagClick(int position);

        void onSelected(SparseArray<View> selectedViews);

    }

    private static class MyLayoutParams extends MarginLayoutParams{
        public int left = 0;
        public int top = 0;

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
        return new NineGridView.MyLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new NineGridView.MyLayoutParams(NineGridView.MyLayoutParams.WRAP_CONTENT, NineGridView.MyLayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new NineGridView.MyLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof NineGridView.MyLayoutParams;
    }
}
