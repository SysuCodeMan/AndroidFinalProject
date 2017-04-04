package com.example.john.finalproject.Study;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by Chen on 2016/12/14.
 */

public class courseDB extends SQLiteOpenHelper {
    private static final String COURSE_TABLE_NAME="course";
    private static final int version = 1;
    public courseDB(Context context) {
        super(context, COURSE_TABLE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + COURSE_TABLE_NAME
                +" (_id INTEGER PRIMARY KEY,"+
                    "username TEXT, name TEXT, classroom TEXT, time TEXT, period TEXT, length INTEGER, myindex INTEGER, place INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void UpdateRecord(String username, ArrayList<Course> courses) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username=?";
        String[] whereArgs = {username};
        db.delete(COURSE_TABLE_NAME, whereClause, whereArgs);
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", username);
            contentValues.put("name", course.getName());
            contentValues.put("classroom", course.getClassroom());
            contentValues.put("time", course.getTime());
            contentValues.put("period", course.getPeriod());
            contentValues.put("length", course.getLength());
            contentValues.put("myindex", course.getIndex());
            contentValues.put("place", course.getPlace());
            db.insert(COURSE_TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    public ArrayList<Course> queryCourses(String username) {
        ArrayList<Course> courses = new ArrayList<Course>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(COURSE_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String resultusername = cursor.getString(cursor.getColumnIndex("username"));
                if (TextUtils.equals(resultusername, username)) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String period = cursor.getString(cursor.getColumnIndex("period"));
                    int length = cursor.getInt(cursor.getColumnIndex("length"));
                    int index = cursor.getInt(cursor.getColumnIndex("myindex"));
                    int place = cursor.getInt(cursor.getColumnIndex("place"));
                    courses.add(new Course(name, classroom, time, period, length, index,place));
                }
                cursor.moveToNext();
            }
        }

        db.close();
        return courses;
    }
}
