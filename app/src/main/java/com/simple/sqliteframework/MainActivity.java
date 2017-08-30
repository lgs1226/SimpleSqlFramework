package com.simple.sqliteframework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.simple.sqliteframework.bean.FileBean;
import com.simple.sqliteframework.bean.User;
import com.simple.sqliteframework.db.dao.BaseDao;
import com.simple.sqliteframework.db.dao.BaseDaoFactory;
import com.simple.sqliteframework.db.dao.IBaseDao;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private IBaseDao<User> userHelper;
    private IBaseDao<FileBean>  fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
        fileHelper = BaseDaoFactory.getInstance("user.db").getDataHelper(BaseDao.class, FileBean.class);
        userHelper = BaseDaoFactory.getInstance("user.db").getDataHelper(BaseDao.class, User.class);
    }

    public void insert(View view){
        FileBean fileBean = new FileBean("2017/08/29 20:54" , "data/data/test");
        User user = new User("liugs"  , "123456");
        long fileResult = fileHelper.insert(fileBean);
        long userResult = userHelper.insert(user);
        Log.e("insertResult" , fileResult+","+userResult);
    }

    public void delete(View view){
        User user = new User();
        user.setUserName("liugs");
        FileBean fileBean = new FileBean();
        fileBean.setPath("data/data/test");
        int fileResult = fileHelper.delete(fileBean);
        int userResult = userHelper.delete(user);
        Log.e("deleteResult" , userResult+","+fileResult);
    }

    public void update(View view){
        FileBean fileBean = new FileBean("2017/8/29 20:54" , "sdcard/myfile/data");
        FileBean fileBean1 = new FileBean();
        fileBean.setPath("data/data/test");
        User user = new User("liugs" , "123456");
        User userWhere = new User();
        userWhere.setUserName("liugs");
        long update = fileHelper.update(fileBean, fileBean1);
        long update1 = userHelper.update(user, userWhere);
        Log.e("updateResult" , update+","+update1);
    }

    public void query(View view){
        FileBean fileBean = new FileBean();
        fileBean.setPath("data/data/test");
        User user = new User();
        user.setUserName("liugs");
        ArrayList<FileBean> query = fileHelper.query(fileBean);
        ArrayList<User> query1 = userHelper.query(user);
        for (FileBean f:query) {
            Log.e("FileBean" , f.toString());
        }
        for (User user1:query1) {
            Log.e("User" , user1.toString());
        }
    }
}
