package com.puzzle.ui;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

import cn.bmob.v3.Bmob;

/**
 * Created by hyc on 2017/12/17 16:24
 */

public class PuzzleApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        LitePal.initialize(this);
        Bmob.initialize(this,"ef1af0ef3bec47fee2921cb718f468c1");
    }

    public static Context getContext(){
        return mContext;
    }
}
