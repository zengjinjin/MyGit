package com.zjj.cosco.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zjj.cosco.R;
import com.zjj.cosco.permission.PermissionActivity;
import com.zjj.cosco.permission.PermissionListener;
import com.zjj.cosco.utils.GuideUtil;
import com.zjj.cosco.view.CameraTopRectView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by administrator on 2018/7/9.
 */

public class RectCameraActivity extends PermissionActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private SurfaceView mySurfaceView = null;
    private SurfaceHolder mySurfaceHolder = null;
    private CameraTopRectView topView = null; //自定义顶层view
    private Camera myCamera = null;
    private Camera.Parameters myParameters;
    private TextView takeTxt;
    private TextView cancelTxt;
    private boolean isPreviewing = false;
    private Bitmap bm;
    private static final String IMG_PATH = "SHZQ";
    private File file;
    private Uri uri;
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window myWindow = this.getWindow();
        myWindow.setFlags(flag, flag);

        setContentView(R.layout.my_camera_activity);
        mySurfaceView = (SurfaceView) findViewById(R.id.sv_camera);
        mySurfaceView.setZOrderOnTop(false);
        mySurfaceHolder = mySurfaceView.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mySurfaceHolder.addCallback(this);
        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        topView = (CameraTopRectView) findViewById(R.id.top_view);
        takeTxt = (TextView) findViewById(R.id.txt_take);
        cancelTxt = (TextView) findViewById(R.id.txt_cancel);
        takeTxt.setClickable(false);
        cancelTxt.setClickable(false);
        takeTxt.setOnClickListener(this);
        cancelTxt.setOnClickListener(this);

        topView.draw(new Canvas());
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        if (myCamera == null) {
                            initCamera();
                        }
                        try {
                            myCamera.setPreviewDisplay(mySurfaceHolder);
                            myCamera.startPreview();
                            isPreviewing = true;
                            takeTxt.setClickable(true);
                            cancelTxt.setClickable(true);
                            takeTxt.setText("拍照");
                            Log.i("zjj","onResume()");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void permissionDenied() {
                        Toast.makeText(RectCameraActivity.this, "相机权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (myCamera != null) {
            isPreviewing = false;
            takeTxt.setClickable(false);
            cancelTxt.setClickable(false);
            myCamera.release(); // release the camera for other applications
            myCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(myCamera != null){
            myCamera.release();
            myCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            Log.i("zjj","surfaceCreated()");
            if(myCamera == null){
                return;
            }
            myCamera.setPreviewDisplay(mySurfaceHolder);
            myCamera.startPreview();
            isPreviewing = true;
            takeTxt.setClickable(true);
            cancelTxt.setClickable(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(myCamera != null){
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }

    }

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {

        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };
    Camera.PictureCallback myRawCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub

        }
    };
    Camera.PictureCallback myjpegCalback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//              myCamera.stopPreview();
//              myCamera.release();
//              myCamera = null;
                isPreviewing = false;
                takeTxt.setText("确定");

                Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, topView.getViewWidth(), topView.getViewHeight(), true);
                bm = Bitmap.createBitmap(sizeBitmap,
                        topView.getRectLeft(),
                        topView.getRectTop(),
                        topView.getRectRight() - topView.getRectLeft(),
                        topView.getRectBottom() - topView.getRectTop());// 截取
            }
        }
    };

    // 初始化摄像头
    public void initCamera() {
        if (myCamera == null) {
            myCamera = getCameraInstance();
        }
        if (myCamera != null) {
            myParameters = myCamera.getParameters();
            myParameters.setPictureFormat(PixelFormat.JPEG);
            myParameters.set("rotation", 90);
            if (getCameraFocusable() != null) {
                myParameters.setFocusMode(getCameraFocusable());
            }
//          myParameters.setPreviewSize(1280, 720);
//          myParameters.setPictureSize(2048, 1152); // 1280, 720

            //设置PreviewSize和PictureSize

            Camera.Size previewSize = getPropPreviewSize(myParameters.getSupportedPreviewSizes(),
                    GuideUtil.getInstance().getScreenHeight(this), GuideUtil.getInstance().getScreenHeight(this));
            myParameters.setPreviewSize(previewSize.width, previewSize.height);
            Camera.Size pictureSize = getPropPictureSize(myParameters.getSupportedPictureSizes(), previewSize.width, previewSize.height);
            myParameters.setPictureSize(pictureSize.width, pictureSize.height);

            Log.i("zjj","这样也挺好的啊");
            myCamera.setDisplayOrientation(90);
            myCamera.setParameters(myParameters);
        } else {
            Toast.makeText(this, "相机错误！", Toast.LENGTH_SHORT).show();
        }

    }

    public Camera.Size getPropPreviewSize(List<Camera.Size> list, int minWidth, int minHeight) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height == minWidth) && s.width >= minHeight) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = list.size() - 1;//如果没找到，就选最大的size
        }
        return list.get(i);
    }

    public Camera.Size getPropPictureSize(List<Camera.Size> list, int minWidth, int minHeight) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if (s.height == minHeight && s.width == minWidth) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = list.size() - 1;//如果没找到，就选最大的size
        }
        return list.get(i);
    }

        public class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.txt_take:
                if (isPreviewing) {
                    // 拍照
                    myCamera.takePicture(myShutterCallback, myRawCallback, myjpegCalback);
                } else {
                    // 保存图片
                    File file = getOutputMediaFile();
                    Log.i("zjj","file="+file.getAbsolutePath().toString());
                    this.file = file;
                    this.uri = Uri.fromFile(file);
                    if (file != null && bm != null) {
                        FileOutputStream fout;
                        try {
                            fout = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fout);
                            // Bitmap mBitmap = Bitmap.createScaledBitmap(bm,
                            // topView.getViewWidth(), topView.getViewHeight(),
                            // false);
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            // bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //返回数据
                        Intent intent = new Intent(RectCameraActivity.this, PhotoActivity.class);
                        intent.putExtra("path", file.getAbsolutePath());
                        intent.setData(uri);
                        Bundle bundle = new Bundle();
                        intent.putExtras(bundle);
//                        setResult(0x1001, intent);
                        startActivity(intent);
                    }
//                    finish();
                }
                break;
            case R.id.txt_cancel:
                if (isPreviewing) {
                    finish();
                    // 退出相机
                } else {
                    if(myCamera == null){
                        initCamera();
                    }
                    // 重新拍照
                    try {
                        myCamera.setPreviewDisplay(mySurfaceHolder);
                        myCamera.startPreview();
                        isPreviewing = true;
                        takeTxt.setText("拍照");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("zjj","=============e.getMessage()================"+e.getMessage());
                    }
                }
                break;
            default:
                break;
        }

    }

    private String getCameraFocusable() {
        List<String> focusModes = myParameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            return Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return Camera.Parameters.FOCUS_MODE_AUTO;
        }
        return null;
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            if (getCameraId() >= 0) {
                c = Camera.open(getCameraId()); // attempt to get a Camera
                // instance
            } else {
                return null;
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e("getCameraInstance", e.toString());
        }
        return c; // returns null if camera is unavailable
    }

    private int getCameraId() {
        if (!checkCameraHardware(this)) {
            return -1;
        }
        int cNum = Camera.getNumberOfCameras();
        int defaultCameraId = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < cNum; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }
        return defaultCameraId;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile() {
        File mediaStorageDir = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMG_PATH);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
        } else {
            Toast.makeText(this, "没有sd卡", Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
