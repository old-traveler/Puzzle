package com.puzzle.ui;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

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
    }

    public static Context getContext(){
        return mContext;
    }
}
