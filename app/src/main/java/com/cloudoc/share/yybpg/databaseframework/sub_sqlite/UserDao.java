package com.cloudoc.share.yybpg.databaseframework.sub_sqlite;

import android.util.Log;

import com.cloudoc.share.yybpg.databaseframework.bean.User;
import com.cloudoc.share.yybpg.databaseframework.db.BaseDao;

import java.util.List;

/**
 * @author : Vic
 *         time   : 2018/03/13
 *         desc   :用于维护共有数据
 */

public class UserDao extends BaseDao<User> {

    @Override
    public long insert(User entity) {
        //查询表中的所有的用户记录
        List<User> list = query(new User());
        User where = null;
        for(User user : list) {
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            Log.d("Vic","用户" + user.getName() + "更改为未登录状态");
            update(entity,where);
        }
        Log.i("Vic","用户" + entity.getName() + "登录");
        entity.setStatus(1);
        return super.insert(entity);
    }

    /**
     * 得到当前登录的用户
     * @return
     */
    public User getCurrentUser(){
        User user = new User();
        user.setStatus(1);
        List<User> list = query(user);
        if(null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}

