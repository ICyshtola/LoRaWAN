package com.example.icarus.lorawan.History;

import android.content.Intent;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.icarus.lorawan.Login.LoginActivity;
import com.example.icarus.lorawan.R;
import com.github.mikephil.charting.charts.LineChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HistoricalData extends AppCompatActivity {

    private LineChart lineChart;
    private String devEUI;
    private String devtype;
    private static final String TAG = "HistoricalData";
    private LineChartManager lineChartManager;
    private List<IncomeBean> stp = new ArrayList<>();
    private List<IncomeBean> smt = new ArrayList<>();
    private List<IncomeBean> stds = new ArrayList<>();
    private List<IncomeBean> sec = new ArrayList<>();
    private List<IncomeBean> tp = new ArrayList<>();
    private List<IncomeBean> hum = new ArrayList<>();
    private List<IncomeBean> ill= new ArrayList<>();
    private List<IncomeBean> co2 = new ArrayList<>();
    private List<IncomeBean> val = new ArrayList<>();
    private ConstraintLayout cl_stp;
    private ConstraintLayout cl_smt;
    private ConstraintLayout cl_stds;
    private ConstraintLayout cl_sec;
    private ConstraintLayout cl_tp;
    private ConstraintLayout cl_hum;
    private ConstraintLayout cl_ill;
    private ConstraintLayout cl_co2;
    private View vi_stp;
    private View vi_smt;
    private View vi_stds;
    private View vi_sec;
    private View vi_tp;
    private View vi_hum;
    private View vi_ill;
    private View vi_co2;
    private boolean bl_stp = true;
    private boolean bl_smt = true;
    private boolean bl_stds = true;
    private boolean bl_sec = true;
    private boolean bl_tp = true;
    private boolean bl_hum = true;
    private boolean bl_ill = true;
    private boolean bl_co2 = true;
    private List<String> dev_eui = new ArrayList<>();
    private List<String> dev_type = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        devEUI = intent.getStringExtra("devEUI");
        devtype = intent.getStringExtra("devtype");
        generateValues();
        //initlc();
    }

    public void initlc(){
        lineChart = findViewById(R.id.lineChart);
        lineChartManager = new LineChartManager(lineChart);
        cl_stp = findViewById(R.id.layout_stp);
        cl_co2 = findViewById(R.id.layout_co2);
        cl_hum = findViewById(R.id.layout_hum);
        cl_ill = findViewById(R.id.layout_ill);
        cl_sec = findViewById(R.id.layout_sec);
        cl_smt = findViewById(R.id.layout_smt);
        cl_stds = findViewById(R.id.layout_stds);
        cl_tp = findViewById(R.id.layout_tp);
        vi_co2 = findViewById(R.id.view_co2);
        vi_hum = findViewById(R.id.view_hum);
        vi_ill = findViewById(R.id.view_ill);
        vi_sec = findViewById(R.id.view_sec);
        vi_smt = findViewById(R.id.view_smt);
        vi_stds = findViewById(R.id.view_stds);
        vi_stp = findViewById(R.id.view_stp);
        vi_tp = findViewById(R.id.view_tp);
        cl_tp.setOnClickListener(listener);
        cl_stds.setOnClickListener(listener);
        cl_smt.setOnClickListener(listener);
        cl_sec.setOnClickListener(listener);
        cl_ill.setOnClickListener(listener);
        cl_hum.setOnClickListener(listener);
        cl_co2.setOnClickListener(listener);
        cl_stp.setOnClickListener(listener);
        /**添加折线**/
        lineChartManager.showLineChart(stp,"土壤温度", ContextCompat.getColor(this,R.color.bg_blue));
        lineChartManager.addLine(smt,"土壤湿度",ContextCompat.getColor(this,R.color.orange));
        lineChartManager.addLine(stds,"TDS",ContextCompat.getColor(this,R.color.app_black));
        lineChartManager.addLine(sec,"EC",ContextCompat.getColor(this,R.color.text_yellow));
        lineChartManager.addLine(tp,"空气温度",ContextCompat.getColor(this,R.color.colorPrimaryDark));
        lineChartManager.addLine(hum,"空气湿度",ContextCompat.getColor(this,R.color.violet));
        lineChartManager.addLine(ill,"光照强度",ContextCompat.getColor(this,R.color.green));
        lineChartManager.addLine(co2,"co2浓度",ContextCompat.getColor(this,R.color.blue));
        lineChartManager.setMarkerView(this);
    }

   
    public void generateValues(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                long val = System.currentTimeMillis();
                String endtime = String.valueOf(val);
                long eval = val - 1 * 1000 * 3600;
                String starttime = String.valueOf(eval);
                OkHttpClient client = new OkHttpClient();
                if (devtype.contains("温度")){
                    dev_eui.add(devEUI);
                    dev_type.add("temperature");
                }
                if (devtype.contains("湿度")){
                    dev_eui.add(devEUI);
                    dev_type.add("humidity");
                }
                final RequestBody requestBody = new FormBody.Builder().add("startTime",starttime).add("endTime",endtime)
                        .add("devEUI",dev_eui.toString()).add("type",dev_type.toString()).build();
                Request request = new Request.Builder().url(LoginActivity.IP + "/find/history").post(requestBody).build();
                Call call = client.newCall(request);
                Response response = null;
                try{
                    response = call.execute();
                    String responseData = response.body().string();
                    //添加返回值判断
                    Log.d(TAG, responseData);

                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout_stp:
                    if (bl_stp == true){
                        bl_stp = false;
                        lineChartManager.resetLine(0,val,"土壤温度", ContextCompat.getColor(HistoricalData.this,R.color.bg_blue));
                        vi_stp.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_stp = true;
                        lineChartManager.resetLine(0,stp,"土壤温度", ContextCompat.getColor(HistoricalData.this,R.color.bg_blue));
                        vi_stp.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_blue));
                    }
                    break;
                case R.id.layout_smt:
                    if (bl_smt == true){
                        bl_smt = false;
                        lineChartManager.resetLine(1,val,"土壤湿度", ContextCompat.getColor(HistoricalData.this,R.color.orange));
                        vi_smt.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_smt = true;
                        lineChartManager.resetLine(1,smt,"土壤湿度", ContextCompat.getColor(HistoricalData.this,R.color.orange));
                        vi_smt.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_orange));
                    }
                    break;
                case R.id.layout_stds:
                    if (bl_stds == true){
                        bl_stds = false;
                        lineChartManager.resetLine(2,val,"土壤TDS", ContextCompat.getColor(HistoricalData.this,R.color.app_black));
                        vi_stds.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_stds = true;
                        lineChartManager.resetLine(2,stds,"土壤TDS", ContextCompat.getColor(HistoricalData.this,R.color.app_black));
                        vi_stds.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_black));
                    }
                    break;
                case R.id.layout_sec:
                    if (bl_sec == true){
                        bl_sec = false;
                        lineChartManager.resetLine(3,val,"土壤EC", ContextCompat.getColor(HistoricalData.this,R.color.text_yellow));
                        vi_sec.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_sec = true;
                        lineChartManager.resetLine(3,sec,"土壤EC", ContextCompat.getColor(HistoricalData.this,R.color.text_yellow));
                        vi_sec.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_yellow));
                    }
                    break;
                case R.id.layout_tp:
                    if (bl_tp == true){
                        bl_tp = false;
                        lineChartManager.resetLine(4,val,"空气温度", ContextCompat.getColor(HistoricalData.this,R.color.colorPrimaryDark));
                        vi_tp.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_tp = true;
                        lineChartManager.resetLine(4,tp,"空气温度", ContextCompat.getColor(HistoricalData.this,R.color.colorPrimaryDark));
                        vi_tp.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_dark));
                    }
                    break;
                case R.id.layout_hum:
                    if (bl_hum == true){
                        bl_hum = false;
                        lineChartManager.resetLine(5,val,"空气湿度", ContextCompat.getColor(HistoricalData.this,R.color.violet));
                        vi_hum.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_hum = true;
                        lineChartManager.resetLine(5,hum,"空气湿度", ContextCompat.getColor(HistoricalData.this,R.color.violet));
                        vi_hum.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_violet));
                    }
                    break;
                case R.id.layout_ill:
                    if (bl_ill == true){
                        bl_ill = false;
                        lineChartManager.resetLine(6,val,"光照强度", ContextCompat.getColor(HistoricalData.this,R.color.green));
                        vi_ill.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_ill = true;
                        lineChartManager.resetLine(6,ill,"光照强度", ContextCompat.getColor(HistoricalData.this,R.color.green));
                        vi_ill.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_green));
                    }
                    break;
                case R.id.layout_co2:
                    if (bl_co2 == true){
                        bl_co2 = false;
                        lineChartManager.resetLine(7,val,"co2浓度", ContextCompat.getColor(HistoricalData.this,R.color.blue));
                        vi_co2.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_gray));
                    }else {
                        bl_co2 = true;
                        lineChartManager.resetLine(7,co2,"co2浓度", ContextCompat.getColor(HistoricalData.this,R.color.blue));
                        vi_co2.setBackground(ContextCompat.getDrawable(HistoricalData.this,R.drawable.shape_round_deepblue));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}