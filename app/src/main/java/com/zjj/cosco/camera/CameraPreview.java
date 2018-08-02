package com.zjj.cosco.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zjj.cosco.activity.PhotoActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by administrator on 2018/6/4.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Activity activity;
    private SurfaceHolder mHolder;
    private Camera camera;
    private boolean mIsPortrait;
    private Camera.Parameters parameters;
    private List<Camera.Size> mPreviewSizeList;
    private List<Camera.Size> mPictureSizeList;

    public CameraPreview(Activity context){
        super(context);
        activity = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void initCamera(int width, int height) {
        setCameraDisplayOrientation();//将预览旋转90度
        parameters = camera.getParameters();//获取camera的parameter实例
        mPreviewSizeList = parameters.getSupportedPreviewSizes();//获取所有支持的camera尺寸
        mPictureSizeList = parameters.getSupportedPictureSizes();
        Camera.Size previewSize = getOptimalPreviewSize(mPreviewSizeList, height, width);//获取一个最为适配的camera.size
        Camera.Size pictureSize = determinePictureSize(previewSize);
        parameters.setPreviewSize(previewSize.width, previewSize.height);//把camera.size赋值到parameters
        parameters.setPictureSize(previewSize.width, previewSize.height);//把camera.size赋值到parameters
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);//把parameters设置给camera
        camera.startPreview();//开始预览
        startAutoFocus();
        Log.e("zjj", "surfaceView.getWidth()=" + getWidth());
        Log.e("zjj", "surfaceView.getHeight()=" + getHeight());
        Log.e("zjj", "previewSize.width=" + previewSize.width);
        Log.e("zjj", "previewSize.height=" + previewSize.height);
        Log.e("zjj", "pictureSize.width=" + pictureSize.width);
        Log.e("zjj", "pictureSize.height=" + pictureSize.height);
    }

    private void startAutoFocus() {
        CameraThreadPool.createAutoFocusTimerTask(new Runnable() {
            @Override
            public void run() {
                synchronized (CameraPreview.this) {
                    if (camera != null) {
                        try {
                            camera.autoFocus(new Camera.AutoFocusCallback() {
                                @Override
                                public void onAutoFocus(boolean success, Camera camera) {
                                }
                            });
                        } catch (Throwable e) {
                            // startPreview是异步实现，可能在某些机器上前几次调用会autofocus failß
                        }
                    }
                }
            }
        });
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    protected Camera.Size determinePictureSize(Camera.Size previewSize) {
        Camera.Size retSize = null;
        for (Camera.Size size : mPictureSizeList) {
            if (size.equals(previewSize)) {
                return size;
            }
        }
        // if the preview size is not supported as a picture size
        double reqRatio = previewSize.width / previewSize.height;//640);//1280);//reqPreviewWidth);// ((double) reqPreviewWidth) / reqPreviewHeight;
        double curRatio, deltaRatio;
        double deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : mPictureSizeList) {
            //curRatio = ((double) size.width) / size.height;
            //deltaRatio = Math.abs(reqRatio - curRatio);
            curRatio = ((double) size.width);
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    public void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        if (result == 90) {
            mIsPortrait = true;
        }
        Log.i("zjj", "result=" + result);
    }

    public void takePictrue(){
        cancelAutoFocus();
        camera.takePicture(null, null, pictureCallback);
    }

    private void cancelAutoFocus() {
        camera.cancelAutoFocus();
        CameraThreadPool.cancelAutoFocusTimer();
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.e("zjj", "b1.getWidth()=" + b.getWidth());
            Log.e("zjj", "b1.getHeight()=" + b.getHeight());
            // data为完整数据
            File file = new File("/sdcard/photo.png");
            // 使用流进行读写
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    fos.write(data);
                    // 关闭流
                    fos.close();
                    // 查看图片
                    Intent intent = new Intent(activity,
                            PhotoActivity.class);
                    // 传递路径
                    intent.putExtra("path", file.getAbsolutePath());
                    activity.startActivity(intent);
                    activity.finish();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            camera.startPreview();
        }
    };
}
