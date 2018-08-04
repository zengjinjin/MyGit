package com.zjj.cosco.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.zjj.cosco.R;
import com.zjj.cosco.utils.GuideUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 2018/8/3.
 */

public class NinePhotoView extends ViewGroup {
    private boolean showAdd = true;
    private static final int MAX_PHOTO_NUMBER = 9;
    int hSpace = GuideUtil.dip2px(getContext(), 10);
    int vSpace = GuideUtil.dip2px(getContext(), 10);
    private int childWidth = 0;
    private int childHeight = 0;
    private int currentPhotoNum = 0;
    private int[] constImageIds = {
            R.drawable.crop2,R.drawable.crop2,R.drawable.crop2
            ,R.drawable.crop2,R.drawable.crop2,R.drawable.crop2
            ,R.drawable.crop2,R.drawable.crop2,R.drawable.crop2
    };

    public NinePhotoView(Context context) {
        this(context, null);
    }

    public NinePhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NinePhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View addPhotoView = new View(context);
        addView(addPhotoView);
        currentPhotoNum = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        KLog.i("zjj","========onMeasure==========");
        int rw = MeasureSpec.getSize(widthMeasureSpec);
        int rh = MeasureSpec.getSize(heightMeasureSpec);
        childWidth = (rw - 2 * hSpace)/3;
        childHeight = childWidth;
        int childCount = getChildCount();
        for (int i= 0; i< childCount; i++){
            View child = getChildAt(i);
            LayoutParams lParams = (LayoutParams)child.getLayoutParams();
            lParams.left = (i % 3) * (childWidth + hSpace);
            lParams.top = (i / 3) * (childWidth + vSpace);
        }
        int vw = rw;
        int vh = rh;
        if (childCount < 3){
            vw = childCount * (childWidth + hSpace);
        }
        vh = ((childCount + 3)/3)*(childHeight+vSpace);
        setMeasuredDimension(vw, vh);
    }

    @Override
    protected void onLayout(boolean b, int j, int i1, int i2, int i3) {
        KLog.i("zjj","========onLayout==========");
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            LayoutParams lParams = (LayoutParams) child.getLayoutParams();
            child.layout(lParams.left, lParams.top, lParams.left + childWidth, lParams.top + childHeight);
            if (i == childCount - 1 && showAdd){
                child.setBackgroundResource(R.drawable.image_add);
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addPhoto();
                    }
                });
            }else {
                child.setBackgroundResource(constImageIds[i]);
                child.setOnClickListener(null);
            }
        }
    }

    public void addPhoto() {
        if (currentPhotoNum >= MAX_PHOTO_NUMBER){
            showAdd = false;
        }else {
            showAdd = true;
            View newChild = new View(getContext());
            addView(newChild);
            currentPhotoNum++;
        }
        requestLayout();
        invalidate();
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public int left = 0;
        public int top = 0;

        public LayoutParams(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams arg0) {
            super(arg0);
        }

    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new NinePhotoView.LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new NinePhotoView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new NinePhotoView.LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof NinePhotoView.LayoutParams;
    }
}
