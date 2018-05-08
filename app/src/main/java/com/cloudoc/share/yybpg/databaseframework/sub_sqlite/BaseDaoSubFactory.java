package com.cloudoc.share.yybpg.databaseframework.sub_sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cloudoc.share.yybpg.databaseframework.db.BaseDao;
import com.cloudoc.share.yybpg.databaseframework.db.BaseDaoFactory;

/**
 * @author : Vic
 *         time   : 2018/03/13
 *         desc   :
 */

public class BaseDaoSubFactory extends BaseDaoFactory {
    private static final BaseDaoSubFactory ourInstance = new BaseDaoSubFactory();
    public static BaseDaoSubFactory getOutInstance(){
        return ourInstance;
    }

    /**
     * 定义一个用户实现分库的数据库操作对象
     */
    protected SQLiteDatabase subSqLiteDatabase;
    protected BaseDaoSubFactory() {
    }

    public <T extends BaseDao<M>,M> T getSubDao(Class<T> daoClass,Class<M> entityClass) {
        BaseDao baseDao = null;
        if(map.get(PrivateDataBaseEnums.database.getValue()) != null) {
            return (T) map.get(PrivateDataBaseEnums.database.getValue());
        }
        Log.d("Vic","生成数据库文件的位置：" + PrivateDataBaseEnums.database.getValue());
        subSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDataBaseEnums.database.getValue(),null);

        try {
            baseDao = daoClass.newInstance();
            baseDao.init(subSqLiteDatabase,entityClass);
            map.put(PrivateDataBaseEnums.database.getValue(),baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
