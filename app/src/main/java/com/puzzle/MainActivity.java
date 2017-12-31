package com.puzzle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.allenliu.circlemenuview.CircleMenuView;

import com.puzzle.ui.activity.ArenaActivity;
import com.puzzle.ui.activity.BaseActivity;
import com.puzzle.ui.activity.BattleActivity;
import com.puzzle.ui.activity.GameActivity;
import com.puzzle.ui.activity.LoginActivity;
import com.puzzle.ui.activity.PassActivity;
import com.puzzle.ui.activity.ShareActivity;
import com.puzzle.util.BackgroundMusic;
import com.puzzle.util.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity {
    DrawerLayout drawerLayout;
    CircleMenuView circleMenuLayout;
    TextView tv_name;
    TextView tv_switch_account;
    ImageView iv_exit;
    boolean isLogin =false;
    boolean isplay = true;
    TextView tv_point;
    TextView tv_bgm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar(R.id.tb_main);
        initToolBarLeft(R.mipmap.more);
        initView();
        tv_point = findViewById(R.id.tv_point);
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_point.setText("您的积分："+(CommonUtils.getCurrentIntegral()+100));
    }

    private void initView() {
        tv_bgm = findViewById(R.id.tv_bgm);
        drawerLayout = findViewById(R.id.dl_main);
        circleMenuLayout =findViewById(R.id.view);
        iv_exit = findViewById(R.id.iv_exit);
        iv_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        tv_switch_account = findViewById(R.id.tv_switch_account);
        tv_switch_account.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        circleMenuLayout.setOnClickListener(new CircleMenuView.onYuanPanClickListener() {
            @Override
            public void onClick(View v, int position) {
                switch (position){
                    case 1:
                        if (!isLogin){
                            tips();
                            return;
                        }
                        startActivity(new Intent(MainActivity.this, PassActivity.class));break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, ShareActivity.class));break;
                    case 3:
                        if (!isLogin){
                            tips();
                            return;
                        }
                        startActivity(new Intent(MainActivity.this, BattleActivity.class));break;
                    case 4:
                        if (!isLogin){
                            tips();
                            return;
                        }
                        startActivity(new Intent(MainActivity.this, ArenaActivity.class));break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, GameActivity.class));break;
                    case 6:
                        if (!isLogin){
                            tips();
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this,GameActivity.class);
                        intent.putExtra("type","guess");
                        startActivity(intent);
                    break;
                }
            }
        });
        tv_name = findViewById(R.id.tv_name);
        if (CommonUtils.hasLogined()){
            isLogin = true;
            tv_name.setText(BmobUser.getCurrentUser().getUsername());
        }


        BackgroundMusic.getInstance(this).setBackgroundVolume(50);
        BackgroundMusic.getInstance(this).playBackgroundMusic("bgm.mp3",true);



        tv_bgm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isplay){
                    tv_bgm.setText("背景音乐：开");
                    BackgroundMusic.getInstance(MainActivity.this).pauseBackgroundMusic();
                }else {
                    BackgroundMusic.getInstance(MainActivity.this).resumeBackgroundMusic();
                    tv_bgm.setText("背景音乐：关");
                }
                isplay = !isplay;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BackgroundMusic.getInstance(this).end();
    }

    public void tips(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("未登录");
        normalDialog.setMessage("请先登录");
        normalDialog.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });

        normalDialog.show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initEvent() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }


}
