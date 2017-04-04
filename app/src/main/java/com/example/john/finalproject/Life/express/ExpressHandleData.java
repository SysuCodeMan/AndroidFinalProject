package com.example.john.finalproject.Life.express;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.john.finalproject.R;

public class ExpressHandleData {
    Context context;
    private BaseAdapter adapter;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    public ExpressHandleData(Context context){
        this.context = context;
        helper = new RecordSQLiteOpenHelper(context);
    }

    public void insertData(String tempName) {
        if(!hasData(tempName)){
            db = helper.getWritableDatabase();
            db.execSQL("insert into records(name) values('" + tempName + "')");
            db.close();
        }
    }

    public boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});

        return cursor.moveToNext();
    }

    //模糊查询数据
    public int queryData(String tempName, ListView listView) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);   // 创建adapter适配器对象
        adapter = new SimpleCursorAdapter(context, R.layout.item_search_record, cursor, new String[] { "name" },
                new int[] { R.id.tv_record_text }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        cursor.getCount();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return cursor.getCount();
    }

    public void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }
}
