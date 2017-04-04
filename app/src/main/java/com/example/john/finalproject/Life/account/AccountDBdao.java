package com.example.john.finalproject.Life.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class AccountDBdao {
	private Context context;
	MyDBOpenHelper dbOpenHelper;

	public AccountDBdao(Context context) {
		this.context = context;
		dbOpenHelper = new MyDBOpenHelper(context);
	}

	public void add(String time, float money, String type, boolean earnings,
			String remark, String name) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("time", time);
			values.put("money", money);
			values.put("type", type);
			values.put("earnings", earnings);
			values.put("remark", remark);
			values.put("name", name);
			db.insert("account", null, values);
			db.close();
		}
	}

	public void delete(String accountid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete("account", "accountid=?", new String[] { accountid });
			db.close();
		}
	}

	public void update(String accountid, String time, float money, String type,
			boolean earnings, String remark) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("time", time);
			values.put("money", money);
			values.put("type", type);
			values.put("earnings", earnings);
			values.put("remark", remark);
			db.update("account", values, "accountid=?",
					new String[] { accountid });
			db.close();
		}
	}


	public List<Account> findTotalIntoByName(String userName) {
		List<Account> accounts = null;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from account where earnings=1 and name=?",
					new String[] { userName });
			accounts = new ArrayList<Account>();
			while (cursor.moveToNext()) {
				Account account = new Account();
				int id = cursor.getInt(cursor.getColumnIndex("accountid"));
				account.setId(id);
				String name = cursor.getString(cursor.getColumnIndex("name"));
				account.setName(name);
				float money = Float.parseFloat(cursor.getString(cursor
						.getColumnIndex("money")));
				account.setMoney(money);
				String time = cursor.getString(cursor.getColumnIndex("time"));
				account.setTime(time);
				String type = cursor.getString(cursor.getColumnIndex("type"));
				account.setType(type);
				long earnings = cursor.getLong(cursor
						.getColumnIndex("earnings"));
				if (earnings == 0) {
					account.setEarnings(false);
				} else {
					account.setEarnings(true);
				}
				String remark = cursor.getString(cursor
						.getColumnIndex("remark"));
				account.setRemark(remark);
				accounts.add(account);
			}
			cursor.close();
			db.close();
		}
		return accounts;
	}

	public List<Account> findTotalTypeOutByName(String userName, String currentType) {
		List<Account> accounts = null;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from account where earnings=0 and name=? and type=?",
					new String[] { userName, currentType });
			accounts = new ArrayList<Account>();
			while (cursor.moveToNext()) {
				Account account = new Account();
				int id = cursor.getInt(cursor.getColumnIndex("accountid"));
				account.setId(id);
				String name = cursor.getString(cursor.getColumnIndex("name"));
				account.setName(name);
				float money = Float.parseFloat(cursor.getString(cursor
						.getColumnIndex("money")));
				account.setMoney(money);
				String time = cursor.getString(cursor.getColumnIndex("time"));
				account.setTime(time);
				String type = cursor.getString(cursor.getColumnIndex("type"));
				account.setType(type);
				long earnings = cursor.getLong(cursor
						.getColumnIndex("earnings"));
				if (earnings == 0) {
					account.setEarnings(false);
				} else {
					account.setEarnings(true);
				}
				String remark = cursor.getString(cursor
						.getColumnIndex("remark"));
				account.setRemark(remark);
				accounts.add(account);
			}
			cursor.close();
			db.close();
		}
		return accounts;
	}

	public float fillTotalInto(String name) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=1 and name=?",
							new String[] { name });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}


	public float fillTotalOut(String name) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=0 and name=?",
							new String[] { name });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}

	public float fillTypeOut(String name, String type) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=0 and name=? and type=?",
							new String[] { name, type });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}

	public float fillTodayTypeOut(String name, String type, String time) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=0 and name=? and type=? and time=?",
							new String[] { name, type, time });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}


	public float fillTypeIn(String name, String type) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=1 and name=? and type=?",
							new String[] { name, type });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}

	public float fillTodayTypeIn(String name, String type, String time) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=1 and name=? and type=? and time=?",
							new String[] { name, type, time });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}


	public float fillTodayOut(String name, String time) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=0 and name=? and time=?",
							new String[] { name, time });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}

	public float fillTodayInto(String name, String time) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select sum(money) as sumvalue from account where earnings=1 and name=? and time=?",
							new String[] { name, time });
			while (cursor.moveToNext()) {
				return cursor.getFloat(cursor.getColumnIndex("sumvalue"));
			}
			cursor.close();
			db.close();
		}
		return 0;
	}

	public Account findInfoById(String accountid) {
		Account account = null;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from account where accountid=?",
					new String[] { accountid });
			while (cursor.moveToNext()) {
				account = new Account();
				int id = cursor.getInt(cursor.getColumnIndex("accountid"));
				account.setId(id);
				String name = cursor.getString(cursor.getColumnIndex("name"));
				account.setName(name);
				float money = Float.parseFloat(cursor.getString(cursor
						.getColumnIndex("money")));
				account.setMoney(money);
				String time = cursor.getString(cursor.getColumnIndex("time"));
				account.setTime(time);
				String type = cursor.getString(cursor.getColumnIndex("type"));
				account.setType(type);
				long earnings = cursor.getLong(cursor
						.getColumnIndex("earnings"));
				if (earnings == 0) {
					account.setEarnings(false);
				} else {
					account.setEarnings(true);
				}
				String remark = cursor.getString(cursor
						.getColumnIndex("remark"));
				account.setRemark(remark);
			}
			cursor.close();
			db.close();
		}
		return account;
	}
}
