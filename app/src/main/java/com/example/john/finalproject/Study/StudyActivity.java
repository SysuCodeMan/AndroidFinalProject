package com.example.john.finalproject.Study;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.Life.account.LifeActivity;
import com.example.john.finalproject.MainActivity;
import com.example.john.finalproject.R;
import com.example.john.finalproject.Sport.SportActivity;
import com.example.john.finalproject.userDB;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xia on 2016/12/1.
 */
public class StudyActivity extends AppCompatActivity {
    private DrawerLayout study_drawer_layout;
    private ListView study_left_drawer;
    private ImageButton study_sport_button;
    private ImageButton study_life_button;
    private ImageButton study_study_button;
    private Button timetable_query_button, score_query_button;
    private TextView timetable_title, timetable_hint;
    private GridView timetable_gridview;
    private CookieManager cookieManager;
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String username;

    private static final int UpdateTimetable = 1;
    private String getURL = "";
    private TimetableAdapter timetableAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UpdateTimetable:
                    timetable_hint.setVisibility(View.GONE);
                    timetable_gridview.setVisibility(View.VISIBLE);
                    ArrayList<Course> courses = (ArrayList<Course>) msg.obj;
                    timetableAdapter.UpdateTimeTable(courses);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_layout);
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);
        cookieManager = CookieManager.getInstance();
        timetableAdapter = new TimetableAdapter(this, username);

        findView();
        setDrawer();
        timetable_gridview.setAdapter(timetableAdapter);
        study_sport_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyActivity.this, SportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        study_life_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyActivity.this, LifeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        study_study_button.setClickable(false);
        timetable_query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkIsAvaliable()) {
                    System.out.println("断网");
                    Toast.makeText(StudyActivity.this, "哎哟好像断网了(╯﹏╰)", Toast.LENGTH_SHORT).show();
                } else if (!isLogin()) {
                    Login();
                } else {
                    ShowDialog();
                }
            }
        });
        score_query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StudyActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });
        scrolllock = false;
        timetable_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (scrolllock == false) {
                    ArrayList<Course> courses = timetableAdapter.getCourses();
                    if (courses.get(position).getLength() != 0) {
                        int index = courses.get(position).getIndex();
                        String title = courses.get(index).getName();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", title);
                        Intent intent = new Intent();
                        intent.setClass(StudyActivity.this, HomeworkActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });
        timetable_gridview.setFastScrollEnabled(false);
        //timetable_gridview.scroll
    }

    private boolean scrolllock;
    private void ShowDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(StudyActivity.this);
        LayoutInflater factory = LayoutInflater.from(StudyActivity.this);
        View DialogView = factory.inflate(R.layout.timetable_selecte_term, null);
        builder.setView(DialogView);
        builder.setTitle("选择学期");
        final Spinner year = (Spinner) DialogView.findViewById(R.id.selectYear);
        final Spinner term = (Spinner) DialogView.findViewById(R.id.selectTerm);
        final String[] years = getYears();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StudyActivity.this, android.R.layout.simple_spinner_item, years);
        year.setAdapter(adapter);
        getURL = "http://wjw.sysu.edu.cn/api/timetable_new?";
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedYear = year.getSelectedItem().toString();
                String selectedTerm = term.getSelectedItem().toString();
                if (selectedYear == "" || selectedTerm == "") {
                    selectedYear = years[0];
                    selectedTerm = "";
                }
                getURL += "year=" + selectedYear + "&term=";
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
                }
                getURL += queryTerm;
                timetable_title.setText(selectedYear + "学年 " + selectedTerm);
                System.out.println("URL:" + getURL);
                RequestData(getURL);
            }
        });
        builder.create();
        builder.show();
    }

    private String[] getYears() {
        String[] years = new String[4];
        SimpleDateFormat formater = new SimpleDateFormat("yyyy");
        Date currentDate = new Date(System.currentTimeMillis());
        String currentYear = formater.format(currentDate);
        System.out.println(currentYear);
        int now;
        now = Integer.parseInt(currentYear);
        ;
        ;
        for (int i = 0; i < 4; i++) {
            years[i] = Integer.toString(now) + "-" + Integer.toString(now + 1);
            now--;
        }
        return years;
    }

    private boolean NetworkIsAvaliable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService((CONNECTIVITY_SERVICE));
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo != null) {
            return networkinfo.isAvailable();
        }
        return false;
    }

    private void RequestData(final String getURL) {
        timetable_gridview.setVisibility(View.INVISIBLE);
        timetable_hint.setVisibility(View.VISIBLE);
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
                    if (connection.getResponseCode() == 200) {
                        InputStream stream = connection.getInputStream();
                        data = ConvertInputStreamToString(stream);
                        getTimetable(data);
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

    private void getTimetable(String data) {
        org.jsoup.nodes.Document doc = Jsoup.parse(data);
        Elements elements = doc.getElementsByTag("tr");
        ArrayList<Course> courses = new ArrayList<Course>();
        for (int i = 0; i < 128; i++) {

            courses.add(new Course("", "", "", "", 0, 0, -1));

        }
        Elements days = elements.get(0).getElementsByTag("td");
        for (int i = 0; i < 8; i++) {
            if (!days.get(i).html().equals("&nbsp;"))
                courses.get(i).setName(days.get(i).html());
        }
        for (int i = 1; i <= 15; i++) {
            Elements course = elements.get(i).getElementsByTag("td");
            courses.get(8 * i).setName(course.get(0).html());
            int index = 1;
            for (int j = 1; j < course.size(); j++) {
                while (courses.get(8 * i + index).getLength() != 0) {
                    index++;
                }
                if (course.get(j).className().equals("tab_1")) {
                    int length = Integer.parseInt(course.get(j).attr("rowspan"));
                    int num = index + 8 * i;
                    for (int nextRow = 0; nextRow < length; nextRow++) {
                        courses.get(num + nextRow * 8).setLength(length);
                        courses.get(num + nextRow * 8).setIndex(8 * i + index);
                        courses.get(num + nextRow * 8).setPlace(nextRow);
                        System.out.println(courses.get(num + nextRow * 8).getName());
                        System.out.println(courses.get(num + nextRow * 8).getPlace());
                    }
                    String[] details = course.get(j).html().split("<br>");
                    Course targetCourse = courses.get(num);
                    targetCourse.setName(details[0]);
                    targetCourse.setClassroom(details[1]);
                    targetCourse.setTime(details[2]);
                    targetCourse.setPeriod(details[3]);
                }
                index++;
            }
        }
        Message message = new Message();
        message.what = UpdateTimetable;
        message.obj = courses;
        handler.sendMessage(message);
    }


    private void Login() {
        Intent intent = new Intent();
        intent.setClass(StudyActivity.this, LoginActivity.class);
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
        ShowDialog();
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

    private void findView() {
        study_drawer_layout = (DrawerLayout) findViewById(R.id.study_drawer_layout);
        study_left_drawer = (ListView) findViewById(R.id.study_left_drawer);
        study_sport_button = (ImageButton) findViewById(R.id.study_sport_button);
        study_life_button = (ImageButton) findViewById(R.id.study_life_button);
        study_study_button = (ImageButton) findViewById(R.id.study_study_button);
        timetable_query_button = (Button) findViewById(R.id.timetable_query);
        score_query_button = (Button) findViewById(R.id.score_query);
        timetable_title = (TextView) findViewById(R.id.timetable_title);
        timetable_hint = (TextView) findViewById(R.id.timetable_hint);
        timetable_gridview = (GridView) findViewById(R.id.timetable_gridview);
    }

    private void setDrawer() {
        final List<Map<String, Object>> data = new ArrayList<>();
        int[] icons = new int[]{R.mipmap.user_icon, R.mipmap.edit_icon, R.mipmap.logout_icon};
        String[] letters = new String[]{username, "修改密码", "退出登录"};
        for (int i = 0; i < 3; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("drawer_icon", icons[i]);
            temp.put("drawer_button", letters[i]);
            data.add(temp);
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[]{"drawer_icon", "drawer_button"}, new int[]{R.id.drawer_icon, R.id.drawer_button});
        study_left_drawer.setAdapter(simpleAdapter);
        study_left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    LayoutInflater factory = LayoutInflater.from(StudyActivity.this);
                    final View dialog_view = factory.inflate(R.layout.password_dialog_layout, null);
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StudyActivity.this);
                    builder.setTitle("修改密码");
                    builder.setView(dialog_view);
                    TextView password_dialog_username = (TextView) dialog_view.findViewById(R.id.password_dialog_username);
                    password_dialog_username.setText(username);
                    final EditText password_dialog_old_password = (EditText) dialog_view.findViewById(R.id.password_dialog_old_password);
                    final EditText password_dialog_new_password = (EditText) dialog_view.findViewById(R.id.password_dialog_new_password);
                    Button password_dialog_quit_button = (Button) dialog_view.findViewById(R.id.password_dialog_quit_button);
                    Button password_dialog_save_button = (Button) dialog_view.findViewById(R.id.password_dialog_save_button);
                    builder.create();
                    final android.app.AlertDialog alertDialog = builder.show();
                    password_dialog_quit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    password_dialog_save_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            userDB userdb = new userDB(StudyActivity.this);
                            if (TextUtils.isEmpty(password_dialog_old_password.getText().toString())) {
                                Toast.makeText(StudyActivity.this, "原密码为空", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password_dialog_new_password.getText().toString())) {
                                Toast.makeText(StudyActivity.this, "新密码为空", Toast.LENGTH_SHORT).show();
                            } else {
                                if (userdb.confirmUser(username, password_dialog_old_password.getText().toString())) {
                                    userdb.updateData(username, password_dialog_new_password.getText().toString());
                                    alertDialog.dismiss();
                                    Toast.makeText(StudyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(StudyActivity.this, "原密码输入错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else if (i == 2) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(StudyActivity.this);
                    builder.setMessage("确定退出登录？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.putString("state", "logout");
                            editor.commit();
                            cookieManager.removeAllCookie();
                            Toast.makeText(StudyActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudyActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
    }
}
