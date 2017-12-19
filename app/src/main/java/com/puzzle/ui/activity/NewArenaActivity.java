package com.puzzle.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.puzzle.R;
import com.puzzle.bean.Arena;
import com.puzzle.util.CommonUtils;
import com.zxy.tiny.callback.FileCallback;
import java.io.File;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


public class NewArenaActivity extends BaseActivity {
    private static final int IMAGE = 1;
    EditText et_arena_time;
    EditText et_arena_integral;
    EditText et_arena_level;
    TextView tv_tips;
    ImageView iv_arena_image;
    Button btn_publish;
    public String imageUrl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_arena);
        setToolBar(R.id.tb_new_arena);
        progressDialog = new ProgressDialog(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imageUrl = c.getString(columnIndex);
            iv_arena_image.setImageURI(Uri.fromFile(new File(imageUrl)));
        }

    }


    private void initView() {
        et_arena_time = findViewById(R.id.et_arena_time);
        et_arena_integral = findViewById(R.id.et_arena_integral);
        et_arena_level = findViewById(R.id.et_arena_level);
        tv_tips = findViewById(R.id.tv_arena_tips);
        iv_arena_image = findViewById(R.id.iv_arena_image);
        btn_publish = findViewById(R.id.btn_publish);
        iv_arena_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                tv_tips.setVisibility(View.GONE);
            }
        });
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtils.hasEmpty(et_arena_integral,et_arena_level,et_arena_time)){
                    if (Integer.parseInt(et_arena_level.getText().toString())>=2&&Integer
                            .parseInt(et_arena_level.getText().toString())<=6){
                        if (TextUtils.isEmpty(imageUrl)){
                            Toast.makeText(NewArenaActivity.this, "请选择一张照片", Toast.LENGTH_SHORT).show();
                        }else {
                            publish();
                        }
                    }else {
                        Toast.makeText(NewArenaActivity.this, "难度应在2-6之间", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(NewArenaActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void publish() {
        progressDialog.show();
        CommonUtils.compress(imageUrl, new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if (isSuccess){
                    final BmobFile file = new BmobFile(new File(outfile));
                    file.upload(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                upload(file.getFileUrl());
                            }else {
                                progressDialog.dismiss();
                                showToast(e.getMessage());
                            }
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(NewArenaActivity.this, "图片压缩失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void upload(String path){
        Arena arena =new Arena();
        arena.setImageUrl(path);
        arena.setIntegral(Integer.parseInt(et_arena_integral.getText().toString()));
        arena.setLevel(Integer.parseInt(et_arena_level.getText().toString()));
        arena.setTime(Integer.parseInt(et_arena_time.getText().toString()));
        arena.setPublish_time(System.currentTimeMillis());
        arena.setState(0);
        arena.setPublisher(CommonUtils.getUser());
        arena.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                progressDialog.dismiss();
                if (e==null){
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(NewArenaActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("发布成功");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    normalDialog.show();
                    btn_publish.setClickable(false);
                    iv_arena_image.setClickable(false);
                }else {
                    showToast(e.getMessage());
                }
            }
        });
    }

    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE);
    }

}
