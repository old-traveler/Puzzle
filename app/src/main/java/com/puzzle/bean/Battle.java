package com.puzzle.bean;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by hyc on 2017/12/20 08:54
 */

public class Battle extends BmobObject{


    private long time;

    private BmobUser user;

    private BmobUser battler;

    private boolean isWin;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }

    public BmobUser getBattler() {
        return battler;
    }

    public void setBattler(BmobUser battler) {
        this.battler = battler;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }
}
