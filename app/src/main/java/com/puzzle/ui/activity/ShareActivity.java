package com.puzzle.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.puzzle.R;
import com.puzzle.adapter.ShareAdapter;
import com.puzzle.bean.Share;
import com.puzzle.view.GameDividerItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ShareActivity extends BaseActivity {
    SmartRefreshLayout srl_share;
    RecyclerView rcv_share;
    ShareAdapter adapter;
    int page=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setToolBar(R.id.tb_share);
        initView();
    }

    private void initView() {
        srl_share = findViewById(R.id.srl_share);
        rcv_share = findViewById(R.id.rcv_share);
        adapter = new ShareAdapter();
        rcv_share.setLayoutManager(new LinearLayoutManager(this));
        rcv_share.addItemDecoration(new GameDividerItemDecoration(this,3,R.color.white));
        rcv_share.setAdapter(adapter);
        srl_share.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                queryShare();
            }
        });
        srl_share.autoRefresh();
    }

    private void queryShare(){
        BmobQuery<Share> query = new BmobQuery<>();
        query.setLimit(20);
        query.setSkip(page*20);
        page++;
        query.include("user");
        query.findObjects(new FindListener<Share>() {
            @Override
            public void done(List<Share> list, BmobException e) {
                srl_share.finishRefresh();
                srl_share.finishLoadmore();
                if (e==null){
                    adapter.setList(list);
                }else {
                    Toast.makeText(ShareActivity.this, e
                            .getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
