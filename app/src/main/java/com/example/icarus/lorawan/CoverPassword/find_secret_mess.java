package com.example.icarus.lorawan.CoverPassword;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icarus.lorawan.Login.LoginActivity;
import com.example.icarus.lorawan.R;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class find_secret_mess extends AppCompatActivity {
    private Button con;
    private EditText edit1;
    private EditText edit2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_secret_mess);
        edit1 = (EditText)findViewById(R.id.editText);
        edit2 = (EditText)findViewById(R.id.editText2);

        con = (Button)findViewById(R.id.confirm);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String str1 = edit1.getText().toString();
                final String str2 = edit2.getText().toString();

                edit1.setError(null);
                edit2.setError(null);
                ;
                if(str2.length() == 0 || str1.length() == 0){
                    edit1.setError("不能为空");
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            OkHttpClient client = new OkHttpClient();

                            RequestBody requestBody = new FormBody.Builder()
                                    .add("name", str1)
                                    .add("phone", str2)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(LoginActivity.IP + "/user/chgbyphone")
                                    .post(requestBody)
                                    .build();

                            Response response = null;
                            try {
                                response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                if (responseData.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "短信已发送", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(find_secret_mess.this, new_secret_mess.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }).start();



                }
            }



        });

    }
}
