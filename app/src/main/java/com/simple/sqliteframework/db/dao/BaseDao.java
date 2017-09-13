package com.simple.sqliteframework.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.simple.sqliteframework.db.annotation.DbFiled;
import com.simple.sqliteframework.db.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/23.
 */

public class BaseDao<T> implements IBaseDao<T>{
    private SQLiteDatabase sqLiteDatabase;

    //保证只初始化一次
    private boolean isInit = false;

    //持有操作数据库表所对应的Java类型
    private Class<T> entityClazz;

    private String tableName;

    /**
     * 维护这表名与成员变量名的映射关系
     * key---》表名
     * value --》Field
     */
    private HashMap<String , Field> cacheMap;
    protected synchronized boolean init(Class<T> entity , SQLiteDatabase sqLiteDatabase){
        if (!isInit){
            this.entityClazz = entity;
            this.sqLiteDatabase = sqLiteDatabase;
            if (entityClazz.getAnnotation(DbTable.class) == null){
                tableName = entityClazz.getSimpleName();
            }
            else {
                tableName = entityClazz.getAnnotation(DbTable.class).value();
            }
            if (!sqLiteDatabase.isOpen()){
                return false;
            }
            if (!TextUtils.isEmpty(createTable())){
                sqLiteDatabase.execSQL(createTable());
            }
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }
        return isInit;
    }

    private void initCacheMap() {
        String sql = "select * from "+this.tableName+" limit 1 , 0";
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql , null);
            //表的列名数组
            String[] columnNames = cursor.getColumnNames();
//            Field[] columnFields = entityClazz.getFields();
            Field[] columnFields = entityClazz.getDeclaredFields();
            for (Field field:columnFields) {
                field.setAccessible(true);
            }
            for (String columnName:columnNames) {
                Field colmunFiled = null;
                for (Field field:columnFields) {
                    String fieldName = "";
                    if (field.getAnnotation(DbFiled.class) != null){
                        fieldName = field.getAnnotation(DbFiled.class).value();
                    }else {
                        fieldName = field.getName();
                    }
                    if (columnName.equals(fieldName)){
                        colmunFiled = field;
                        break;
                    }
                }
                if (colmunFiled != null){
                    cacheMap.put(columnName , colmunFiled);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    @Override
    public long insert(T entity) {
        Map<String, String> values = getValues(entity);
        ContentValues contentValue = getContentValue(values);
        return sqLiteDatabase.insert(tableName, null, contentValue);
    }

    @Override
    public long update(T entity, T where) {
        Map<String, String> whereMap = getValues(where);
        ContentValues contentValue = getContentValue(getValues(entity));
        Condition condition = new Condition(whereMap);
        return sqLiteDatabase.update(tableName, contentValue, condition.getWhereClause(), condition.getWhereArgs());
    }


    @Override
    public int delete(T where) {
        Map<String, String> whereMap = getValues(where);
        Condition condition = new Condition(whereMap);
        return sqLiteDatabase.delete(tableName, condition.getWhereClause(), condition.getWhereArgs());
    }

    @Override
    public ArrayList<T> query(T where) {
        return this.query(where , null , null , null);
    }

    @Override
    public ArrayList<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        String limitStr = null;
        if (startIndex != null && limit != null){
            limitStr = startIndex +" , "+limit;
        }
        Condition condition = new Condition(getValues(where));
        Cursor query = sqLiteDatabase.query(tableName, null, condition.getWhereClause(), condition.getWhereArgs(), null, null, orderBy, limitStr);
        ArrayList<T> result = getResult(query, where);
        query.close();
        return result;
    }

    private ArrayList<T> getResult(Cursor cursor , T where){
        ArrayList arrayList = new ArrayList();
        Object item = null;
        while (cursor.moveToNext()){
            try {
                item = where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Field> entry = iterator.next();
                    //获取列名
                    String key = entry.getKey();
                    Field value = entry.getValue();
                    int columnIndex = cursor.getColumnIndex(key);
                    Class type = value.getType();
                    if (columnIndex != -1){
                        if (type == String.class){
                            value.set(item , cursor.getString(columnIndex));
                        }
                        else if(type==Double.class)
                        {
                            value.set(item,cursor.getDouble(columnIndex));
                        }else  if(type==Integer.class)
                        {
                            value.set(item,cursor.getInt(columnIndex));
                        }else if(type==Long.class)
                        {
                            value.set(item,cursor.getLong(columnIndex));
                        }else  if(type==byte[].class)
                        {
                            value.set(item,cursor.getBlob(columnIndex));
                        }else {
                            continue;
                        }
                    }
                }
                arrayList.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    private ContentValues getContentValue(Map<String, String> values) {
        ContentValues contentValues = new ContentValues();
        Set<String> setKey = values.keySet();
        Iterator<String> iterator = setKey.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = values.get(key);
            if (value != null){
                contentValues.put(key , value);
            }
        }
        return contentValues;
    }

    private Map<String , String> getValues(T entity){
        HashMap<String , String> resultCache = new HashMap<>();
        Iterator<Field> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()){
            Field field = iterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if (field.getAnnotation(DbFiled.class) != null){
                cacheKey = field.getAnnotation(DbFiled.class).value();
            }else {
                cacheKey = field.getName();
            }
            try {
                if (null == field.get(entity)){
                    continue;
                }
                cacheValue = field.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            resultCache.put(cacheKey , cacheValue);
        }
        return resultCache;
    }

    /*
     *封装修改语句
     */
    class Condition{
        private String whereClause;

        private String[] whereArgs;

        public Condition(Map<String , String> whereMap) {
            StringBuilder strs = new StringBuilder();
            ArrayList<String> list = new ArrayList<>();
            strs.append(" 1=1 ");
            Set<String> keySet = whereMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = whereMap.get(key);
                if (value != null){
                    //拼接查询语句
                    strs.append(" and "+key+" =?");
                    list.add(value);
                }
            }
            this.whereClause = strs.toString();
            this.whereArgs = list.toArray(new String[list.size()]);
        }

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }

    public String createTable(){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("create table if not exists "+tableName+" (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
        Field[] fields = entityClazz.getDeclaredFields();
        for (Field field:fields) {
            String columnName = "";
            if (field.getAnnotation(DbFiled.class) != null){
                columnName = field.getAnnotation(DbFiled.class).value();
            }else {
                columnName = field.getName();
            }
            if (!columnName.equals("_id")){
                switch (field.getType().getSimpleName()){
                    case "int":
                    case "Integer":
                        strBuilder.append(columnName+" INTEGER,");
                        break;
                    case "String":
                    case "double":
                    case "Double":
                    case "float":
                    case "Float":
                    case "byte":
                    case "long":
                    case "Long":
                        strBuilder.append(columnName+" TEXT,");
                        break;
                }
            }
        }
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append(");");
        return strBuilder.toString();
    }
}
