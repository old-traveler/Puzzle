package com.puzzle.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.puzzle.MainActivity;
import com.puzzle.R;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog dialog ;
    Button login;
    Button register;
    EditText nick;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(this);
        initView();
    }

    private void initView() {
        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_register);
        nick = findViewById(R.id.et_login_username);
        password = findViewById(R.id.et_login_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void login(){
        if (!TextUtils.isEmpty(nick.getText().toString())&&!TextUtils.isEmpty(password.getText().toString())){
            BmobUser user = new BmobUser();
            user.setUsername(nick.getText().toString());
            user.setPassword(password.getText().toString());
            dialog.show();
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    dialog.dismiss();
                    if (e==null){
                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, "请先输入完整", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(){
        if (!TextUtils.isEmpty(nick.getText().toString())&&!TextUtils.isEmpty(password.getText().toString())){
            BmobUser user = new BmobUser();
            user.setUsername(nick.getText().toString());
            user.setPassword(password.getText().toString());
            dialog.show();
            user.signUp(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e==null){
                        Toast.makeText(LoginActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        login();
                    }else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, "请先输入完整", Toast.LENGTH_SHORT).show();
        }
    }

}
