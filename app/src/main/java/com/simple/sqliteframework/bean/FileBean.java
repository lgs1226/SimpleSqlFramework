package com.simple.sqliteframework.bean;

import com.simple.sqliteframework.db.annotation.DbFiled;
import com.simple.sqliteframework.db.annotation.DbTable;

/**
 * Created by Administrator on 2017/8/25.
 */
@DbTable("tb_file")
public class FileBean {

    @DbFiled("_id")
    private Integer id;

    @DbFiled("date")
    private String date;

    @DbFiled("path")
    private String path;

    public FileBean() {
    }

    public FileBean(String date, String path) {
        this.date = date;
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
