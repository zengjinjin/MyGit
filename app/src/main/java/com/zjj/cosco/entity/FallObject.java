package com.zjj.cosco.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.util.Random;

/**
 * Created by administrator on 2018/8/7.
 */

public class FallObject {
    private int initX;
    private int initY;
    private Random random;
    private int parentWidth;//父容器宽度
    private int parentHeight;//父容器高度
    private float objectWidth;//下落物体宽度
    private float objectHeight;//下落物体高度

    public int initSpeed;//初始下降速度

    public float presentX;//当前位置X坐标
    public float presentY;//当前位置Y坐标
    public float presentSpeed;//当前下降速度
    private boolean isSpeedRandom;//物体初始下降速度比例是否随机
    private boolean isSizeRandom;//物体初始大小比例是否随机

    private static Bitmap bitmap;
    public Builder builder;

    private static final int defaultSpeed = 10;//默认下降速度

    public FallObject(Builder builder){
        this.builder = builder;
        this.initSpeed = builder.initSpeed;
        this.bitmap = builder.bitmap;
        isSpeedRandom = builder.isSpeedRandom;
        isSizeRandom = builder.isSizeRandom;
    }

    public FallObject(Builder builder, int parentWidth, int parentHeight){
        random = new Random();
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        initX = random.nextInt(parentWidth);
        initY = random.nextInt(parentHeight) - parentHeight;
        presentX = initX;
        presentY = initY;
        this.builder = builder;
        this.bitmap = builder.bitmap;
        initSpeed = builder.initSpeed;
        isSpeedRandom = builder.isSpeedRandom;
        isSizeRandom = builder.isSizeRandom;
        randomSpeed();
        randomSize();
    }

    public static class Builder{
        private int initSpeed;
        private Bitmap bitmap;
        private boolean isSpeedRandom;//物体初始下降速度比例是否随机
        private boolean isSizeRandom;//物体初始大小比例是否随机

        public Builder(Drawable drawable){
            this.initSpeed = defaultSpeed;
            this.bitmap = drawableToBitmap(drawable);
        }

        public Builder setSpeed(int speed, boolean isSpeedRandom){
            this.initSpeed = speed;
            this.isSpeedRandom = isSpeedRandom;
            return this;
        }

        public Builder setSize(int w, int h, boolean isSizeRandom){
            this.bitmap = changeBitmapSize(this.bitmap, w, h);
            this.isSizeRandom = isSizeRandom;
            return this;
        }

        public FallObject build(){
            return new FallObject(this);
        }
    }

    public void drawObject(Canvas canvas){
        moveObject();
        canvas.drawBitmap(bitmap, presentX, presentY, null);
    }

    public void moveObject(){
        moveY();
        if (presentY > parentHeight){
            reset();
        }
    }

    public void moveY(){
        presentY += presentSpeed;
    }

    public void reset(){
        presentY = -objectHeight;
        randomSpeed();
    }

    private void randomSpeed(){
        if (isSpeedRandom){
            presentSpeed = (float) (random.nextInt(5)*0.1*initSpeed);
        }else {
            presentSpeed = initSpeed;
        }
    }

    /**
     * 随机物体初始大小比例
     */
    private void randomSize(){
        if(isSizeRandom){
            float r = (random.nextInt(10)+1)*0.1f;
            float rW = r * builder.bitmap.getWidth();
            float rH = r * builder.bitmap.getHeight();
            bitmap = changeBitmapSize(builder.bitmap,(int)rW,(int)rH);
        }else {
            bitmap = builder.bitmap;
        }
        objectWidth = bitmap.getWidth();
        objectHeight = bitmap.getHeight();
    }

    private static Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap changeBitmapSize(Bitmap bitmap, int newW, int newH){
        int oldW = bitmap.getWidth();
        int oldH = bitmap.getHeight();
        float scaleW = ((float)newW) / oldW;
        float scaleH = ((float)newH) / oldH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, oldW, oldH, matrix, true);
        return bitmap;
    }
}






































