package com.cloudoc.share.yybpg.databaseframework.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.cloudoc.share.yybpg.databaseframework.annotaion.DbField;
import com.cloudoc.share.yybpg.databaseframework.annotaion.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : Vic
 *         time   : 2018/03/11
 *         desc   :
 */

public class BaseDao<T> implements IBaseDao<T> {
    /**
     * 持有数据库的操作的引用
     */
    private SQLiteDatabase sqLiteDatabase;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 持有操作数据库所对应的java类型
     */
    private Class<T> entityClass;
    /**
     * 标志:用来表示是否做过数据库初始化操作
     */
    private boolean isInit = false;
    /**
     * 定义一个缓存空间(key--字段名  value--成员变量)
     */
    private HashMap<String,Field> cacheMap;

    /**
     * 框架内部的逻辑，最好不要提供给构造方法给调用层调用
     * @param sqLiteDatabase
     * @param entityClass
     * @return
     */
    public boolean init(SQLiteDatabase sqLiteDatabase,Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        if(!isInit) {
            //自动建表
            //取道表名
            if(entityClass.getAnnotation(DbTable.class) == null) {
                //反射取道表名称
                tableName = entityClass.getSimpleName();
            } else {
                tableName = entityClass.getAnnotation(DbTable.class).value();
            }

            if(!sqLiteDatabase.isOpen()) {
                return false;
            }

            String createTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(createTableSql);
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }
        return isInit;
    }

    private void initCacheMap() {
        // 1 取到所有的列名 //空表
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        String[] columnNames = cursor.getColumnNames();
        //2 获取所有的成员变量
        Field[] columnFields = entityClass.getDeclaredFields();
        //把所有字段的访问权限打开
        for(Field field : columnFields) {
            field.setAccessible(true);
        }

        // 对1 和2进行映射
        for(String columnName : columnNames) {
            Field columnField = null;
            for(Field field : columnFields) {
                String fieldName = null;
                if(field.getAnnotation(DbField.class) != null) {
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }

                if(columnName.equals(fieldName)){
                    columnField = field;
                    break;
                }
            }
            if(columnField != null){
                cacheMap.put(columnName,columnField);
            }
        }
    }


    /**
     * 创建sql的语句
     * @return
     */
    private String getCreateTableSql() {
        //crate table if not exists tb_user(_id INTEGER,name TEXT,password TEXT)
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName + "(");
        //反射得到所有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for(Field field : fields) {
            //拿到成员的类型
            Class type = field.getType();
            if(field.getAnnotation(DbField.class) != null) {
                if(type == String.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if(type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if(type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                }else if(type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                }else if(type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                }else {
                    //不支持的类型
                    continue;
                }
            } else {
                if(type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if(type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if(type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                }else if(type == Double.class) {
                    stringBuffer.append(field.getName() + " DOUBLE,");
                }else if(type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOB,");
                }else {
                    //不支持的类型
                    continue;
                }
            }

        }
        if(stringBuffer.charAt(stringBuffer.length()-1)== ',') {
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }


    @Override
    public long insert(T entity) {

        Map<String,Object> map = getValues(entity);
        ContentValues values = getContentValues(map);
        long result = sqLiteDatabase.insert(tableName,null,values);
        return result;
    }

    @Override
    public long update(T entity, T where) {
        int result = -1;
        Map values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        Map whereCause = getValues(where);
        Condition condition = new Condition(whereCause);
        result = sqLiteDatabase.update(tableName,contentValues,condition.whereCasue,condition.whereArgs);
        return result;
    }

    @Override
    public int delete(T where) {
        Map map = getValues(where);
        Condition condition = new Condition(map);
        int result = sqLiteDatabase.delete(tableName,condition.whereCasue,condition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map map = getValues(where);
        String limitString = null;
        if(startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName,null,condition.whereCasue,condition.whereArgs,null,
                null,orderBy,limitString);
        List<T> result = getResult(cursor,where);
        return result;
    }


    /**
     *  提供字段whereCause和whereArgs,避免手动去拼
     */
    private class Condition{
        //"name=? and password = ?
        private String whereCasue;
        //new string[]{"vic"}
        private String[] whereArgs;

        public Condition(Map<String,String> whereCasue) {
            ArrayList list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            //用于占位
            stringBuilder.append("1=1");
            Set keys = whereCasue.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                String value = whereCasue.get(key);
                if(value != null) {
                    stringBuilder.append(" and " + key + "=?");
                    list.add(value);
                }
            }
            this.whereCasue = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }


    /**
     * 用sql获取数据  1：通过列名得到columnIndex
     *               2：根据field不同的类型去get
     *               eg:String类型 cursor.getString(cursor.getColumnIndex("列名"))；
     * 拿到列名后，通过列名拿到columnIndex, 然后获取的Field 的类型，最后根据类型去调用cursor不同的get方法
     * @param cursor
     * @param obj
     * @return
     */
    private List<T> getResult(Cursor cursor,T obj){
        ArrayList list = new ArrayList();
        Object item = null;
        while (cursor.moveToNext()) {
            try {
                item =obj.getClass().newInstance();
                //字段成员变量
                Iterator iterator = cacheMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    //取到列名
                    String columnName = (String) entry.getKey();
                    //用列名得到列名在游标中位置
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    //得到Field
                    Field field = (Field) entry.getValue();
                    //得到Field的类型
                    Class type = field.getType();

                    //根据不同的类型去设置的值
                    if(columnIndex != -1) {
                        if(type == String.class) {
                            field.set(item,cursor.getString(columnIndex));
                        } else if(type == Double.class) {
                            field.set(item,cursor.getDouble(columnIndex));
                        }else if(type == Integer.class) {
                            field.set(item,cursor.getInt(columnIndex));
                        } else if(type == Long.class) {
                            field.set(item,cursor.getLong(columnIndex));
                        } else if(type == byte[].class) {
                            field.set(item,cursor.getBlob(columnIndex));
                        } else{
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    /**
     *  得到ContentValues  map中存储的是string类型的，key为String，value可以为其他Object
     *   这里用String
     * @param map
     * @return
     */
    private ContentValues getContentValues(Map<String,Object> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator iterator  = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
//            String value = map.get(key);
//            if(value != null) {
//                contentValues.put(key,value);
//            }
//            正确的使用方式如下，为了测试方便直接写成String
            Object value = map.get(key);
            Class<?> type = value.getClass();
            if(type == String.class) {
                contentValues.put(key, (String) value);
            } else if(type == Double.class) {
                contentValues.put(key, (Double) value);
            }else if(type == Integer.class) {
                contentValues.put(key, (Integer) value);
            } else if(type == Long.class) {
                contentValues.put(key, (Long) value);
            } else if(type == byte[].class) {
                contentValues.put(key, (Byte) value);
            } else{
                continue;
            }
        }
        return contentValues;
    }


    /**
     * 根据具体的类，得到map
     * @param entity
     * @return
     */
    private Map<String,Object> getValues(T entity) {
        HashMap<String,Object> map = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            //获取成员变量的值
            try {
                Object object = field.get(entity);
                if(object == null){
                    continue;
                }
                String value = object.toString();
                String key = null;
                if(field.getAnnotation(DbField.class) != null) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }
                if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key,value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
