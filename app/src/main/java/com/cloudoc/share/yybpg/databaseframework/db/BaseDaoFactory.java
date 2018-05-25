package com.cloudoc.share.yybpg.databaseframework.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Vic
 *         time   : 2018/03/11
 *         desc   :
 */

public class BaseDaoFactory {
    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getOurInstance() {
        return ourInstance;
    }

    private SQLiteDatabase sqLiteDatabase;
    /**
     * 数据库的存储path最好存在SD卡中，好处，APP删除了，下次安装的时候，数据还在
     */
    private String sqliteDatabasePath;

    /**
     * 定义一个数据库连接池
     */
    protected Map<String,BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    protected BaseDaoFactory() {
        //这里注意权限问题，看读写权限是否开启
        sqliteDatabasePath = "data/data/com.cloudoc.share.yybpg.databaseframework/Vic.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }

    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if(map.get(daoClass.getSimpleName()) != null) {
            return (T) map.get(daoClass.getSimpleName());
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            map.put(daoClass.getSimpleName(),baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;

    }
}
