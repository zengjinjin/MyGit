package com.zjj.cosco.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zjj.cosco.R;
import com.zjj.cosco.utils.FileUtils;
import com.zjj.cosco.utils.GuideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by administrator on 2018/6/27.
 */

public class CanvasActivity extends Activity {
    @BindView(R.id.canvas1)
    ImageView canvas1;
    @BindView(R.id.canvas2)
    ImageView canvas2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        ButterKnife.bind(this);
//        Bitmap bitmap = drawReverseText("代收：￥3.00");
//        Bitmap bitmap = null;
//        bitmap = createOneDCode("BOC",200, 40);
        String charge = "30000.00";
        Bitmap bmp1 = drawSTOReverseText("代收货款", 190, 81, 24, true);
        Bitmap bmp2 = drawSTOReverseText("¥" + charge, 190, 75, 24, false);
//        canvas.drawBitmap(TOP_BOX_END_X - 200 + 8, TOP1_BOX_START_Y + 92 + 5, bmp1, paint);
//        canvas.drawBitmap(TOP_BOX_END_X - 200 + 8, TOP1_BOX_START_Y + 92 + 86, bmp2, paint);
        canvas1.setImageBitmap(bmp1);
        canvas2.setImageBitmap(bmp2);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                intentToExcel();
            }

        });
//        GuideUtil.getInstance().initGuide(this, R.drawable.guide);
        ViewTreeObserver observer = canvas1.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                canvas1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                GuideUtil.getInstance().initGuide(CanvasActivity.this, R.drawable.guide);
            }
        });
    }

    private Bitmap drawSTOReverseText(String charge, int width, int height, float textSize, boolean up) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextSize(textSize);//设置文字大小
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建一个包裹文字的背景图片
        Canvas mCanvas = new Canvas(bmp);
        mCanvas.drawColor(Color.BLACK);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        Rect rect1 = new Rect(0, 0, width, height);//创建一个包裹文字的矩形框
        float baselineY = rect1.centerY() - fontMetrics.bottom / 2 - fontMetrics.top / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText(charge, rect1.centerX(), up ? baselineY + 20 : baselineY - 20, mPaint);
        return bmp;
    }

    private void intentToExcel() {
        String[] mimeTypes =
                {
                        "application/pdf",
                        "application/msword",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                };

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pickIntent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            pickIntent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(pickIntent, "ChooseFile"), 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                Uri uri = data.getData();
                String path = FileUtils.getPathByUri(this, uri);
                Log.i("zjj", "path=" + path);
            }
        }
    }


    private Bitmap drawReverseText(String charge) {
        Bitmap bmp = Bitmap.createBitmap(50, 100, Bitmap.Config.ARGB_8888);// ARGB_8888就是由4（ARGB）个8位组成即32位
        // 位图位数越高代表其可以存储的颜色信息越多，当然图像也就越逼真
        Canvas mCanvas = new Canvas(bmp);
        mCanvas.drawColor(Color.BLACK);
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(3.0f);
        mPaint.setFilterBitmap(true);
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextSize(50); // 设置字体的大小
        mPaint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        mPaint.getTextBounds(charge, 0, charge.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (50 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        mCanvas.drawText(charge, 50 - bounds.width() / 2, baseline, mPaint);
        return bmp;
    }

    public static Bitmap createOneDCode(String content, int w, int h) {
        try {
            // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, w, h);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }
            Bitmap orginBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap,具体参考api
            orginBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            Bitmap bitmap = drawBg4Bitmap(Color.parseColor("#FF0000"), orginBitmap);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(), orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

}
