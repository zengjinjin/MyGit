package com.zjj.cosco;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.zjj.cosco.helper.MySQLiteOpenHelper;

import org.greenrobot.greendao.database.Database;

import cosco.greendao.dao.DaoMaster;
import cosco.greendao.dao.DaoSession;

/**
 * Created by administrator on 2018/5/31.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private DaoSession mDaoSession;

    public synchronized DaoSession getDaoSession(){
        if (mDaoSession == null){
            initDaoSession();
        }
        return mDaoSession;
    }

    private void initDaoSession(){
        try{
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, "cosco.db", null);
            Database db = helper.getWritableDb();
            DaoMaster daoMaster = new DaoMaster(db);
            mDaoSession = daoMaster.newSession();
        }catch (Exception e){
            if(e!=null && !TextUtils.isEmpty(e.getMessage())){
                if (e.getMessage().contains("database or disk is full")){
                    Toast.makeText(this, "内部存储空间已满！请清理空间", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance(){
        return instance;
    }
}
