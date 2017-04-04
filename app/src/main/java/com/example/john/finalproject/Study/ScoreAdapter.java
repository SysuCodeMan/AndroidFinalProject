package com.example.john.finalproject.Study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.john.finalproject.R;

import java.util.ArrayList;

/**
 * Created by Chen on 2016/12/4.
 */

public class ScoreAdapter extends BaseAdapter {
    private ArrayList<CourseScore> courseScores;
    private Context context;

    public ScoreAdapter(Context context) {
        this.context = context;
        courseScores = new ArrayList<CourseScore>();
    }

    public ArrayList<CourseScore> getCourseScores() {
        return courseScores;
    }

    public void UpdateData(ArrayList<CourseScore> courseScores) {
        this.courseScores.clear();
        for (int i = 0; i < courseScores.size(); i++) {
            this.courseScores.add(courseScores.get(i));
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return courseScores.size();
    }

    @Override
    public Object getItem(int position) {
        return courseScores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.score_listview_layout, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView credit = (TextView) convertView.findViewById(R.id.credit);
        TextView teacher = (TextView) convertView.findViewById(R.id.teacher);
        TextView score = (TextView) convertView.findViewById(R.id.score);
        TextView rank = (TextView) convertView.findViewById(R.id.rank);
        TextView gpa = (TextView) convertView.findViewById(R.id.gpa);

        CourseScore courseScore = courseScores.get(position);
        name.setText(courseScore.getName());
        credit.setText(courseScore.getCreadit());
        teacher.setText(courseScore.getTeacher());
        score.setText(courseScore.getScore());
        rank.setText(courseScore.getRank());
        gpa.setText(courseScore.getGpa());

        return convertView;
    }
}
