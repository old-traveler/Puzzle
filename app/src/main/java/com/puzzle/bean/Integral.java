package com.puzzle.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by hyc on 2017/12/20 09:01
 */

public class Integral {

    private BmobUser user;

    private int integralChange;

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }

    public int getIntegralChange() {
        return integralChange;
    }

    public void setIntegralChange(int integralChange) {
        this.integralChange = integralChange;
    }
}
