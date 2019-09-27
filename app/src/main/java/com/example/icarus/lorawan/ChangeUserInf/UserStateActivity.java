package com.example.icarus.lorawan.ChangeUserInf;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.icarus.lorawan.Login.LoginActivity;
import com.example.icarus.lorawan.R;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserStateActivity extends AppCompatActivity {

    private TextView state_username;
    private TextView state_email;
    private TextView state_phone;
    private TextView state_permission;
    private Switch state_email_warning;
    private Switch state_phone_warning;
    private String setEstate;
    private String setPstate;

    private static final String TAG = "UserStateActivity";
    private String name = "";
    private String permission = "";
    private String email = "";
    private String phone = "";
    private String email_ctl = "";
    private String phone_ctl = "";
    private Button btn_chgemail;
    private Button btn_chgphone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_state);
        state_username = (TextView)findViewById(R.id.state_name);
        state_email = (TextView)findViewById(R.id.state_email);
        state_phone = (TextView)findViewById(R.id.state_phone);
        state_email_warning = (Switch)findViewById(R.id.state_email_warning);
        state_phone_warning = (Switch) findViewById(R.id.state_mess_warning);
        state_permission = (TextView) findViewById(R.id.state_permission);
        btn_chgemail = (Button)findViewById(R.id.chgemail);
        btn_chgphone = (Button)findViewById(R.id.chgphone);
        btn_chgemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserStateActivity.this, Chgemail.class);
                startActivity(intent);
            }
        });
        btn_chgphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserStateActivity.this, Chgphone.class);
                startActivity(intent);
            }
        });
        state_email_warning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEstate = "";
                if(isChecked){
                    setEstate = "on";
                }else {
                    setEstate = "off";
                }
                emailstate();
            }
        });
        state_phone_warning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPstate = "";
                if (isChecked){
                    setPstate = "on";
                }else {
                    setPstate = "off";
                }
                phonestate();
            }
        });
        pageflush();
    }


    public void pageflush(){
    new  Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            OkHttpClient client = new OkHttpClient();
            final RequestBody requestBody = new FormBody.Builder().add("name", LoginActivity.User_Id).build();
            Request request = new Request.Builder().url(LoginActivity.IP + "/user/getinfo").post(requestBody).build();
            Call call = client.newCall(request);
            Response response = null;
            try{
                response = call.execute();
                String responseData = response.body().string();
                /****t添加返回值判断*****/
                Log.d(TAG, responseData);
                JSONObject Jobject = new JSONObject(responseData);
                name = Jobject.optString("name",null);
                email = Jobject.optString("email",null);
                phone = Jobject.optString("phone",null);
                permission = Jobject.optString("permission",null);
                email_ctl = Jobject.optString("email_ctl",null);
                phone_ctl = Jobject.optString("phone_ctl",null);
                state_username.setText(name);
                state_email.setText(email);
                state_phone.setText(phone);
                state_permission.setText(permission);
                if(email_ctl.equals("on")){
                    state_email_warning.setChecked(true);
                }else{
                    state_email_warning.setChecked(false);
                }
                if (phone_ctl.equals("on")){
                    state_phone_warning.setChecked(true);
                }else{
                    state_phone_warning.setChecked(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Looper.loop();
        }
    }).start();
    }

    public void emailstate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                OkHttpClient client = new OkHttpClient();
                final RequestBody requestBody = new FormBody.Builder().add("name",LoginActivity.User_Id).add("control",setEstate).build();
                Log.d(TAG, "run: " + setEstate);
                Request request = new Request.Builder().url(LoginActivity.IP + "/user/setEswitch").post(requestBody).build();
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

    public void phonestate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                OkHttpClient client = new OkHttpClient();
                final RequestBody requestBody = new FormBody.Builder().add("name",LoginActivity.User_Id).add("control",setPstate).build();
                Log.d(TAG, "run: " + setPstate);
                Request request = new Request.Builder().url(LoginActivity.IP + "/user/setPswitch").post(requestBody).build();
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
