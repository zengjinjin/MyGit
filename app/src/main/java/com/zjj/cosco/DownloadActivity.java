package com.zjj.cosco;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.zjj.cosco.utils.AppDownloadManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by administrator on 2018/7/5.
 */

public class DownloadActivity extends Activity {
    private DownloadCompleteReceiver receiver;
    private AppDownloadManager mDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);
        mDownloadManager = new AppDownloadManager(this);
    }

    @OnClick({R.id.download})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.download:
                Toast.makeText(DownloadActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                String apkUrl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
//                downloadBySystem(apkUrl, "", "");
                showUpdateDialog(apkUrl);
                break;
        }
    }

    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        // 设置下载路径和文件名

        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimeType);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setMimeType("application/vnd.android.package-archive");

        // 设置为可被媒体扫描器找到

        request.allowScanningByMediaScanner();

        // 设置为可见和可管理

        request.setVisibleInDownloadsUi(true);

        long refernece = dManager.enqueue(request);
    }

    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (isNetworkAvailable(DownloadActivity.this)){
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                        Toast.makeText(DownloadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(DownloadActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo nInfo = connectivity.getActiveNetworkInfo();
            if (nInfo != null && nInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDownloadManager != null) {
            mDownloadManager.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDownloadManager != null) {
            mDownloadManager.onPause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
        }
    }

    private void showUpdateDialog(final String apkUrl) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("提示");
        dialog.setMax(100);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String title = "qq_mobile.apk";
                        String desc = "版本更新";

                        mDownloadManager.setUpdateListener(new AppDownloadManager.OnUpdateListener() {
                            @Override
                            public void update(int currentByte, int totalByte) {
//                                dialog.setProgress(currentByte, totalByte);
                                if ((currentByte == totalByte) && totalByte != 0) {
                                    dialog.dismiss();
                                }

                            }
                        });
                        mDownloadManager.downloadApk(apkUrl, title, desc);
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
        dialog.setMessage("这是一个水平进度条");
        dialog.show();
    }
}
