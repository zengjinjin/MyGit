package com.zjj.cosco.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.greenrobot.greendao.database.Database;

import cosco.greendao.dao.DaoMaster;
import cosco.greendao.dao.StudentDao;

/**
 * 数据库更新
 * Created by lgg
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    private static final String TAG = "greenDao";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 注意把所新版本的表的xxDao都添加到这里
        MigrationHelper.getInstance().migrate(db, StudentDao.class);
    }
}
