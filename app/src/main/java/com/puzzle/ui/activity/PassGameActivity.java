package com.puzzle.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.puzzle.R;
import com.puzzle.adapter.GameAdapter;
import com.puzzle.bean.GameTime;
import com.puzzle.bean.Share;
import com.puzzle.util.CommonUtils;
import com.puzzle.view.GameDividerItemDecoration;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

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
    String type;
    LinearLayout layout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_game);
        setToolBar(R.id.tb_game);
        handler = new Handler();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("加载中。。。");
        runnable = new CountDownRunnable();
        initView();
    }

    private void initView() {
        layout = findViewById(R.id.layout);
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
        final Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        Log.e("TAG",type);
        level = bundle.getInt("level");
        if (type.equals("pass")){
            tv_game_title.setText("第"+level+"关");
            int n = Math.min(6,level/2+2);
            int resId = bundle.getInt("resId");
            rcv_game.setLayoutManager(new GridLayoutManager(PassGameActivity.this,n));
            gameAdapter = new GameAdapter(CommonUtils.getPuzzleFragment(getResources(),resId,n),n);
            rcv_game.setAdapter(gameAdapter);
            initEvent();
        }else if (type.equals("arena")){
            tv_game_title.setText("擂台模式");
            time = bundle.getInt("time");
            tv_time.setText("剩余时间："+time+"秒");
            Glide.with(this).load(bundle.getString("imageUrl")).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    rcv_game.setLayoutManager(new GridLayoutManager(PassGameActivity.this,level));
                    gameAdapter = new GameAdapter(CommonUtils.getPuzzleFragment(resource,false,level),level);
                    rcv_game.setAdapter(gameAdapter);
                    initEvent();
                }
            });

        }else {
            tv_game_title.setText("挑战模式");
            rcv_game.setLayoutManager(new GridLayoutManager(PassGameActivity.this,2));
            gameAdapter = new GameAdapter(CommonUtils.getPuzzleFragment(getResources(),R.mipmap.level_2,2),2);
            rcv_game.setAdapter(gameAdapter);
            initEvent();
        }



    }

    public void initEvent(){
        gameAdapter.setListener(new GameAdapter.CountStepListener() {
            @Override
            public void onStep() {

            }
            @Override
            public void gameOver() {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(PassGameActivity.this);
                if (type.equals("pass")){
                    normalDialog.setTitle("恭喜您成功闯关！");
                    normalDialog.setMessage("获得"+(time*level*tipsSize)+"积分");
                }else if (type.equals("arena")){
                    normalDialog.setTitle("恭喜您夺擂成功！");
                    normalDialog.setMessage("获得"+getIntent().getExtras().getInt("integral")+"积分");
                }else {
                    queryResult();
                    iv_state.setClickable(false);
                    return;
                }
                normalDialog.setPositiveButton("确定",
                        null);
                normalDialog.setNeutralButton("分享",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String path = CommonUtils.saveBitmap(PassGameActivity
                                        .this,CommonUtils.loadBitmapFromViewBySystem(layout));
                                CommonUtils.compressShare(path, new FileCallback() {
                                    @Override
                                    public void callback(boolean isSuccess, String outfile, Throwable t) {
                                        if (isSuccess){
                                            upload(outfile);
                                        }else {
                                            Toast.makeText(PassGameActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                normalDialog.setNegativeButton("下一关",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                iv_state.setClickable(false);
                normalDialog.show();
            }
        });
    }

    private void queryResult() {
        progressDialog.show();
        final GameTime gameTime = new GameTime();
        gameTime.setUser(CommonUtils.getUser());
        gameTime.setTime(System.currentTimeMillis());
        gameTime.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    BmobQuery<GameTime> query = new BmobQuery<>();
                    BmobUser user = new BmobUser();
                    user.setObjectId(getIntent().getExtras().getString("battler"));
                    query.addWhereEqualTo("user",user);
                    query.setLimit(1);
                    query.order("-createdAt");
                    query.findObjects(new FindListener<GameTime>() {
                        @Override
                        public void done(List<GameTime> list, BmobException e) {
                            progressDialog.dismiss();
                            if (e==null){
                                boolean isWin;
                                if (list.size()==1){
                                    if (list.get(0).getTime()<gameTime.getTime()){
                                        isWin = gameTime.getTime()-list.get(0).getTime()>=100000;
                                    }else {
                                        isWin = true;
                                    }
                                }else {
                                    isWin = true;
                                }
                                final AlertDialog.Builder normalDialog =
                                        new AlertDialog.Builder(PassGameActivity.this);
                                if (isWin){
                                    normalDialog.setTitle("恭喜您击败对手！");
                                    normalDialog.setMessage("获得"+50+"积分");
                                }else {
                                    normalDialog.setTitle("您失败了！");
                                    normalDialog.setMessage("失去"+50+"积分");
                                }
                                normalDialog.setPositiveButton("确定",
                                        null);
                                normalDialog.show();
                            }else {
                                Toast.makeText(PassGameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(PassGameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showCustomizeDialog(final String path) {
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_customize,null);
        customizeDialog.setTitle("请输入分享内容");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edit_text = dialogView.findViewById(R.id.edit_text);
                        Share share = new Share();
                        share.setContent(edit_text.getText().toString());
                        share.setUser(CommonUtils.getUser());
                        share.setImageUrl(path);
                        progressDialog.show();
                        share.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                progressDialog.dismiss();
                                if (e==null){
                                    Toast.makeText(PassGameActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(PassGameActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
        customizeDialog.show();

    }

    private void upload(String path){
        progressDialog.show();
        final BmobFile file = new BmobFile(new File(path));
        file.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                progressDialog.dismiss();
                if (e==null){
                    showCustomizeDialog(file.getFileUrl());
                }else {
                    Toast.makeText(PassGameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(PassGameActivity.this);
                if (type.equals("pass")){
                    normalDialog.setTitle("闯关失败");
                    normalDialog.setMessage("您在规定时间内未能完成任务");
                }else if(type.equals("arena")){
                    normalDialog.setTitle("夺擂失败");
                    normalDialog.setMessage("您在规定时间内未能完成任务");
                }else {
                    normalDialog.setTitle("您失败了！");
                    normalDialog.setMessage("失去"+50+"积分");
                    normalDialog.setPositiveButton("确定",
                            null);
                    iv_state.setClickable(false);
                    normalDialog.show();
                    return;
                }
                normalDialog.setPositiveButton("继续挑战",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (type.equals("pass")){
                                    time=100;
                                }else if(type.equals("arena")){
                                    time = getIntent().getExtras().getInt("time");
                                }
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
                                iv_state.setClickable(false);
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
