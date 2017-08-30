package com.simple.sqliteframework.db.dao;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by Administrator on 2017/8/23.
 */

public class BaseDaoFactory {
    private String sqliteDataPath;
    private SQLiteDatabase sqLiteDatabase;
    private static BaseDaoFactory instance;

    private BaseDaoFactory(String dbName) {
        sqliteDataPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+dbName;
        openDatabase();
    }

    public synchronized <T extends BaseDao<M> , M> T getDataHelper(Class<T> clazz , Class<M> entityClass){
        BaseDao baseDao = null;
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClass , sqLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    private void openDatabase() {
        this.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDataPath , null);
    }

    public static BaseDaoFactory getInstance(String dbName){
        return instance = new BaseDaoFactory(dbName);
    }
}
