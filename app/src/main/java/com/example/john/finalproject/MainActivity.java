package com.example.john.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.john.finalproject.Sport.SportActivity;

/**
 * Created by xia on 2016/11/27.
 */
public class MainActivity extends AppCompatActivity {
    private EditText username_input;
    private EditText password_input;
    private Button register_button;
    private Button login_button;
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    userDB db = new userDB(MainActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        checkPermission();
        findView();
        checkState();

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(username_input.getText().toString())) {
                    Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password_input.getText().toString())) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!db.queryName(username_input.getText().toString())) {
                        Toast.makeText(MainActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putString("username", username_input.getText().toString());
                        editor.putString("state", "login");
                        editor.commit();
                        db.insertData(username_input.getText().toString(), password_input.getText().toString());
                        // 进入应用
                        Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, SportActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(username_input.getText().toString())) {
                    Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password_input.getText().toString())) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (db.confirmUser(username_input.getText().toString(), password_input.getText().toString())) {
                        editor.putString("username", username_input.getText().toString());
                        editor.putString("state", "login");
                        editor.commit();
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, SportActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private static String [] PERMISSIONS_Array = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_Array,1);
        }
    }

    private void findView() {
        username_input = (EditText) findViewById(R.id.username_input);
        password_input = (EditText) findViewById(R.id.password_input);
        register_button = (Button) findViewById(R.id.register_button);
        login_button = (Button) findViewById(R.id.login_button);
    }

    private void checkState() {
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getString("username", null) != null
                && TextUtils.equals("login", sharedPreferences.getString("state", null))) {
            // 直接进入应用
            Intent intent = new Intent(MainActivity.this, SportActivity.class);
            startActivity(intent);
        }
        else if (TextUtils.equals("logout", sharedPreferences.getString("state", null))) {
            login_button.setVisibility(View.VISIBLE);
            register_button.setText("注册新账号");
        } else {
            login_button.setVisibility(View.GONE);
            register_button.setText("注册");
        }
    }
}
