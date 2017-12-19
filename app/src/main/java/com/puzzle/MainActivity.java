package com.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.allenliu.circlemenuview.CircleMenuView;
import com.puzzle.bean.Arena;
import com.puzzle.bean.Share;
import com.puzzle.ui.activity.ArenaActivity;
import com.puzzle.ui.activity.BaseActivity;
import com.puzzle.ui.activity.GameActivity;
import com.puzzle.ui.activity.PassActivity;
import com.puzzle.ui.activity.ShareActivity;

public class MainActivity extends BaseActivity {
    DrawerLayout drawerLayout;
    CircleMenuView circleMenuLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar(R.id.tb_main);
        initToolBarLeft(R.mipmap.more);
        initView();
        initEvent();
    }

    private void initView() {
        drawerLayout = findViewById(R.id.dl_main);
        circleMenuLayout =findViewById(R.id.view);
        circleMenuLayout.setOnClickListener(new CircleMenuView.onYuanPanClickListener() {
            @Override
            public void onClick(View v, int position) {
                switch (position){
                    case 1: startActivity(new Intent(MainActivity.this, PassActivity.class));break;
                    case 2: startActivity(new Intent(MainActivity.this, ShareActivity.class));break;
                    case 3: break;
                    case 4: startActivity(new Intent(MainActivity.this, ArenaActivity.class));break;
                    case 5: startActivity(new Intent(MainActivity.this, GameActivity.class));break;
                    case 6:
                        Intent intent = new Intent(MainActivity.this,GameActivity.class);
                        intent.putExtra("type","guess");
                        startActivity(intent);
                    break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }else  if (item.getItemId() == R.id.rank){
            Toast.makeText(this, "排行榜", Toast.LENGTH_SHORT).show();
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
