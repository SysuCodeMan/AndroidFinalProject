package com.example.john.finalproject.Sport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Willo on 2016/12/13.
 */

public class stepCountDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "stepcount";
    private static final String TABLE_NAME = "steprecord";
    private static final int DB_VERSION = 1;
    public stepCountDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME
                +" (_id INTEGER PRIMARY KEY, name TEXT, month TEXT, day TEXT, count, TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert2DB( String name,String month, String day, String count) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("month", month);
        cv.put("day", day);
        cv.put("count", count);
        db.insert(TABLE_NAME,null,cv);
        db.close();
    }

    public  void update2DB(String name, String month, String day, String count) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("month", month);
        cv.put("day", day);
        cv.put("count", count);
        String whereClause = "name=? and month=? and day=?";
        String[] whereArgs = {name, month, day};
        db.update(TABLE_NAME,cv, whereClause,whereArgs);
        db.close();
    }

    public void deleteFromDB(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "name="+name, null);
        db.close();
    }
    public int getSize() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {"name"},
                null, null, null, null, null );
        int count = cursor.getCount();
        return count;
    }

    public boolean checkExist(String name,String month, String day) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name=? and month=? and day=?";
        String[] whereArgs = {name,month, day};
        Cursor cursor = db.query(TABLE_NAME, new String[]{"name", "month", "day"},
                whereClause, whereArgs, null, null, null );

        //Cursor cursor = db.rawQuery("Select* From steprecord Where steprecord.name = " + '"'+name+'"', null);
        int result = cursor.getCount();
        //db.close();
        return result>0;
    }

    public List<stepRecord> query(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name=?";
        String[] whereArgs = {name};
        List<stepRecord> records = new LinkedList<>();
        Cursor cursor = db.query(TABLE_NAME, new String[] {"month", "day", "count"},
                whereClause, whereArgs, null, null, null );
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            return records;
        }
        for (int i = 0 ; i <  count; i++) {
            stepRecord record = new stepRecord();
            //String _id = cursor.getString(cursor.getColumnIndex("_id"));
            //String name = cursor.getString(cursor.getColumnIndex("name"));
            record.month = cursor.getString(cursor.getColumnIndex("month"));
            record.day = cursor.getString(cursor.getColumnIndex("day"));
            record.count = cursor.getString(cursor.getColumnIndex("count"));
            records.add(record);
            cursor.moveToNext();
        }
        db.close();
        return records;
    }

}
