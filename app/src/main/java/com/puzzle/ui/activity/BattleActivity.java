package com.puzzle.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.puzzle.R;
import com.puzzle.bean.Battle;
import com.puzzle.util.CommonUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class BattleActivity extends BaseActivity {
    ProgressDialog progressDialog;
    TextView tv_user_name;
    TextView tv_battler_name;
    Button btn_battle;
    private BmobUser user;
    private long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        setToolBar(R.id.tb_battle);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("寻找对手中。。。");
        progressDialog.show();
        initView();
        btn_battle.setClickable(false);
        publishBattle(false);
    }

    private void initView() {
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_battler_name = findViewById(R.id.tv_battler_name);
        btn_battle = findViewById(R.id.btn_battle);
        btn_battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("battler",user.getObjectId());
                bundle.putString("type","battle");
                Intent intent = new Intent(BattleActivity.this,PassGameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    boolean stopFind = false;
    public void publishBattle(final boolean isReply){
        Battle battle = new Battle();
        battle.setUser(CommonUtils.getUser());
        if (isReply){
            battle.setTime(System.currentTimeMillis()+100);
        }else {
            battle.setTime(System.currentTimeMillis());
        }

        Log.e("TAG","时间"+battle.getTime());
        battle.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    if (!isReply&&!stopFind){
                        time = System.currentTimeMillis();
                        Log.e("TAG","时间"+time);
                        findBattler();
                    }
                }else {
                    if (!isReply){
                        progressDialog.dismiss();
                        Toast.makeText(BattleActivity.this, e
                                .getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopFind = true;
        super.onDestroy();
    }

    public void findBattler(){
        BmobQuery<Battle> query = new BmobQuery<>();
        query.addWhereGreaterThanOrEqualTo("time",time);
        query.setLimit(1);
        query.include("user");
        query.findObjects(new FindListener<Battle>() {
            @Override
            public void done(List<Battle> list, BmobException e) {
                if (e==null){
                    if (list.size()==1){
                        progressDialog.dismiss();
                        user = list.get(0).getUser();
                        Log.e("TAG","时间"+list.get(0).getTime());
                        publishBattle(true);
                        loadMsg();
                    }else {
                        Log.e("TAG","寻找对手");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                findBattler();
                            }
                        }, 2000);

                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(BattleActivity.this, e
                            .getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadMsg(){
        btn_battle.setClickable(true);
        tv_user_name.setText(BmobUser.getCurrentUser().getUsername());
        tv_battler_name.setText(user.getUsername());
    }
}
