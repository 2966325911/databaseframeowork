package com.cloudoc.share.yybpg.databaseframework.sub_sqlite;

import com.cloudoc.share.yybpg.databaseframework.bean.User;
import com.cloudoc.share.yybpg.databaseframework.db.BaseDaoFactory;

import java.io.File;

/**
 * @author : Vic
 *         time   : 2018/03/13
 *         desc   :  枚举单例
 */

public enum PrivateDataBaseEnums {
    database("");
    private String value;
    PrivateDataBaseEnums(String value) {

    }

    /**
     * 用于产生路径
     */
    public String getValue(){
        UserDao userDao = BaseDaoFactory.getOurInstance().getBaseDao(UserDao.class, User.class);
        if(null != userDao) {
            User currentUser = userDao.getCurrentUser();
            if(null != currentUser) {
                File file = new File("data/data/com.cloudoc.share.yybpg.databaseframework");
                if(!file.exists()){
                    file.mkdirs();
                }

                return file.getAbsolutePath() + "/" + currentUser.getId() + "_login.db";
            }
        }
        return null;
    }
}
