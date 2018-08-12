package com.zjj.cosco.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * 键盘显示隐藏监听
 * Created by gudongdong on 2017/3/29.
 */

public class SoftKeyboardStateHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private List<OnSoftKeyboardStateChangedListener> mKeyboardStateListeners;      //软键盘状态监听列表
    private boolean mIsSoftKeyboardShowing;

    private View activityRootView;
    private Activity activity;

    public SoftKeyboardStateHelper(Activity activity, View view){
        this.activityRootView = view;
        this.activity = activity;
        mIsSoftKeyboardShowing = false;
        mKeyboardStateListeners = new LinkedList<>();
        //注册布局变化监听
        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        // 屏幕高度。这个高度不含虚拟按键的高度
        int screenHeight = activityRootView.getRootView().getHeight();
        //判断窗口可见区域大小
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。
        int heightDifference = screenHeight - (r.bottom - r.top);
        boolean isKeyboardShowing = heightDifference > screenHeight/3;

        //如果之前软键盘状态为显示，现在为关闭，或者之前为关闭，现在为显示，则表示软键盘的状态发生了改变
        if ((mIsSoftKeyboardShowing && !isKeyboardShowing) || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
            mIsSoftKeyboardShowing = isKeyboardShowing;
            for (int i = 0; i < mKeyboardStateListeners.size(); i++) {
                mKeyboardStateListeners.get(i).OnSoftKeyboardStateChanged(mIsSoftKeyboardShowing, heightDifference);
            }
        }
        /*//如果之前软键盘状态为显示，现在为关闭，或者之前为关闭，现在为显示，则表示软键盘的状态发生了改变
        if ((mIsSoftKeyboardShowing && !isKeyboardShowing) || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
            mIsSoftKeyboardShowing = isKeyboardShowing;
            for (int i = 0; i < mKeyboardStateListeners.size(); i++) {
                CircleCommentDetailActivity.OnSoftKeyboardStateChangedListener listener = mKeyboardStateListeners.get(i);
                listener.OnSoftKeyboardStateChanged(mIsSoftKeyboardShowing, heightDifference);
            }
        }*/
    }

    public interface OnSoftKeyboardStateChangedListener {
        void OnSoftKeyboardStateChanged(boolean isKeyBoardShow, int keyboardHeight);
    }

    //注册软键盘状态变化监听
    public void addSoftKeyboardChangedListener(OnSoftKeyboardStateChangedListener listener) {
        if (listener != null) {
            mKeyboardStateListeners.add(listener);
        }
    }
    //取消软键盘状态变化监听
    public void removeSoftKeyboardChangedListener(OnSoftKeyboardStateChangedListener listener) {
        if (listener != null) {
            mKeyboardStateListeners.remove(listener);
        }
    }

    public void removeGlobalLayoutListener(){
        //移除布局变化监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            activity.getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }






    /*public interface SoftKeyboardStateListener {
        void onSoftKeyboardStateChanged(boolean isKeyBoardShow,int keyBoardHeight);
    }

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<>();
    private final View activityRootView;
    private boolean    isSoftKeyboardOpened;

    // 状态栏的高度
    private int statusBarHeight;
    // 软键盘的高度
    private int keyboardHeight;

    public SoftKeyboardStateHelper(Activity activity,View activityRootView) {
        this(activity,activityRootView, false);
    }

    public SoftKeyboardStateHelper(Activity activity,View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView     = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        statusBarHeight = Utility.getStatusBarHeight(activity.getApplicationContext());
    }

    @Override
    public void onGlobalLayout() {
        // 判断窗口可见区域大小
        Rect r = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(r);

        // 屏幕高度。这个高度不含虚拟按键的高度
        int screenHeight = activityRootView.getRootView().getHeight();
        int heightDiff = screenHeight - (r.bottom - r.top);

        // 在不显示软键盘时，heightDiff等于状态栏的高度
        // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
        // 所以heightDiff大于状态栏高度时表示软键盘出现了，
        // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
        if(keyboardHeight == 0 && heightDiff > statusBarHeight){
            keyboardHeight = heightDiff - statusBarHeight;
        }

        if (isSoftKeyboardOpened) {
            // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
            // 说明这时软键盘已经收起
            if (heightDiff <= statusBarHeight) {
                isSoftKeyboardOpened = false;
            }
        } else {
            // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
            // 说明这时软键盘已经弹出
            if (heightDiff > statusBarHeight) {
                isSoftKeyboardOpened = true;
            }
        }

        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardStateChanged(isSoftKeyboardOpened,keyboardHeight);
            }
        }
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }*/
}
