package com.zjj.cosco.view;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.zjj.cosco.R;
import com.zjj.cosco.permission.PermissionActivity;
import com.zjj.cosco.permission.PermissionListener;

/**
 * Created by administrator on 2018/7/23.
 */

public class TestActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void click(View view) {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        Toast.makeText(TestActivity.this, "权限被允许", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void permissionDenied() {
                        Toast.makeText(TestActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}