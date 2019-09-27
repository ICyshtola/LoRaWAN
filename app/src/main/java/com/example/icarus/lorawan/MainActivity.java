package com.example.icarus.lorawan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.icarus.lorawan.ChangeUserInf.UserStateActivity;
import com.example.icarus.lorawan.CoverPassword.ChangepasswordActivity;
import com.example.icarus.lorawan.History.HistoricalData;
import com.example.icarus.lorawan.ListView.Device;
import com.example.icarus.lorawan.ListView.DeviceAdapter;
import com.example.icarus.lorawan.Login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private TextView textView;
    private List<Device> deviceList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDevice();
        DeviceAdapter adapter = new DeviceAdapter(MainActivity.this,R.layout.device_item,deviceList);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hv = navigationView.getHeaderView(0);
        textView = (TextView) hv.findViewById(R.id.textname);
        textView.setText(LoginActivity.User_Id);
        imageView = (ImageView) hv.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至状态界面
                Intent intent = new Intent(MainActivity.this, UserStateActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = deviceList.get(position);
                Intent intent = new Intent(MainActivity.this, HistoricalData.class);
                intent.putExtra("devEUI",device.getDeveui());
                intent.putExtra("devtype",device.getType());
                startActivity(intent );
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //处理操作栏项。只要你指定一个AndroidManifest.xml父活动，操作栏会自动处理单击Home/Up按钮。
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //Toast.makeText(this, "hello", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, ChangepasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initDevice(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                OkHttpClient client = new OkHttpClient();
                final RequestBody requestBody = new FormBody.Builder().add("name",LoginActivity.User_Id).build();
                Request request = new Request.Builder().url(LoginActivity.IP + "/find/userdevices").post(requestBody).build();
                Call call = client.newCall(request);
                Response response = null;
                try{
                    response = call.execute();
                    String responseData = response.body().string();
                    /****t添加返回值判断*****/
                    Log.d(TAG, "run: 返回值如下：");
                    Log.d(TAG, responseData);
                    JSONArray Jobject = new JSONArray(responseData);
                    for (int i = 0; i < Jobject.length();i++){
                        JSONObject jsonObject = Jobject.getJSONObject(i);
                        String deveui = jsonObject.optString("deveui",null);
                        String devname = jsonObject.optString("devname",null);
                        String st = jsonObject.optString("state",null);
                        String last = jsonObject.optString("lastruntime",null);
                        long lastruntime = Integer.valueOf(last);
                        String status ;
                        if (st.equals("1")){
                             status = "正在运行";
                        }else{
                            status = "已断开连接";
                        }
                        String ty = jsonObject.optString("type",null);
                        String type = "";
                        if(ty.contains("humidity")){
                            type = type + "湿度 ";
                        }
                        if(ty.contains("temperature")){
                            type = type + "温度 ";
                        }
                        //添加可监测字段
                        Device device = new Device(deveui,devname,status,getDateToString(lastruntime,"E yyyy.MM.dd HH:mm:ss"),type);
                        deviceList.add(device);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond * 1000 + 8 * 3600);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
