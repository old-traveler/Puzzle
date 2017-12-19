package com.puzzle.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by hyc on 2017/12/19 14:31
 */

public class Arena extends BmobObject{
    // 0 待挑战 1正在挑战 2挑战成功
    private long publish_time;
    private String imageUrl;
    private int time;
    private int level;
    private int integral;
    private BmobUser publisher;
    private int state;

    public long getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(long publish_time) {
        this.publish_time = publish_time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public BmobUser getPublisher() {
        return publisher;
    }

    public void setPublisher(BmobUser publisher) {
        this.publisher = publisher;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
