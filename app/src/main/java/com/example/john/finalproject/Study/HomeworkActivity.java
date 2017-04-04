package com.example.john.finalproject.Study;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.R;

/**
 * Created by Chen on 2016/12/6.
 */

public class HomeworkActivity extends AppCompatActivity {
    ListView homework_history_list;
    Button save_homework;
    EditText edit_homework;
    HomeworkAdapter homeworkAdater;
    TextView homework_title;
    private String username;
    private String course;
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_layout);
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        findViews();
        setTitle();
        setAdapterAndListener();
    }

    private void setTitle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        course = bundle.getString("title");
        homework_title.setText(course);
    }

    private void setAdapterAndListener() {
        homeworkAdater = new HomeworkAdapter(this, username, course);
        homework_history_list.setAdapter(homeworkAdater);
        homework_history_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeworkActivity.this);
                builder.setMessage("确定删除该项作业？");
                builder.setTitle("删除作业");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        homeworkAdater.RemoveData(position);
                        TextView temp= (TextView) view.findViewById(R.id.homework_description);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
                return false;
            }
        });
        homework_history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("test for click");
            }
        });
        save_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String homework_description = edit_homework.getText().toString();
                if (homework_description.equals("")) {
                    Toast.makeText(HomeworkActivity.this, "输入为空，羡慕没有作业的人", Toast.LENGTH_SHORT).show();
                } else {
                    homeworkAdater.AddData(new Homework(false, homework_description));
                    edit_homework.setText("");
                }
            }
        });
    }

    private void findViews() {
        homework_history_list = (ListView) findViewById(R.id.homework_history_list);
        save_homework = (Button) findViewById(R.id.save_homework);
        edit_homework = (EditText) findViewById(R.id.edit_homework);
        homework_title = (TextView)findViewById(R.id.homework_title);
    }

}
