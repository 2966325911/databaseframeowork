package com.cloudoc.share.yybpg.databaseframework.db;

import java.util.List;

/**
 * @author : Vic
 *         time   : 2018/03/11
 *         desc   :规范所有的数据库操作
 */

public interface IBaseDao<T> {

    long insert(T entity);

    long update(T entity,T where);

    int delete(T where);

    List<T> query(T where);
    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
}
