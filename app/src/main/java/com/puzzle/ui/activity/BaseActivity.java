package com.puzzle.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by hyc on 2017/12/17 16:44
 */

public class BaseActivity extends AppCompatActivity {

    public Toolbar mToolbar;

    public void setToolBar(int toolId){
        Toolbar toolbar = findViewById(toolId);
        if (toolbar != null){
            setSupportActionBar(toolbar);
            this.mToolbar = toolbar;
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    public void setTitle(int titleId,String title){
        if (mToolbar!=null){
            TextView titleView = mToolbar.findViewById(titleId);
            titleView.setText(title);
        }
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void initToolBarLeft(int res) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(res);
        }
    }

}
