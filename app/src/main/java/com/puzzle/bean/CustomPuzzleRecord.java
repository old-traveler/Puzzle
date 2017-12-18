package com.puzzle.bean;

import android.graphics.Bitmap;

import org.litepal.crud.DataSupport;

/**
 * Created by hyc on 2017/12/17 17:31
 */

public class CustomPuzzleRecord extends DataSupport{

    private int whenUse;

    private int step;

    private long time;

    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWhenUse() {
        return whenUse;
    }

    public void setWhenUse(int whenUse) {
        this.whenUse = whenUse;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
