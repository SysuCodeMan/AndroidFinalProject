package com.example.john.finalproject.Life.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.Life.express.LifeActivityExpress;
import com.example.john.finalproject.MainActivity;
import com.example.john.finalproject.R;
import com.example.john.finalproject.Sport.SportActivity;
import com.example.john.finalproject.Study.StudyActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by limin on 2016/12/18.
 */
public class Chart extends AppCompatActivity {

    private DrawerLayout life_drawer_layout;
    private ListView life_left_drawer;
    private ImageButton life_sport_button;
    private ImageButton life_life_button;
    private ImageButton life_study_button;
    private ImageButton account;
    private ImageButton express;
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String username;
    private TextView totalInto;
    private TextView totalOut;

    private Float out;
    private Float in;
    private Float income;
    private Float eating;
    private Float transport;
    private Float commodity;
    private Float social;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);

        Bundle bundle = this.getIntent().getExtras();
        out = bundle.getFloat("totalOut");
        in = bundle.getFloat("totalInto");
        income = Math.abs(bundle.getFloat("income"));
        eating = Math.abs(bundle.getFloat("eating"));
        transport = Math.abs(bundle.getFloat("transport"));
        commodity = Math.abs(bundle.getFloat("commdity"));
        social = Math.abs(bundle.getFloat("social"));


        findView();
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);
        setDrawer();

        MyPieChart pieChart = (MyPieChart)findViewById(R.id.chart);
        ArrayList<Float> values = new ArrayList<>();
        values.add(income);
        values.add(eating);
        values.add(transport);
        values.add(commodity);
        values.add(social);
        pieChart.setValues(values);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(164, 222, 208));
        colors.add(Color.rgb(165, 195, 105));
        colors.add(Color.rgb(133, 208, 235));
        colors.add(Color.rgb(247, 155, 101));
        colors.add(Color.rgb(224, 113, 113));
        pieChart.setColors(colors);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("收入");
        labels.add("餐饮");
        labels.add("交通");
        labels.add("日用");
        labels.add("社交");
        pieChart.setLabels(labels);

        life_sport_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chart.this, SportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        life_life_button.setClickable(false);
        life_study_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chart.this, StudyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        //  点击记账按钮跳转到生活界面默认记账页面
        account.setClickable(false);
        //  进入到快递查询页面，设置该按钮点击事件无效
        express.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chart.this, LifeActivityExpress.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        totalOut.setText(out.toString());
        totalInto.setText(in.toString());
    }

    private void findView() {
        life_drawer_layout = (DrawerLayout) findViewById(R.id.life_drawer_layout);
        life_left_drawer = (ListView) findViewById(R.id.life_left_drawer);
        life_sport_button = (ImageButton) findViewById(R.id.life_sport_button);
        life_life_button = (ImageButton) findViewById(R.id.life_life_button);
        life_study_button = (ImageButton) findViewById(R.id.life_study_button);
        account = (ImageButton)findViewById(R.id.account);
        express = (ImageButton)findViewById(R.id.express);
        totalInto = (TextView)findViewById(R.id.totol_income);
        totalOut = (TextView)findViewById(R.id.total_expenditure);
    }

    private void setDrawer() {
        final List<Map<String, Object>> data = new ArrayList<>();
        int[] icons = new int[] {R.mipmap.expenditure, R.mipmap.edit_icon, R.mipmap.logout_icon};
        String[] letters = new String[] {"消费占比", "修改密码", "退出登录"};
        for (int i = 0; i < 3; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("drawer_icon", icons[i]);
            temp.put("drawer_button", letters[i]);
            data.add(temp);
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[] {"drawer_icon", "drawer_button"}, new int[] {R.id.drawer_icon, R.id.drawer_button});
        life_left_drawer.setAdapter(simpleAdapter);
        life_left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent();
                    intent.setClass(Chart.this, Chart.class);
                    startActivity(intent);
                }
                else if (i == 2) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Chart.this);
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
                            Toast.makeText(Chart.this, "退出成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Chart.this,  MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
    }

    // 解决转屏问题
    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }
    }

}
