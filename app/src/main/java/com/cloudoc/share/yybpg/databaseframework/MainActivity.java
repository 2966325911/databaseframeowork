package com.cloudoc.share.yybpg.databaseframework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cloudoc.share.yybpg.databaseframework.bean.Photo;
import com.cloudoc.share.yybpg.databaseframework.bean.User;
import com.cloudoc.share.yybpg.databaseframework.db.BaseDao;
import com.cloudoc.share.yybpg.databaseframework.db.BaseDaoFactory;
import com.cloudoc.share.yybpg.databaseframework.db.BaseDaoNewImpl;
import com.cloudoc.share.yybpg.databaseframework.db.IBaseDao;
import com.cloudoc.share.yybpg.databaseframework.sub_sqlite.BaseDaoSubFactory;
import com.cloudoc.share.yybpg.databaseframework.sub_sqlite.PhotoDao;
import com.cloudoc.share.yybpg.databaseframework.sub_sqlite.UserDao;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int i = 0;
    BaseDao baseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao = BaseDaoFactory.getOurInstance().getBaseDao(UserDao.class,User.class);
    }

    /**
     * 插入
     * @param view
     */
    public void clickInsert(View view) {
        IBaseDao baseDao = BaseDaoFactory.getOurInstance().getBaseDao(BaseDaoNewImpl.class,
                User.class);
        baseDao.insert(new User("1","Jet","111111"));
        showToast("插入成功");
    }

    /**
     * 跟新
     * @param view
     */
    public void clickUpdate(View view) {
        BaseDaoNewImpl baseDao = BaseDaoFactory.getOurInstance().getBaseDao(BaseDaoNewImpl.class,
                User.class);
        User user = new User();
        user.setName("Tim");
        User where = new User();
        where.setId("2");
        baseDao.update(user,where);
        showToast("更新成功");
    }

    /**
     * 删除
     * @param view
     */
    public void clickDelete(View view) {
        IBaseDao baseDao = BaseDaoFactory.getOurInstance().getBaseDao(BaseDaoNewImpl.class,
                User.class);
        User where = new User();
        where.setName("Vic");
        where.setId("1");
        baseDao.delete(where);
        showToast("删除成功");
    }

    /**
     * 查询
     * @param view
     */
    public void clickQuery(View view) {
        IBaseDao baseDao = BaseDaoFactory.getOurInstance().getBaseDao(BaseDaoNewImpl.class,
                User.class);
        User where = new User();
        where.setPassword("111111");
        List<User> list = baseDao.query(where);
        Log.d(TAG,"list.size =========" + list.size());
        for(int i = 0 ; i < list.size();i++) {
            Log.d(TAG,list.get(i) + " =====i=====" + i);
        }
    }

    private void showToast(String msg) {
        if(!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void clickLogin(View view) {
        User user = new User();
        user.setName("张三" + (++i));
        user.setPassword("123456");
        user.setId("N00" + i);
        baseDao.insert(user);
        showToast("执行登录成功");
    }

    public void subInsert(View view) {
        Photo photo = new Photo();
        photo.setPath("data/data/my.jpg");
        photo.setTime(new Date().toString());

        PhotoDao photoDao = BaseDaoSubFactory.getOutInstance().getSubDao(PhotoDao.class,Photo.class);
        photoDao.insert(photo);

        showToast("执行成功");

    }


    public void subQuery(View view) {

        PhotoDao photoDao = BaseDaoSubFactory.getOutInstance().getSubDao(PhotoDao.class,Photo.class);
        List<Photo> list = photoDao.query(new Photo());
        Log.d("Vic","list.size====" + list.size());
        for(int i = 0 ; i < list.size();i++) {
            Log.d("Vic","photoTime===" + list.get(i).getTime() + " =="+ list.get(i).getPath());
        }
    }
}
