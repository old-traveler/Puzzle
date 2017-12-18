package com.puzzle.ui.activity;


import android.app.Activity;
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
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.puzzle.R;
import com.puzzle.adapter.GameAdapter;
import com.puzzle.bean.CustomPuzzleRecord;
import com.puzzle.util.CommonUtils;
import com.puzzle.view.GameDividerItemDecoration;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GameActivity extends BaseActivity {
    private static final int IMAGE = 1;
    private static final int  CAMERA_WITH_DATA= 12;
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
            GameAdapter adapter =new GameAdapter(CommonUtils.getPuzzleFragment(imagePath,level),level);
            beginGame(adapter);
        }
        if(resultCode!=RESULT_OK)
            return;
        switch(requestCode) {
            case CAMERA_WITH_DATA:
                final Bitmap photo = data.getParcelableExtra("data");
                if (photo != null) {
                    GameAdapter adapter =new GameAdapter(CommonUtils.getPuzzleFragment(photo,level),level);
                    beginGame(adapter);
                }
        }

    }

    public void beginGame(GameAdapter adapter){
        step = 0;
        time = 0;
        handler.removeCallbacks(timer);
        rcv_game.setLayoutManager(new GridLayoutManager(GameActivity.this,level));
        if (gameAdapter!=null){
            gameAdapter.recycle();
        }
        gameAdapter =adapter;
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
        time();
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
        gameAdapter.recycle();
    }
    TimeRunnable timer = new TimeRunnable();

    public void time(){
        handler.postDelayed(timer, 1000);
    }

    class TimeRunnable implements Runnable{

        @Override
        public void run() {
            tv_time.setText("计时："+(++time)+"秒");
            if (!gameAdapter.isGameOver){
                time();
            }
        }
    }
}
