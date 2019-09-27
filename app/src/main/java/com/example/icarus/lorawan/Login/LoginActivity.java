package com.example.icarus.lorawan.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icarus.lorawan.CoverPassword.ForgetChoice;
import com.example.icarus.lorawan.MainActivity;
import com.example.icarus.lorawan.R;
import com.example.icarus.lorawan.Register.RegisterActivity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = "LoginActivity";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * 记录登录的任务,以确保如果需要我们可以取消它。
     */
    private UserLoginTask mAuthTask = null;
    // 定义组件
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
//    public static String IP = "http://101.132.156.183:8090";
    public static String IP = "http://192.168.100.136:8090";
    public static String User_Id = "no";
    boolean ret = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private TextView forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //首次启动 Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT 为 0，再次点击图标启动时就不为零了
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass = (CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember = pref.getBoolean("remember_password",false);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button button = findViewById(R.id.email_register);
        /*未加密pref中的值*/
        if (isRemember){
            String account = pref.getString("account","");
            String password = pref.getString("password","");
            mEmailView.setText(account);
            mPasswordView.setText(password);
            rememberPass.setChecked(true);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至注册界面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        forget = (TextView)findViewById(R.id.forget_pass);
        forget.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, ForgetChoice.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * 　试图登录用户指定的账号。
     * 　如果有错误(无效的电子邮件,缺少字段等),并给出了错误,没有实际的登录尝试。
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // 重置错误
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // 储存登录信息
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        //Log.d(TAG, email);
        //Log.d(TAG, password);
        boolean cancel = false;
        View focusView = null;

        // 检查密码
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("密码不符规范");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("用户名不能为空");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            //输入出错处理
            focusView.requestFocus();
        } else {
            //输入无误，创建后台程序用于登录
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * 代表一个异步登录/注册任务用于对用户进行身份验证。
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            Log.d(TAG, email);
            Log.d(TAG, password);
        }
        @Override

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            /*************************************/
            mysend(mEmail,mPassword);
            /**************************************/
            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "doInBackground: error");
                return ret;
            }
            /*返回值为true表示登录成功，否则登录失败*/
            if(ret == false){
                Log.d(TAG, "doInBackground: false");   
            }else {
                Log.d(TAG, "doInBackground: true");
            }
            return ret;
            //return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            Log.d(TAG, "onPostExecute");
            if (success) {
                editor = pref.edit();
                if (rememberPass.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",mEmail);
                    editor.putString("password",mPassword);
                }else{
                    editor.clear();
                }
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError("用户名或密码错误");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private void mysend(final String name,final String password){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    OkHttpClient client = new OkHttpClient();
                    final RequestBody requestBody = new FormBody.Builder().add("name",name).add("password",getMD5(password)).build();
                    Request request = new Request.Builder().url(IP + "/user/login").post(requestBody).build();
                    //Request request = new Request.Builder().url("https://www.baidu.com").build();
                    Call call = client.newCall(request);
                    Response response = null;
                    try{
                        response = call.execute();
                        String responseData = response.body().string();
                        /****t添加返回值判断*****/
                        Log.d(TAG, responseData);
                        if(responseData.equals("success")){
                            ret = true;
                            User_Id = name;
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        }else {
                            ret = false;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }
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

