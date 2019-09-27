package com.example.icarus.lorawan.Register;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icarus.lorawan.Login.LoginActivity;
import com.example.icarus.lorawan.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView username;
    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText repassword;
    private Button button;
    private static final String TAG = "RegisterActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (AutoCompleteTextView) findViewById(R.id.regusername);
        email = (EditText) findViewById(R.id.regemail);
        phone = (EditText) findViewById(R.id.regphone);
        password = (EditText) findViewById(R.id.regpassword);
        repassword = (EditText) findViewById(R.id.regrepassword);
        button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               attemption();
            }
        });
        Button button1 = (Button) findViewById(R.id.regback);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                finish();
            }
        });
    }
    private void attemption(){
        String usernamestring = username.getText().toString();
        String emailstring = email.getText().toString();
        String phonestring = phone.getText().toString();
        String passwordstring = password.getText().toString();
        String repasswordstring = repassword.getText().toString();
        View focusView = null;
        username.setError(null);
        email.setError(null);
        phone.setError(null);
        password.setError(null);
        repassword.setError(null);
        boolean flag = true;
        if (phonestring.length() != 11){
            phone.setError("输入的手机号不合规范");
            focusView = phone;
            flag = false;
        }
        if (usernamestring.length() < 3){
            username.setError("用户名太短");
            focusView = username;
            flag = false;
        }
        if (passwordstring.length() < 6){
            password.setError("密码太短");
            focusView = password;
            flag = false;
        }
        if (!isEmail(emailstring)){
            email.setError("邮箱格式错误");
            focusView = email;
            flag = false;
        }
        if (!passwordstring.equals(repasswordstring)){
            repassword.setError("密码不一致");
            focusView = repassword;
            flag = false;
        }
        if (flag){
            mysend(usernamestring,emailstring,phonestring,passwordstring,repasswordstring);
        }
        else {
            focusView.requestFocus();
        }
    }
    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    private void mysend(String usernamestring, String emailstring,String phonestring,String passwordstring,String repasswordstring){
        final RequestBody requestBody = new FormBody.Builder().add("name",usernamestring).add("password",getMD5(passwordstring))
                .add("email",emailstring).add("phone",phonestring).build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(LoginActivity.IP + "/user/reg" ).post(requestBody).build();
                Call call = client.newCall(request);
                Response response = null;
                try{
                    response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    /****t添加返回值判断*****/
                    Log.d(TAG, responseData);
                    if(responseData.equals("success")){
                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (responseData.equals("Has")){
                        Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

    public String getMD5(String info)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++)
            {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1)
                {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                }
                else
                {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            return "";
        }
        catch (UnsupportedEncodingException e)
        {
            return "";
        }
    }
}
