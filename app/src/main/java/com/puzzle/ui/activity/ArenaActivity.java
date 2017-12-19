package com.puzzle.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.puzzle.R;
import com.puzzle.adapter.ArenaAdapter;
import com.puzzle.bean.Arena;
import com.puzzle.view.GameDividerItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ArenaActivity extends BaseActivity {
    SmartRefreshLayout layout;
    RecyclerView rcv_arena;
    ArenaAdapter arenaAdapter;
    int page = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);
        setToolBar(R.id.tb_arena);
        arenaAdapter = new ArenaAdapter(null,new WeakReference<Activity>(this));
        initView();
    }

    private void initView() {
        layout = findViewById(R.id.sl_arena);
        rcv_arena = findViewById(R.id.rcv_arena);
        rcv_arena.setLayoutManager(new GridLayoutManager(this,2));
        rcv_arena.addItemDecoration(new GameDividerItemDecoration(this,3,R.color.background));
        layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page=0;
                layout.setEnableLoadmore(true);
                queryArena();
            }
        });
        layout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                queryArena();
            }
        });
        rcv_arena.setAdapter(arenaAdapter);
        layout.autoRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_arena,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.add){
            startActivity(new Intent(this,NewArenaActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void queryArena(){
        BmobQuery<Arena> arenaBmobQuery = new BmobQuery<>();
        arenaBmobQuery.addWhereEqualTo("state",0);
        arenaBmobQuery.order("-createdAt");
        arenaBmobQuery.include("publisher");
        arenaBmobQuery.setLimit(20);
        arenaBmobQuery.setSkip(page*20);
        arenaBmobQuery.findObjects(new FindListener<Arena>() {
            @Override
            public void done(List<Arena> list, BmobException e) {
                layout.finishRefresh();
                layout.finishLoadmore();
                if (e==null){
                    Log.e("TAG",""+list.size());
                    if (list.size()==0){
                        layout.setEnableLoadmore(false);
                    }else {
                        arenaAdapter.setList(list);
                        page++;
                    }
                }else {
                    showToast(e.getMessage());
                }
            }
        });
    }
}
