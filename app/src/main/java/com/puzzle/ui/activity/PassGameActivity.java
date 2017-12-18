package com.puzzle.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.puzzle.R;
import com.puzzle.adapter.GameAdapter;
import com.puzzle.util.CommonUtils;
import com.puzzle.view.GameDividerItemDecoration;

public class PassGameActivity extends BaseActivity {
    public int level;
    public int time=100;
    RecyclerView rcv_game;
    TextView tv_time;
    ImageView iv_state;
    public Handler handler;
    GameAdapter gameAdapter;
    CountDownRunnable runnable;
    TextView tv_game_title;
    public int tipsSize = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_game);
        setToolBar(R.id.tb_game);
        handler = new Handler();
        runnable = new CountDownRunnable();
        initView();
    }

    private void initView() {
        rcv_game = findViewById(R.id.rcv_game);
        rcv_game.addItemDecoration(new GameDividerItemDecoration(
                PassGameActivity.this,2,R.color.white));
        tv_time = findViewById(R.id.tv_game_time);
        iv_state = findViewById(R.id.iv_game_state);
        tv_game_title = findViewById(R.id.tv_game_title);
        iv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_state.setClickable(false);
                if (gameAdapter.isStop){
                    gameAdapter.isStop = false;
                    iv_state.setImageResource(R.mipmap.stop);
                    countDown();
                }else {
                    gameAdapter.isStop = true;
                    iv_state.setImageResource(R.mipmap.begin);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_state.setClickable(true);
                    }
                }, 1010);
            }
        });
        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("type");
        level = bundle.getInt("level");
        tv_game_title.setText("第"+level+"关");
        int n = Math.min(6,level/2+2);
        int resId = bundle.getInt("resId");
        rcv_game.setLayoutManager(new GridLayoutManager(PassGameActivity.this,n));
        gameAdapter = new GameAdapter(CommonUtils.getPuzzleFragment(getResources(),resId,n),n);
        rcv_game.setAdapter(gameAdapter);
        gameAdapter.setListener(new GameAdapter.CountStepListener() {
            @Override
            public void onStep() {

            }

            @Override
            public void gameOver() {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(PassGameActivity.this);
                normalDialog.setTitle("恭喜您成功闯关！");
                normalDialog.setMessage("获得"+(time*level*tipsSize)+"积分");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                normalDialog.setNegativeButton("下一关",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                normalDialog.show();
            }
        });
        if (type.equals("pass")){

        }
    }

    public void countDown(){
        handler.postDelayed(runnable,1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.game_tip&&!gameAdapter.isTips&&!gameAdapter.isGameOver&&!gameAdapter.isStop){
            if (tipsSize>0){
                gameAdapter.tips();
                showToast("还有"+(--tipsSize)+"次提示");
            }else {
                showToast("提示机会已用完!");
            }
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    class CountDownRunnable implements Runnable{

        @Override
        public void run() {
            if (!gameAdapter.isGameOver&&!gameAdapter.isStop&&time>1){
                time--;
                tv_time.setText("剩余时间："+time+"秒");
                countDown();
            }else if (time==1){
                tv_time.setText("剩余时间："+0+"秒");
                gameAdapter.isGameOver =true;
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(PassGameActivity.this);
                normalDialog.setTitle("闯关失败");
                normalDialog.setMessage("您在规定时间内未能完成任务");
                normalDialog.setPositiveButton("继续挑战",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                time=100;
                                tv_time.setText("剩余时间："+time+"秒");
                                gameAdapter.continueGame();
                                gameAdapter.isStop = true;
                                iv_state.setImageResource(R.mipmap.begin);
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                normalDialog.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameAdapter.recycle();
    }
}
