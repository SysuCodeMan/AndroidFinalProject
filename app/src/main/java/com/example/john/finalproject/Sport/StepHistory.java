package com.example.john.finalproject.Sport;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;

import com.example.john.finalproject.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StepHistory extends AppCompatActivity {
    LineCharView lcv;
    ListView history_left_drawer;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);
        lcv = (LineCharView) findViewById(R.id.stepLineChart);
        history_left_drawer = (ListView) findViewById(R.id.history_left_drawer);
        seekBar =(SeekBar)findViewById(R.id.lineChartSizeBar);
        seekBar.setMax(20);
        seekBar.setProgress(10);
        setDrawer();
        showLineChart();
        setMonthXCoor();
        setSeekBarListener();
    }

    public void setSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lcv.setYfactor(seekBar.getMax()-progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    List<String> x_coords;
    List<String> x_coord_values;

    private void showLineChart() {
        x_coords = new ArrayList<String>();

        x_coords.add("11月2日");
        x_coords.add("11月3日");
        x_coords.add("11月4日");
        x_coords.add("11月5日");
        x_coords.add("11月6日");
        x_coords.add("11月7日");
        x_coords.add("11月8日");
        x_coords.add("11月9日");
        x_coords.add("11月10日");
        x_coords.add("11月11日");
        x_coords.add("11月12日");
        x_coords.add("11月13日");
        x_coords.add("11月14日");
        x_coords.add("11月15日");
        x_coords.add("11月16日");
        x_coords.add("11月17日");


        x_coord_values = new ArrayList<String>();

        x_coord_values.add("2000");
        x_coord_values.add("1500");
        x_coord_values.add("1200");
        x_coord_values.add("1300");
        x_coord_values.add("1490");
        x_coord_values.add("2180");
        x_coord_values.add("2200");
        x_coord_values.add("2000");
        x_coord_values.add("500");
        x_coord_values.add("870");
        x_coord_values.add("1331");
        x_coord_values.add("2430");
        x_coord_values.add("1260");
        x_coord_values.add("230");
        x_coord_values.add("560");
        x_coord_values.add("1700");

        stepCountDB db = new stepCountDB(StepHistory.this);
        String name = getIntent().getStringExtra("name");
        List<stepRecord> records = db.query(name);
        Log.i("query!", records.toString());
        for (int i = 0; i < records.size(); i++) {
            String month = records.get(i).month;
            String day = records.get(i).day;
            String count = records.get(i).count;
            if (month.equals(""))continue;
            x_coords.add(month + "月" + day + "日");
            if (i == 0) {
                x_coord_values.add(count);
            } else {
                String prestep = records.get(i - 1).count;
                int prestepint = Integer.valueOf(prestep);
                int nowstepint = Integer.valueOf(count);
                if (prestepint < nowstepint || prestepint == nowstepint) {
                    x_coord_values.add("" + (nowstepint - prestepint));
                } else {
                    x_coord_values.add(count);
                }
            }
        }

        lcv.setBgColor(Color.TRANSPARENT);
        lcv.setXytextsize(50);
        lcv.setXytextcolor(Color.WHITE);
        lcv.setValue(x_coords, x_coord_values);
    }

    private void setDrawer() {
        final List<Map<String, Object>> data = new ArrayList<>();
        int[] icons = new int[]{R.mipmap.history, R.mipmap.history};
        String[] letters = new String[]{"日度数据", "月度数据"};
        for (int i = 0; i < 2; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("drawer_icon", icons[i]);
            temp.put("drawer_button", letters[i]);
            data.add(temp);
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[]{"drawer_icon", "drawer_button"}, new int[]{R.id.drawer_icon, R.id.drawer_button});
        history_left_drawer.setAdapter(simpleAdapter);
        history_left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    lcv.setYfactor(10);
                    seekBar.setMax(20);
                    seekBar.setProgress(10);
                    lcv.setValue(x_coords, x_coord_values);
                }
                if (i == 1) {
                    showMonth();
                }
            }
        });
    }

    String[] months = {"1月", "2月", "3月", "4月", "5月", "6月",
            "7月", "8月", "9月", "10月", "11月", "12月"};
    List<String> x_coords1;
    List<String> x_coord_values1;

    void setMonthXCoor() {
        x_coords1 = new ArrayList<>();
        x_coord_values1 = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            x_coords1.add(months[i]);
        }
    }

    void resetMonthY() {
        x_coord_values1.clear();
        for (int i = 0; i < 12; i++) {
            x_coord_values1.add("0");
        }
    }

    void showMonth() {
        resetMonthY();
        for (int i = 0; i < x_coords.size(); i++) {
            if (x_coords.get(i).equals("")) continue;
            String tempMonth = x_coords.get(i).split("月")[0];
            int index = Integer.valueOf(tempMonth)-1;
            int newvalue = Integer.valueOf(x_coord_values1.get(index)) + Integer.valueOf(x_coord_values.get(i));
            x_coord_values1.set(index, "" + newvalue);
        }
        lcv.setYfactor(150);
        seekBar.setMax(300);
        seekBar.setProgress(150);
        lcv.setValue(x_coords1, x_coord_values1);
    }
}
