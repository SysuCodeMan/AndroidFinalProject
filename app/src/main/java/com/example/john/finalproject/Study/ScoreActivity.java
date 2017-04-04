package com.example.john.finalproject.Study;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chen on 2016/12/4.
 */

public class ScoreActivity extends AppCompatActivity {
    private CookieManager cookieManager;
    private Button score_update;
    private ListView score_list;
    private TextView hint, score_title;
    private ScoreAdapter scoreAdapter;
    private String getURL = "";

    private static final int Update_score = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Update_score:
                    hint.setVisibility(View.INVISIBLE);
                    score_list.setVisibility(View.VISIBLE);
                    ArrayList<CourseScore> result = (ArrayList<CourseScore>)msg.obj;
                    scoreAdapter.UpdateData(result);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        findViews();
        scoreAdapter = new ScoreAdapter(this);
        score_list.setAdapter(scoreAdapter);
        cookieManager = CookieManager.getInstance();
        score_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkIsAvaliable()) {
                    System.out.println("断网");
                    Toast.makeText(ScoreActivity.this, "哎哟好像断网了(╯﹏╰)", Toast.LENGTH_SHORT).show();
                } else if (!isLogin()) {
                    Login();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
                    LayoutInflater factory = LayoutInflater.from(ScoreActivity.this);
                    View DialogView = factory.inflate(R.layout.select_item_layout, null);
                    builder.setView(DialogView);
                    builder.setTitle("选择学期");
                    final Spinner year = (Spinner) DialogView.findViewById(R.id.selectYear);
                    final Spinner term = (Spinner) DialogView.findViewById(R.id.selectTerm);
                    final String[] years = getYears();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScoreActivity.this,android.R.layout.simple_spinner_item, years);
                    year.setAdapter(adapter);
                    getURL = "http://wjw.sysu.edu.cn/api/score?";
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectedYear = year.getSelectedItem().toString();
                            String selectedTerm = term.getSelectedItem().toString();
                            if (selectedYear == "" || selectedTerm == "") {
                                selectedYear = years[0];
                                selectedTerm = "";
                            }
                            getURL += "year="+selectedYear+"&term=";
                            String queryTerm = "";
                            switch (selectedTerm) {
                                case "":
                                    queryTerm = "";
                                    break;
                                case "第一学期":
                                    queryTerm = "1";
                                    break;
                                case "第二学期":
                                    queryTerm = "2";
                                    break;
                                case "第三学期":
                                    queryTerm = "3";
                                    break;
                                case  "所有":
                                    queryTerm = "";
                                    break;
                            }
                            score_title.setText(selectedYear+"学年 "+selectedTerm);
                            getURL += queryTerm+"&pylb=01";
                            System.out.println("URL:"+ getURL);
                            RequestData(getURL);
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
    }

    private String[] getYears() {
        String[] years = new String[4];
        SimpleDateFormat formater = new SimpleDateFormat("yyyy");
        Date currentDate = new Date(System.currentTimeMillis());
        String currentYear = formater.format(currentDate);
        System.out.println(currentYear);
        int now;
        now = Integer.parseInt(currentYear);;;
        for (int i = 0; i < 4; i++) {
            years[i] = Integer.toString(now)+"-"+Integer.toString(now+1);
            now--;
        }
        return years;
    }

    private boolean NetworkIsAvaliable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService((CONNECTIVITY_SERVICE));
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo != null) {
            return networkinfo.isAvailable();
        }
        return false;
    }

    private void findViews() {
        score_list = (ListView)findViewById(R.id.score_list);
        score_update = (Button)findViewById(R.id.score_update);
        hint = (TextView)findViewById(R.id.hint);
        score_title = (TextView)findViewById(R.id.score_title);
    }

    private void RequestData(final String getURL) {
        hint.setVisibility(View.VISIBLE);
        score_list.setVisibility(View.INVISIBLE);
        final String cookie = cookieManager.getCookie("http://wjw.sysu.edu.cn/");
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String data;
                    URL url = new URL(getURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", cookie);
                    System.out.println("已经登录");
                    if (connection.getResponseCode() == 200) {
                        System.out.println("请求成功");
                        InputStream stream = connection.getInputStream();
                        data = ConvertInputStreamToString(stream);
                        getScore(data);
                    } else {
                        System.out.println("Request Failure!:" + connection.getResponseCode());
                    }
                } catch (MalformedURLException e) {
                    System.out.println("URL has problem");
                } catch (IOException e) {
                    System.out.println("IOException");
                }
            }
        }.start();
    }

    private void getScore(String data) {
        try {
            JSONObject json = new JSONObject(data);
            JSONArray courseName = json.getJSONObject("body").getJSONObject("dataStores").getJSONObject("kccjStore").getJSONObject("rowSet").getJSONArray("primary");
            ArrayList<CourseScore> courseScores = new ArrayList<CourseScore>();
            for (int i = 0; i < courseName.length(); i++) {
                JSONObject object = courseName.getJSONObject(i);
                String name = "";
                if (object.has("kcmc"))
                    name = object.getString("kcmc");
                String teacher = "";
                if (object.has("jsxm"))
                    teacher = object.getString("jsxm");
                String rank = "";
                if (object.has("jxbpm"))
                    rank = object.getString("jxbpm");
                String id = "";
                if (object.has("cjlcId"))
                    id = object.getString("cjlcId");
                String creadit = "";
                if (object.has("xf"))
                    creadit = object.getString("xf");
                String score = "";
                if (object.has("zpcj"))
                    score  = object.getString("zpcj");
                String gpa = "";
                if (object.has("jd"))
                    gpa = object.getString("jd");
                CourseScore courseScore = new CourseScore(name, teacher, rank, id, creadit, score,gpa);
                courseScores.add(courseScore);
                System.out.println(name+" "+teacher+" "+rank+" "+id+" "+creadit+" "+score+" "+gpa);
            }
            Message message = new Message();
            message.what = Update_score;
            message.obj = courseScores;
            handler.sendMessage(message);
        } catch (JSONException e) {
            System.out.println("Json Problem Occurs!");
        }
    }

    private void Login() {
        Intent intent = new Intent();
        intent.setClass(ScoreActivity.this, LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    private String ConvertInputStreamToString(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String result = "";
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                sb.append(line + '\n');
            }
        } catch (IOException e) {
            System.out.println("Problem Occur When Analayze The Stream!");
        }
        result = sb.toString();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String getURL = "http://wjw.sysu.edu.cn/api/score?year=2015-2016&term=&pylb=01";
        RequestData(getURL);
    }

    private boolean isLogin() {
        String cookie = cookieManager.getCookie("http://wjw.sysu.edu.cn/");
        Log.v("test", "cookie is:" + cookie);
        if (cookie == null) {
            System.out.println("Cookie:is null");
            return false;
        } else {
            return cookie.contains("sno");
        }
    }
}
