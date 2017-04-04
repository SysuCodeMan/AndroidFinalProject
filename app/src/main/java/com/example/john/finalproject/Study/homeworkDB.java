package com.example.john.finalproject.Study;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by xia on 2016/12/7.
 */
public class homeworkDB extends SQLiteOpenHelper{
    private static final String HOMEWORK_TABLE_NAME = "homework";
    private static final int version = 1;

    public homeworkDB(Context context) {
        super(context, HOMEWORK_TABLE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +HOMEWORK_TABLE_NAME
                +" (_id INTEGER PRIMARY KEY," +
                    "username TEXT,course TEXT,hw_description TEXT, isFinished TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {

    }

    public void insertHomework(String course, boolean IsFinished, String hw_description, String username) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String isFinished = boolToString(IsFinished);
        cv.put("username", username);
        cv.put("course", course);
        cv.put("hw_description", hw_description);
        cv.put("isFinished", isFinished);
        db.insert(HOMEWORK_TABLE_NAME, null, cv);
        db.close();
    }

    public ArrayList<Homework> queryHomeworks(String course, String username) {
        ArrayList<Homework> homeworks = new ArrayList<Homework>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(HOMEWORK_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String temp_course = cursor.getString(cursor.getColumnIndex("course"));
                String temp_username = cursor.getString(cursor.getColumnIndex("username"));
                if (TextUtils.equals(temp_course, course) && TextUtils.equals(username, temp_username)) {
                    String temp_descri = cursor.getString(cursor.getColumnIndex("hw_description"));
                    String temp_finished = cursor.getString(cursor.getColumnIndex("isFinished"));
                    boolean isfinished = stringToBool(temp_finished);
                    homeworks.add(new Homework(isfinished, temp_descri));
                }
                cursor.moveToNext();
            }
        }

        db.close();
        return homeworks;
    }


    public void deleteHomework(String username, String course, String hw_descri) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username=? and course=? and hw_description=?";
        String[] whereArgs = {username, course, hw_descri};
        db.delete(HOMEWORK_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void updateHomework(String username, String course, String hw_descri, boolean IsFinished) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String isFinished = boolToString(IsFinished);
        cv.put("isFinished", isFinished);
        String whereClause = "username=? and course=? and hw_description=?";
        String[] whereArgs = {username, course, hw_descri};
        db.update(HOMEWORK_TABLE_NAME, cv, whereClause, whereArgs);
        db.close();
    }

    private Boolean stringToBool(String a) {
        if (TextUtils.equals(a, "true")) return true;
        return false;
    }

    private String boolToString(boolean a) {
        if (a == true) return "true";
        return "false";
    }
}
