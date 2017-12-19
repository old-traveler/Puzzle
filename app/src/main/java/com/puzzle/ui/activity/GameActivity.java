package com.puzzle.ui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.puzzle.R;
import com.puzzle.adapter.GameAdapter;
import com.puzzle.bean.CustomPuzzleRecord;
import com.puzzle.bean.Share;
import com.puzzle.util.CommonUtils;
import com.puzzle.view.GameDividerItemDecoration;
import com.zxy.tiny.callback.FileCallback;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class GameActivity extends BaseActivity {
    private static final int IMAGE = 1;
    private static final int  CAMERA_WITH_DATA= 12;
    public static final int MAX_TIME = 200;
    public int level=0;
    public Handler handler = new Handler();
    RecyclerView rcv_game;
    public int time = 0;
    public int step = 0;
    public int tipsSize = 3;
    TextView tv_time;
    TextView tv_step;
    GameAdapter gameAdapter;
    TextView tv_ablum;
    TextView tv_shot;
    HorizontalselectedView hd_game;
    TextView tv_left;
    TextView tv_right;
    ImageView iv_state;
    public String type;
    TextView tv_game_title;
    LinearLayout layout;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        rcv_game = findViewById(R.id.rcv_game);
        setToolBar(R.id.tb_game);
        tv_time = findViewById(R.id.tv_game_time);
        tv_step = findViewById(R.id.tv_game_step);
        tv_ablum = findViewById(R.id.tv_album_select);
        tv_shot = findViewById(R.id.tv_shot);
        iv_state = findViewById(R.id.iv_game_state);
        tv_game_title = findViewById(R.id.tv_game_title);
        layout = findViewById(R.id.ll_game);
        type = getIntent().getStringExtra("type");
        type = type == null ? "":type;
        progressDialog = new ProgressDialog(this);
        if (type.equals("guess")){
            tv_game_title.setText("盲猜模式");
        }
        iv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameAdapter==null)return;
                iv_state.setClickable(false);
                if (gameAdapter.isStop){
                    gameAdapter.isStop = false;
                    iv_state.setImageResource(R.mipmap.stop);
                    time();
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
        tv_ablum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        tv_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_WITH_DATA);
            }
        });
        hd_game = findViewById(R.id.hd_game);
        tv_left = findViewById(R.id.tv_left);
        tv_right = findViewById(R.id.tv_right);
        initSelectData();
        rcv_game.addItemDecoration(new GameDividerItemDecoration(GameActivity.this,2,R.color.white));
    }

    private void initSelectData() {
        List<String> strings = new ArrayList<>();
        strings.add("2");
        strings.add("3");
        strings.add("4");
        strings.add("5");
        strings.add("6");
        hd_game.setData(strings);
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hd_game.setAnLeftOffset();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hd_game.setAnRightOffset();
            }
        });
    }

    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.game_tip&&!gameAdapter.isTips&&!gameAdapter.isGameOver){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取被选择的图片路径
        level = Integer.parseInt(hd_game.getSelectedString());
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            GameAdapter adapter;
            if (type.equals("guess")){
                adapter =new GameAdapter(CommonUtils.getPuzzleFragment(imagePath,level),true,level);
            }else {
                adapter =new GameAdapter(CommonUtils.getPuzzleFragment(imagePath,level),level);
            }

            beginGame(adapter);
        }
        if(resultCode!=RESULT_OK)
            return;
        switch(requestCode) {
            case CAMERA_WITH_DATA:
                final Bitmap photo = data.getParcelableExtra("data");
                if (photo != null) {
                    GameAdapter adapter ;
                    if (type.equals("guess")){
                        adapter =new GameAdapter(CommonUtils.getPuzzleFragment(photo,level),true,level);
                    }else {
                        adapter =new GameAdapter(CommonUtils.getPuzzleFragment(photo,level),level);
                    }

                    beginGame(adapter);
                }
        }

    }

    public void beginGame(GameAdapter adapter){
        step = 0;
        time = 0;
        tv_time.setText("计时：0秒");
        tv_step.setText("计步：0步");
        iv_state.setImageResource(R.mipmap.begin);
        handler.removeCallbacks(timer);
        rcv_game.setLayoutManager(new GridLayoutManager(GameActivity.this,level));
        if (gameAdapter!=null){
            gameAdapter.recycle();
        }
        gameAdapter =adapter;
        gameAdapter.isStop = true;
        rcv_game.setAdapter(gameAdapter);
        gameAdapter.setListener(new GameAdapter.CountStepListener() {
            @Override
            public void onStep() {
                tv_step.setText("计步："+(++step)+"步");
            }

            @Override
            public void gameOver() {
                LitePal.getDatabase();
                CustomPuzzleRecord record = new CustomPuzzleRecord();
                record.setTime(System.currentTimeMillis());
                record.setWhenUse(time);
                record.setStep(step);
                record.setLevel(level);
                record.save();
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(GameActivity.this);
                normalDialog.setTitle("恭喜您成功拼图！");
                normalDialog.setMessage("用时："+time+"  "+"步数"+step);
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                normalDialog.setNeutralButton("分享",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String path = CommonUtils.saveBitmap(GameActivity
                                        .this,CommonUtils.loadBitmapFromViewBySystem(layout));
                                CommonUtils.compressShare(path, new FileCallback() {
                                    @Override
                                    public void callback(boolean isSuccess, String outfile, Throwable t) {
                                        if (isSuccess){
                                            upload(outfile);
                                        }else {
                                            Toast.makeText(GameActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        });
                normalDialog.setNegativeButton("记录榜",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showRecord();
                            }
                        });

                normalDialog.show();
            }
        });
    }

    public void showRecord(){
        String[] items ;
        List<CustomPuzzleRecord> data =DataSupport.findAll(CustomPuzzleRecord.class);
        Collections.sort(data, new Comparator<CustomPuzzleRecord>() {
            @Override
            public int compare(CustomPuzzleRecord customPuzzleRecord, CustomPuzzleRecord t1) {
                return (int) (t1.getTime()/t1.getLevel()-customPuzzleRecord.getTime()/customPuzzleRecord.getLevel());
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        if (data.size()>10){
            items = new String[10];
            for (int i = 0; i < 10; i++) {
                items[i] ="难度:"+data.get(i).getLevel()+ " "+data.get(i).getWhenUse()+"秒:"
                        +data.get(i).getStep()+" 步 "+format.format(new Date(data.get(i).getTime()));
            }
        }else {
            items = new String[data.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] ="难度:"+data.get(i).getLevel()+ " "+data.get(i).getWhenUse()+"秒:"
                        +data.get(i).getStep()+" 步 "+format.format(new Date(data.get(i).getTime()));
            }
        }
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(GameActivity.this);
        listDialog.setTitle("记录榜");
        listDialog.setItems(items, null);
        listDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameAdapter!=null){
            gameAdapter.recycle();
        }

    }
    TimeRunnable timer = new TimeRunnable();

    public void time(){
        handler.postDelayed(timer, 1000);
    }

    class TimeRunnable implements Runnable{

        @Override
        public void run() {
            if (time>MAX_TIME){
                gameAdapter.isGameOver = true;
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(GameActivity.this);
                normalDialog.setTitle("游戏结束");
                normalDialog.setMessage("您的时间已经超出限制，游戏失败");
                normalDialog.setPositiveButton("继续挑战",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                step = 0;
                                time = 0;
                                tv_time.setText("计时：0秒");
                                tv_step.setText("计步：0步");
                                iv_state.setImageResource(R.mipmap.begin);
                                gameAdapter.continueGame();
                                gameAdapter.isStop = true;
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                normalDialog.show();
                return;
            }
            tv_time.setText("计时："+(++time)+"秒");
            if (!gameAdapter.isGameOver&&!gameAdapter.isStop){
                time();
            }
        }
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
                                    Toast.makeText(GameActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(GameActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
