package com.example.icarus.lorawan.ChangeUserInf;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.icarus.lorawan.Login.LoginActivity;
import com.example.icarus.lorawan.R;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chgphone extends AppCompatActivity {

    private Button button;
    private TextView textView;
    private static final String TAG = "Chgphone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chgphone);
        button = (Button)findViewById(R.id.send_phone_change);
        textView = (TextView) findViewById(R.id.change_phone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mysend();
            }
        });
    }

    public void mysend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String newphone = textView.getText().toString();
                OkHttpClient client = new OkHttpClient();
                final RequestBody requestBody = new FormBody.Builder().add("name", LoginActivity.User_Id).add("newphone",newphone).build();
                Request request = new Request.Builder().url(LoginActivity.IP + "/user/chgphone").post(requestBody).build();
                Call call = client.newCall(request);
                Response response = null;
                try{
                    response = call.execute();
                    String responseData = response.body().string();
                    /****t添加返回值判断*****/
                    Log.d(TAG, responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }
}
