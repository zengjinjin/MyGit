package com.zjj.cosco;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zjj.cosco.utils.FileUtils;
import com.zjj.cosco.utils.ImportExcelUtils;

/**
 * Created by administrator on 2018/7/11.
 */

public class ImportExcelActivity extends Activity {
    private TextView tv_path;
    private Button btn_import;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_excel);
        tv_path = findViewById(R.id.tv_path);
        btn_import = findViewById(R.id.btn_import);
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importExcel(ImportExcelActivity.this);
            }
        });
    }

    public void importExcel(Activity context){
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
        context.startActivityForResult(Intent.createChooser(pickIntent, "ChooseFile"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Uri uri = data.getData();
                String path = FileUtils.getPathByUri(ImportExcelActivity.this, uri);
                Log.i("zjj","path="+path);
                tv_path.setText(path);
            }
        }
    }
}
