package com.puzzle.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by hyc on 2017/12/18 09:38
 */

public class Player extends BmobUser {

    private String headUrl;

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
}
