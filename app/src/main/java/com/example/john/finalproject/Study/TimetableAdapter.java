package com.example.john.finalproject.Study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.john.finalproject.R;

import java.util.ArrayList;

/**
 * Created by Chen on 2016/12/5.
 */

public class TimetableAdapter extends BaseAdapter {
    private ArrayList<Course> courses;
    private Context context;
    private String username;
    courseDB db;

    public TimetableAdapter(Context context, String username) {
        this.context = context;
        this.username = username;
        db = new courseDB(context);
        courses = db.queryCourses(username);
    }

    public void UpdateTimeTable(ArrayList<Course> courses) {
        this.courses.clear();
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            this.courses.add(course);
        }
        db.UpdateRecord(username, courses);
        notifyDataSetChanged();
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }


    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView;
        ViewHolder viewHolder;
        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.course_item, null);
            viewHolder = new ViewHolder();
            viewHolder.courseLinear = (LinearLayout)convertView.findViewById(R.id.courseLinear);
            viewHolder.courseName = (TextView)convertView.findViewById(R.id.courseName);
            viewHolder.courseTime = (TextView) convertView.findViewById(R.id.courseTime);
            viewHolder.courseClassroom = (TextView) convertView.findViewById(R.id.courseClassroom);
            viewHolder.coursePeriod = (TextView) convertView.findViewById(R.id.coursePeriod);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Course course = courses.get(position);
        if (course.getLength()!=0) {
            if (course.getPlace() != -1) {
                if (course.getPlace() == 0) {
                    viewHolder.courseLinear.setBackground(convertView.getResources().getDrawable(R.drawable.tableitem_top));
                    viewHolder.courseLinear.setZ(1.0f);
                }
                else if (course.getPlace() == (course.getLength() - 1)) {
                    viewHolder.courseLinear.setBackground(convertView.getResources().getDrawable(R.drawable.tableitem_bottom));
                    viewHolder.courseLinear.setZ(0);
                }
                else {
                    viewHolder.courseLinear.setBackground(convertView.getResources().getDrawable(R.drawable.tableitem_middle));
                    viewHolder.courseLinear.setZ(0);
                }
            }
        } else {
            viewHolder.courseLinear.setBackground(null);
            viewHolder.courseLinear.setZ(0);
        }

        viewHolder.courseName.setText(course.getName());
        viewHolder.courseClassroom.setText(course.getClassroom());
        viewHolder.courseTime.setText(course.getTime());
        viewHolder.coursePeriod.setText(course.getPeriod());
        return convertView;
    }
    private class ViewHolder {
        public LinearLayout courseLinear;
        public TextView courseName;
        public TextView courseTime;
        public TextView courseClassroom;
        public TextView coursePeriod;
    }
}
