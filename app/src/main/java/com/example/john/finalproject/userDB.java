package com.example.john.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * Created by xia on 2016/11/27.
 */
public class userDB extends SQLiteOpenHelper {
    private static final String USER_TABLE_NAME = "user";
    private static final int version = 1;

    public userDB(Context context) {
        super(context, USER_TABLE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +USER_TABLE_NAME
                +" (_id INTEGER PRIMARY KEY,username TEXT,password TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {

    }

    public void insertData(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        db.insert(USER_TABLE_NAME, null, cv);
        db.close();
    }

    public void deleteData(String username) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username=?";
        String[] whereArgs = {username};
        db.delete(USER_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public boolean queryName(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String temp = cursor.getString(cursor.getColumnIndex("username"));
                if (TextUtils.equals(temp.toString(), username.toString())) {
                    db.close();
                    return false;
                }
                cursor.moveToNext();
            }
        }
        db.close();
        return true;
    }

    public Boolean confirmUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String temp1 = cursor.getString(cursor.getColumnIndex("username"));
                String temp2 = cursor.getString(cursor.getColumnIndex("password"));
                if (TextUtils.equals(temp1.toString(), username.toString())
                        && TextUtils.equals(temp2.toString(), password.toString())) {
                    db.close();
                    return true;
                }
                cursor.moveToNext();
            }
        }
        db.close();
        return false;
    }

    public void updateData(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", password);
        String whereClause = "username=?";
        String[] whereArgs = {username};
        db.update(USER_TABLE_NAME, cv, whereClause, whereArgs);
        db.close();
    }
}
