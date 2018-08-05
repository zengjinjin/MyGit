package com.zjj.cosco.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.socks.library.KLog;

/**
 * Created by administrator on 2018/8/5.
 */

public class ArcMenuView extends ViewGroup implements View.OnClickListener{
    /**
     * 菜单按钮
     */
    private View mCBMenu;
    private int mRadius = 520;

    private enum Status{
        OPEN, CLOSE
    }
    private Status mCurStatus = Status.CLOSE;

    public ArcMenuView(Context context) {
        this(context, null);
    }

    public ArcMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        KLog.i("zjj","changed="+changed);
        if (changed){
            layoutMainMenu();
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++){
                View childView = getChildAt(i+1);
                childView.setVisibility(GONE);
                int left = (int) (mRadius*Math.cos(Math.PI/2/(count-2)*i));
                int top = (int) (mRadius*Math.sin(Math.PI/2/(count-2)*i));
                left = getMeasuredWidth() - left-childView.getMeasuredWidth();
                top = getMeasuredHeight() - top-childView.getMeasuredHeight();
                childView.layout(left, top, left+childView.getMeasuredWidth(), top+childView.getMeasuredHeight());
            }
        }
    }

    private void layoutMainMenu(){
        mCBMenu = getChildAt(0);
        mCBMenu.setOnClickListener(this);
        int left = getMeasuredWidth() - mCBMenu.getMeasuredWidth();
        int top = getMeasuredHeight() - mCBMenu.getMeasuredHeight();
        mCBMenu.layout(left, top, left + mCBMenu.getMeasuredWidth(), top + mCBMenu.getMeasuredHeight());
    }

    @Override
    public void onClick(View view) {
        //为菜单按钮设置点击动画
        RotateAnimation rAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rAnimation.setDuration(300);
        rAnimation.setFillAfter(true);
        view.startAnimation(rAnimation);
        dealChildMenu(300);
    }

    private void dealChildMenu(int duration){
        int count = getChildCount();
        for (int i = 0; i < count-1; i++){
            final View childView = getChildAt(i+1);
            AnimationSet set = new AnimationSet(true);
            //1.首先是平移动画
            TranslateAnimation tAnimation = null;
            //平移的X方向和Y方向的距离
            int x = (int) (mRadius*Math.cos(Math.PI/2/(count-2)*i));
            int y = (int) (mRadius*Math.sin(Math.PI/2/(count-2)*i));
            //平移的标志，是平移一个整数还是一个负数
            int xflag = 1;
            int yflag = 1;
            if (mCurStatus == Status.CLOSE){//如果当前状态为关闭，则应该打开
                tAnimation = new TranslateAnimation(xflag*x, 0, yflag*y, 0);
                tAnimation.setDuration(duration);
                tAnimation.setFillAfter(true);// true:图片停在动画结束位置
                childView.setVisibility(VISIBLE);
            }else {//如果当前状态为打开，则应该关闭
                tAnimation = new TranslateAnimation(0, xflag*x, 0, yflag*y);
                tAnimation.setDuration(duration);
                tAnimation.setFillAfter(true);
            }
            tAnimation.setStartOffset((i*100)/count);
            tAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurStatus == Status.CLOSE)
                        childView.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            RotateAnimation rAnimation = new RotateAnimation(0f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rAnimation.setDuration(duration);
            rAnimation.setFillAfter(true);
            set.addAnimation(rAnimation);
            set.addAnimation(tAnimation);
            childView.startAnimation(set);

            final int curPos = i+1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickAnimation(curPos);
                    if (mListener != null){
                        mListener.dealMenuClick(childView);
                    }
                    changeStatus();
                }
            });
        }
        changeStatus();;
    }

    /**
     * 菜单项的点击动画
     * @param cPos  用来判断当前点击的是哪一个菜单
     */
     private void clickAnimation(int cPos) {
         for(int i = 0; i < getChildCount()-1; i++) {
             View childView = getChildAt(i+1);
             if(i+1 == cPos) {
                 AnimationSet set = new AnimationSet(true);
                 ScaleAnimation sAnimation = new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f,
                         Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
                 sAnimation.setFillAfter(true);
                 AlphaAnimation alAnimation = new AlphaAnimation(1.0f, 0f);
                 alAnimation.setFillAfter(true);
                 set.addAnimation(sAnimation);
                 set.addAnimation(alAnimation);
                 set.setDuration(300);
                 childView.startAnimation(set);
             }else {
                 AnimationSet set = new AnimationSet(true);
                 ScaleAnimation sAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                         Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
                 sAnimation.setFillAfter(true);
                 AlphaAnimation alAnimation = new AlphaAnimation(1.0f, 0f);
                 alAnimation.setFillAfter(true);
                 set.addAnimation(sAnimation);
                 set.addAnimation(alAnimation);
                 set.setDuration(300);
                 childView.startAnimation(set);
             }
             childView.setVisibility(GONE);
         }
     }

    private void changeStatus(){
        mCurStatus = (mCurStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    public interface ArcMenuListener {
        void dealMenuClick(View v);
    }

    public void setOnArcMenuListener(ArcMenuListener listener){
        mListener = listener;
    }

    private ArcMenuListener mListener;
}
