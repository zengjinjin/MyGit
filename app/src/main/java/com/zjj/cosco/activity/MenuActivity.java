package com.zjj.cosco.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zjj.cosco.R;
import com.zjj.cosco.view.ArcMenuView;

/**
 * Created by administrator on 2018/8/5.
 */

public class MenuActivity extends Activity implements ArcMenuView.ArcMenuListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ArcMenuView menu = findViewById(R.id.menu_view);
        menu.setOnArcMenuListener(this);
    }

    @Override
    public void dealMenuClick(View v) {
        Toast.makeText(this, "这是"+v.getTag(), Toast.LENGTH_SHORT).show();
    }
}
