package com.cloudoc.share.yybpg.databaseframework.bean;

import com.cloudoc.share.yybpg.databaseframework.annotaion.DbTable;

/**
 * @author : Vic
 *         time   : 2018/03/13
 *         desc   :
 */

@DbTable("tb_photo")
public class Photo {
    private String time;
    private String path;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
