package com.cloudoc.share.yybpg.databaseframework.bean;

import com.cloudoc.share.yybpg.databaseframework.annotaion.DbField;
import com.cloudoc.share.yybpg.databaseframework.annotaion.DbTable;

/**
 * @author : Vic
 *         time   : 2018/03/11
 *         desc   :
 */

@DbTable("tb_user")
public class User {
    @DbField("_id")
    private String id;
    private String name;
    private String password;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User(){


    }

    public User(String id,String name,String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
