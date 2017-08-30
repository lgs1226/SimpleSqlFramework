package com.simple.sqliteframework.bean;

import com.simple.sqliteframework.db.annotation.DbFiled;
import com.simple.sqliteframework.db.annotation.DbTable;

/**
 * Created by Administrator on 2017/8/23.
 */
@DbTable("tb_user")
public class User {

    @DbFiled("_id")
    private Integer id;

    @DbFiled("name")
    private String userName;

    @DbFiled("password")
    private String password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
