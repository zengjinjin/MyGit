package com.zjj.cosco;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * Created by zjj on 2017/9/12.
 */

public class IDCardScanActivity extends Activity implements Camera.PreviewCallback {
    private DetectThread mDetectThread = null;
    private Preview mPreview = null;
    private Camera mCamera = null;
    private int numberOfCameras;
    private int defaultCameraId;
    private int screen_width, screen_height;// 屏幕分辨率
    volatile private boolean isAuto = false;
    public boolean CAMERA_TYPE = false;
    public boolean IS_SHOW_DECH_VIEW = false;
    private int cameraWith, cameraHeight;
    private String fileImg = "";

    private TextView tv_title_des;
    private Button bt_title_more;
    private ImageView iv_title_back;
    private RelativeLayout relativeGroup;
    private Context context;
    private Camera.AutoFocusCallback autoFocusCallback;
    private String orderId;
    private boolean localOCR;
    private Intent intent;

    private String idCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+"/skuaidi/pic/realname/";
    protected void onBack(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();
        // Hide the window title.
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        CAMERA_TYPE = intent.getBooleanExtra("type", false);
        orderId = intent.getStringExtra("orderId");
        IS_SHOW_DECH_VIEW = intent.getBooleanExtra("box", false);
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);

        setContentView(R.layout.activity_read_preview);
        relativeGroup = (RelativeLayout) findViewById(R.id.layout_preview);

        relativeGroup.addView(mPreview);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();
        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }

        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCamera != null) {
                    try {
                        mCamera.autoFocus(afc);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mCamera.cancelAutoFocus();
                    }
                }
                return false;
            }
        });

    }

    private Camera.AutoFocusCallback afc = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                if (CAMERA_TYPE && !isOrcDrawble && !isAuto) {
                    isAuto = true;
                }

                setDisplayOrientation();
                mCamera.cancelAutoFocus();
            } else {
                isAuto = false;
                mCamera.cancelAutoFocus();
            }
        }
    };

    //聚焦
    private void sendHandlerMessageAF(){
//        mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreview == null) {
            mPreview = new Preview(this);
            relativeGroup.addView(mPreview);
        }
        try {
            mCamera = Camera.open(defaultCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            showFailedDialogAndFinish();
            return;
        }
        mPreview.setCamera(mCamera);
        setDisplayOrientation();
//        mCamera.setOneShotPreviewCallback(this);
        mCamera.setPreviewCallback(this);
        sendHandlerMessageAF();
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCamera != null) {
            Camera camera = mCamera;
            mCamera = null;
//            camera.setOneShotPreviewCallback(null);
            camera.setPreviewCallback(null);
            camera.release();
            isAuto = false;
            isOrcDrawble = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetectThread != null) {
            mDetectThread.stopRun();
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
        mPreview.setCamera(null);
        mPreview = null;
        mHandler.removeMessages(MSG_AUTO_FOCUS);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (CAMERA_TYPE) {
            if (!isAuto) {
                resumePreviewCallback();
                return;
            }
        }
        if (CAMERA_TYPE) {
            Size size = camera.getParameters().getPreviewSize();
            cameraWith = size.width;
            cameraHeight = size.height;
            if (mDetectThread == null) {
                mDetectThread = new DetectThread();
                mDetectThread.start();
                // 两秒聚焦
                sendHandlerMessageAF();
            }
            mDetectThread.addDetect(data, size.width, size.height);
        }

    }

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    public void saveBitmap2File(Bitmap mbitmap, String path) {
        Bitmap mtemp = mbitmap;
        File picfile = new File(path);
        if (!picfile.exists()) {
            try {
                picfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(picfile);
            mtemp.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getDataImg(byte[] data) throws Exception {
        Rect rect = new Rect(0, 0, cameraWith, cameraHeight);
        YuvImage img = new YuvImage(data, ImageFormat.NV21, cameraWith, cameraHeight, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (img.compressToJpeg(rect, 100, baos)) {
            Bitmap image = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
            return getRotateBitmap(image, 0);
        }
        return null;
    }

    private void showFailedDialogAndFinish() {
        new AlertDialog.Builder(this)
                .setMessage("无法连接到相机,请检查权限设置")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                })
                .create().show();
    }

    private boolean isOrcDrawble = false;


    // thread to detect and recognize.
    private class DetectThread extends Thread {
        private ArrayBlockingQueue<byte[]> mPreviewQueue = new ArrayBlockingQueue<byte[]>(1);

        public void stopRun() {

            addDetect(new byte[]{0}, -1, -1);
        }

        @Override
        public void run() {
            boolean isOk = false;
            try {
                while (true) {
                    byte[] data = mPreviewQueue.take();// block here, if no data in the queue.

                    if (CAMERA_TYPE) {
                        if (data.length == 1) {// quit the thread, if we got
                            // special byte array put by
                            // stopRun().
                            return;
                        }

                        if (isAuto) {
                            isOrcDrawble = true;
                            isAuto = false;
                            Bitmap image = getDataImg(data);
                            if (image != null) {
                                saveBitmap2File(image, fileImg);
                            }
                        }
                        if (!isOk) {
                            isOrcDrawble = false;
                            mPreview.showBorder(null, false);
                        }
                    }
                    if (!isOk) {
                        resumePreviewCallback();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addDetect(byte[] data, int width, int height) {
            if (mPreviewQueue.size() == 1) {
                mPreviewQueue.clear();
            }
            mPreviewQueue.add(data);
        }

    }

    private void resumePreviewCallback() {
        if (mCamera != null) {
            mCamera.setOneShotPreviewCallback(this);
        }
    }

    private void setDisplayOrientation() {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(defaultCameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
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
        int result = (info.orientation - degrees + 360) % 360;
        mCamera.setDisplayOrientation(result);
    }

    public boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    private static final int MSG_AUTO_FOCUS = 100;
    private static final int MSG_NO_AUTO_FOCUS = 1001;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_FOCUS) {
                autoFocus();
            }else if(msg.what == MSG_NO_AUTO_FOCUS){
                autoFocus();
            }
        }

        ;
    };

    private void autoFocus() {
        if (mCamera != null) {
            try {
                mCamera.autoFocus(afc);
            } catch (Exception e) {
                e.printStackTrace();
                mCamera.cancelAutoFocus();
            }
            if (!Build.MANUFACTURER.contains("samsung")) {
                sendHandlerMessageAF();
            } else {
            }
        }
    }

    private Rect rct = new Rect();

    /**
     * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the
     * Camera to the surface. We need to center the SurfaceView because not all devices have cameras
     * that support preview sizes at the same aspect ratio as the device's display.
     */
    @SuppressLint("NewApi")
    private class Preview extends ViewGroup implements SurfaceHolder.Callback {
        private final String TAG = "Preview";
        private SurfaceView mSurfaceView = null;
        private SurfaceHolder mHolder = null;
        private Size mPreviewSize = null;
        private List<Size> mSupportedPreviewSizes = null;
        private Camera mCamera = null;
        private DetectView mDetectView = null;
        private TextView mInfoView = null;
        //        private TextView mCopyRight = null;
        private ImageView mbutton = null;
        private TextView mToast = null;

        public Preview(Context context) {
            super(context);
            LayoutParams lp = new LayoutParams(screen_width, screen_height);
            setLayoutParams(lp);
            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView);

            mDetectView = new DetectView(context);
            addView(mDetectView);
            if (!IS_SHOW_DECH_VIEW) {
                mDetectView.setVisibility(GONE);
            }


            LayoutParams lp1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mInfoView = new TextView(context);
            mInfoView.setLayoutParams(lp1);
            mInfoView.setTextColor(Color.WHITE);
            mInfoView.setText("*请横屏拍摄整张身份证");
//            mInfoView.setEms(1);
            mInfoView.setGravity(Gravity.CENTER);
            addView(mInfoView);


            mToast = new TextView(context);
            mToast.setLayoutParams(lp1);
            mToast.setTextColor(Color.RED);
            mToast.setText("识别失败，请保持照片清晰和完整");
//            mToast.setEms(1);
            mToast.setGravity(Gravity.CENTER);
            addView(mToast);
            mToast.setVisibility(GONE);

            mbutton = new ImageView(context);
            if (!CAMERA_TYPE) {
                mbutton.setBackgroundResource(R.drawable.btn_camrea);
            }
            addView(mbutton);

            if(autoFocusCallback == null){
                autoFocusCallback = new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        doTakePicture(rct.width(), rct.height());
                    }
                };
            }
            mbutton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.cancelAutoFocus();
                    if(mCamera != null){
                        try {
                            mCamera.autoFocus(autoFocusCallback);
                        }catch (Exception e){
                            doTakePicture(rct.width(), rct.height());
                        }
                    }

                }
            });
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
        }

        public void doTakePicture(int w, int h) {
            try {
                if (mCamera != null) {
                    mCamera.takePicture(null, null, mRectJpegPictureCallback);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * 拍摄指定区域的Rect
         */
        Camera.PictureCallback mRectJpegPictureCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                boolean isOk = false;
                Bitmap b = null;
                if (null != data) {
                    b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                    if(mCamera != null) {
                        mCamera.stopPreview();
                    }
                }
                //保存图片到sdcard
                Bitmap rectBitmap = null;
                if (null != b) {
                    //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                    //图片竟然不能旋转了，故这里要旋转下
                    Bitmap rotaBitmap = b;
                    rectBitmap = Bitmap.createBitmap(rotaBitmap, 0, 0, rotaBitmap.getWidth(), rotaBitmap.getHeight());
                    saveBitmap2File(rectBitmap, fileImg);
                    if (!rotaBitmap.isRecycled()) {
                        rotaBitmap.recycle();
                        rotaBitmap = null;
                    }
                }
//                if (!isOk) {
//                    //再次进入预览
//                    try {
//                        mCamera.startPreview();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
                if (b != null && !b.isRecycled()) {
                    b.recycle();
                    b = null;
                }

            }
        };

        public void setCamera(Camera camera) {
            mCamera = camera;
            if (mCamera != null) {
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                requestLayout();
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // We purposely disregard child measurements because act as a
            // wrapper to a SurfaceView that centers the camera preview instead
            // of stretching it.
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height, width);// 竖屏模式，寬高颠倒
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed && getChildCount() > 0) {
                final View child = getChildAt(0);

                final int width = r - l;
                final int height = b - t;
                int previewWidth = width;
                int previewHeight = height;
                if (mPreviewSize != null) {
                    previewWidth = mPreviewSize.height;
                    previewHeight = mPreviewSize.width;
                }

                // Center the child SurfaceView within the parent.
                if (width * previewHeight > height * previewWidth) {
                    final int scaledChildWidth = previewWidth * height / previewHeight;
                    //这个在oppr7下有问题
//                    child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2,
//                                    height);
                    child.layout(0, 0, width, height);
//                    mDetectView.layout((width - scaledChildWidth) / 2, 0,
//                                    (width + scaledChildWidth) / 2, height);
                    mDetectView.layout(0, 0, width, height);
                } else {
                    final int scaledChildHeight = previewHeight * width / previewWidth;
                    child.layout(0, (height - scaledChildHeight) / 2, width,
                            (height + scaledChildHeight) / 2);
                    mDetectView.layout(0, (height - scaledChildHeight) / 2, width,
                            (height + scaledChildHeight) / 2);
                }
                // 设置提示拍摄照片有误文字位置（文字默认隐藏）
                int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
                mToast.measure(w,h);
                int vl_width = mToast.getMeasuredWidth();// mToast宽度
                int vl_height = mToast.getMeasuredHeight();// mToast高度
                int left_off = (width-vl_width)/2;
                int top_off = (height-vl_height)/2;
                mToast.layout(left_off, top_off, left_off+vl_width, top_off+vl_height);
                mToast.setRotation(90);
                // 设置拍照按钮位置
                mbutton.measure(w,h);
                int btn_width = (int) (mbutton.getMeasuredWidth()*1.5);
                int btn_height = (int) (mbutton.getMeasuredHeight()*1.5);
                int btn_left_off = (width-btn_width)/2;
                mbutton.layout(btn_left_off,height-btn_height,btn_left_off+btn_width,height);
                // 设置请横屏拍摄整张身份证文字位置
                mInfoView.measure(w,h);
                int info_width = mInfoView.getMeasuredWidth();
                int info_height = mInfoView.getMeasuredHeight();
                int info_left_off = (width-info_width)/2;
                int info_top_off = info_height;
                mInfoView.layout(info_left_off,info_top_off,info_left_off+info_width,b);
            }
        }

        private void showAnimation() {
            final ValueAnimator animator1 = ObjectAnimator.ofFloat(mToast, "alpha", 1, 0);//淡出效果
            animator1.setDuration(2000);
            animator1.setInterpolator(new AccelerateInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();//合起来就是左淡出效果
            animatorSet.play(animator1);
            animatorSet.start();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where to draw.
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(holder);
                }
            } catch (IOException exception) {
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null)
                return null;
            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Size size : sizes) {
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
                for (Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (mCamera != null) {
                // Now that the size is known, set up the camera parameters and begin the preview.

                Camera.Parameters parameters = mCamera.getParameters();
                try {
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
                    parameters.setPreviewFormat(ImageFormat.NV21);
                    requestLayout();
                    mDetectView.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    mCamera.setParameters(parameters);
                }catch (Exception e){
                    requestLayout();
                }
                mCamera.startPreview();
            }
        }

        public void showBorder(int[] border, boolean match) {

            mDetectView.showBorder(border, match);
        }
    }

    /**
     * the view show bank card border.
     */
    private class DetectView extends View {
        private Paint paint = null;
        private int[] border = null;
        private String result = null;
        private boolean match = false;
        private int previewWidth;
        private int previewHeight;

        public void showBorder(int[] border, boolean match) {
            this.border = border;
            this.match = match;
            postInvalidate();
        }

        public DetectView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(0xffff0000);
        }

        public void setPreviewSize(int width, int height) {
            this.previewWidth = width;
            this.previewHeight = height;
        }

        @Override
        public void onDraw(Canvas c) {

            if (border != null) {
                paint.setColor(0xff00ff00);
                paint.setStrokeWidth(3);
                int height = getWidth();
                float scale = getWidth() / (float) previewHeight;
                c.drawLine(border[0] * scale, border[1] * scale, border[2] * scale, border[3]
                        * scale, paint);
                c.drawLine(border[2] * scale, border[3] * scale, border[4] * scale, border[5]
                        * scale, paint);
                c.drawLine(border[4] * scale, border[5] * scale, border[6] * scale, border[7]
                        * scale, paint);
                c.drawLine(border[6] * scale, border[7] * scale, border[0] * scale, border[1]
                        * scale, paint);
            }
            if (match) {
                paint.setColor(0xff00ff00);
                paint.setStrokeWidth(20);
            } else {
                paint.setColor(0xffff0000);
                paint.setStrokeWidth(3);
            }

            float left, top, right, bottom;
            float dis = 1 / 8f;
            left = getWidth() * dis;
            right = getWidth() - left;

            top = (getHeight() - (getWidth() - left - left) / 0.618f) / 2;
            bottom = getHeight() - top;

            c.save();
            c.restore();
            paint.setColor(0xff000fff);
            paint.setStyle(Paint.Style.STROKE);
//          c.drawRect(left, top, right, bottom, paint);
            //绘制四周蒙版
            paint.setStrokeWidth(5);
            paint.setColor(0xa0000000);
            paint.setStyle(Paint.Style.FILL);
            rct.left = (int) left;
            rct.top = (int) top;
            rct.right = (int) right;
            rct.bottom = (int) (getHeight() - top);
            //左
            c.drawRect(0, top, left, bottom, paint);
            //上
            c.drawRect(0, 0, getWidth(), top, paint);
            //右
            c.drawRect(right, top, getWidth(), bottom, paint);
            //下
            c.drawRect(0, getHeight() - top, getWidth(), getHeight(), paint);

            if (match) {
                paint.setColor(0xffff0000);
            } else {
                paint.setColor(0xff00ff00);
            }
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.FILL);
            //绘制四角边框
            Path path1 = new Path();
            path1.moveTo(left - 5, top - 10);
            path1.lineTo(left - 5, top + (bottom - top) / 12);
            path1.lineTo(left + 10, top + (bottom - top) / 12);
            path1.lineTo(left + 10, top + (bottom - top) / ((bottom - top) / 5));
            path1.lineTo(left + (right - left) / 9, top + (bottom - top) / ((bottom - top) / 5));
            path1.lineTo(left + (right - left) / 9, top - 10);
            path1.lineTo(left - 5, top - 10);
            //左上角
            c.drawPath(path1, paint);

            //左下角
            Path path2 = new Path();
            path2.moveTo(left - 5, bottom + 10);
            path2.lineTo(left - 5, bottom - (bottom - top) / 12);
            path2.lineTo(left + 10, bottom - (bottom - top) / 12);
            path2.lineTo(left + 10, bottom - (bottom - top) / ((bottom - top) / 5));
            path2.lineTo(left + (right - left) / 9, bottom - (bottom - top) / ((bottom - top) / 5));
            path2.lineTo(left + (right - left) / 9, bottom + 10);
            path2.moveTo(left - 5, bottom + 10);
            //左下角
            c.drawPath(path2, paint);

            //右上角
            Path path3 = new Path();
            path3.moveTo(right + 5, top - 10);
            path3.lineTo(right + 5, top + (bottom - top) / 12);
            path3.lineTo(right - 10, top + (bottom - top) / 12);
            path3.lineTo(right - 10, top + (bottom - top) / ((bottom - top) / 5));
            path3.lineTo(right - (right - left) / 9, top + (bottom - top) / ((bottom - top) / 5));
            path3.lineTo(right - (right - left) / 9, top - 10);
            path3.moveTo(right + 5, top - 10);
            c.drawPath(path3, paint);

            //右下角
            Path path4 = new Path();
            path4.moveTo(right + 5, bottom + 10);
            path4.lineTo(right + 5, bottom - (bottom - top) / 12);
            path4.lineTo(right - 10, bottom - (bottom - top) / 12);
            path4.lineTo(right - 10, bottom - (bottom - top) / ((bottom - top) / 5));
            path4.lineTo(right - (right - left) / 9, bottom - (bottom - top) / ((bottom - top) / 5));
            path4.lineTo(right - (right - left) / 9, bottom + 10);
            path4.moveTo(right + 5, bottom + 10);
            c.drawPath(path4, paint);

        }
    }
}
