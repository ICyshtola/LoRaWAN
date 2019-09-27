package com.example.icarus.lorawan.CoverPassword;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icarus.lorawan.Login.LoginActivity;
import com.example.icarus.lorawan.R;

import java.io.*;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangepasswordActivity extends AppCompatActivity {
    private Button con;
    private EditText edit1;
    private EditText edit2;
    private EditText edit3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        edit1 = (EditText)findViewById(R.id.editText);
        edit2 = (EditText)findViewById(R.id.editText2);
        edit3 = (EditText)findViewById(R.id.editText3);


        con = (Button)findViewById(R.id.confirm);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String str1 = edit1.getText().toString();
                final String str2 = edit2.getText().toString();
                final String str3 = edit3.getText().toString();
                edit1.setError(null);
                edit2.setError(null);
                edit2.setError(null);
                if(!str2.equals(str3)){
                    edit3.setError("密码不一致");
                }
                else if(str2.length() < 6 || str2.length() >15){
                    edit2.setError("密码长度不正确");
                }
                else if(str2.length() == 0 || str2.length() == 0 || str1.length() == 0){
                    edit1.setError("密码不能为空");
                }
                else{
                    ChangepasswordActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ChangepasswordActivity.this)
                                    .setTitle("系统提示")
                                    .setMessage("是否确认修改？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Looper.prepare();
                                                    OkHttpClient client = new OkHttpClient();

                                                    RequestBody requestBody = new FormBody.Builder()
                                                            .add("name", LoginActivity.User_Id)
                                                            .add("newpassword", str2)
                                                            .build();

                                                    Request request = new Request.Builder()
                                                            .url(LoginActivity.IP+"/user/chgpswd")
                                                            .post(requestBody)
                                                            .build();
                                                    Response response = null;
                                                    try {
                                                        response = client.newCall(request).execute();
                                                        String responseData = response.body().string();
                                                        if (responseData.equals("success")) {
                                                            Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ChangepasswordActivity.this,LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else {
                                                            Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Looper.loop();
                                                }
                                            }).start();

                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });
    }
}
