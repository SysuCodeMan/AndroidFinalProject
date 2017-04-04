package com.example.john.finalproject.Life.account;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.R;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SpecificData extends Activity {
	private Intent intent = null;
	private String title;
	AccountDBdao accountDBdao;

	private TextView mTextViewTime;

	private String time1;
	private String time2;
	private String time3;

	private CornerListView cornerListView = null;
	private List<Account> accounts;
	private LayoutInflater inflater;

	private EditText mEditTextMoney;
	private EditText mEditTextRemark;
	private TextView mEditTextTime;
	private Spinner mSpinnerType;
	private Button mButtonUpdate;
	private Button mButtonDelete;
	private Button mButtonCancel;

	private String time;
	private float money;
	private String type;
	private boolean earning;
	private String remark;
	private String id;
	private String name;

	private Account account;

	private static final String[] types = { "收入", "餐饮", "交通", "日用", "社交"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_data);

		intent = this.getIntent();
		name = intent.getStringExtra("name");
		title = intent.getStringExtra("title");

		mTextViewTime = (TextView) this
				.findViewById(R.id.tv_specific_data_txtDataRange);
		mTextViewTime.setText(title);

		accountDBdao = new AccountDBdao(getApplicationContext());

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		time1 = year + "/" + month + "/" + day;
		time2 = year + "/" + month + "%";
		time3 = year + "%";

		inflater = LayoutInflater.from(this);
		cornerListView = (CornerListView) findViewById(R.id.lv_specific_data_list);
		//GetData();

		try {
			if (title.equals("收入")) {
				accounts = accountDBdao.findTotalIntoByName(name);
			} else if (title.equals("餐饮")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "餐饮");
			} else if (title.equals("交通")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "交通");
			} else if (title.equals("日用")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "日用");
			} else if (title.equals("社交")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "社交");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		cornerListView.setAdapter(new MyAdapter());

		cornerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				Account account = accounts.get(arg2);
				id = account.getId()+"";
				LayoutInflater factory = LayoutInflater.from(SpecificData.this);
				final View dialog_view = factory.inflate(R.layout.activity_more_action, null);
				final AlertDialog.Builder builder = new AlertDialog.Builder(SpecificData.this);
				builder.setTitle("修改账单信息");
				builder.setView(dialog_view);


				mEditTextMoney = (EditText)dialog_view.findViewById(R.id.et_moreaction_money);
				mEditTextRemark = (EditText)dialog_view.findViewById(R.id.et_moreaction_remark);
				mEditTextTime = (TextView) dialog_view.findViewById(R.id.et_moreaction_time);

				mSpinnerType = (Spinner)dialog_view.findViewById(R.id.sp_moreaction_type);

				ArrayAdapter<String> adapterType = new ArrayAdapter<String>(SpecificData.this,
						R.layout.addnodes_earnings, types);

				adapterType
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				mSpinnerType.setAdapter(adapterType);

				mButtonUpdate = (Button)dialog_view.findViewById(R.id.bt_moreaction_update);
				mButtonDelete = (Button) dialog_view.findViewById(R.id.bt_moreaction_delete);
				mButtonCancel = (Button) dialog_view.findViewById(R.id.bt_moreaction_cancel);

				accountDBdao = new AccountDBdao(getApplicationContext());
				account = accountDBdao.findInfoById(id);
				mEditTextMoney.setText(account.getMoney() + "");
				mEditTextRemark.setText(account.getRemark());
				mEditTextTime.setText(account.getTime());

				if (account.getType().equals("收入")) {
					mSpinnerType.setSelection(0);
				} else if (account.getType().equals("餐饮")) {
					mSpinnerType.setSelection(1);
				} else if (account.getType().equals("交通")) {
					mSpinnerType.setSelection(2);
				} else if (account.getType().equals("日用")) {
					mSpinnerType.setSelection(3);
				} else {
					mSpinnerType.setSelection(4);
				}


				builder.create();
				final AlertDialog alertDialog = builder.show();

				mButtonUpdate.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mEditTextMoney.getText().toString().trim().equals("")) {
							Toast.makeText(getApplicationContext(), "金额不能为空", Toast.LENGTH_SHORT).show();
						} else {
							money = Float.parseFloat(mEditTextMoney.getText().toString()
									.trim());
						}
						time = mEditTextTime.getText().toString().trim();
						type = mSpinnerType.getSelectedItem().toString();
						if (mSpinnerType.getSelectedItem().toString().equals("收入")) {
							earning = true;
						} else {
							earning = false;
						}
						remark = mEditTextRemark.getText().toString().trim();

						accountDBdao = new AccountDBdao(getApplicationContext());
						accountDBdao.update(id, time, money, type, earning, remark);
						Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
						finish();


					}
				});
				mButtonDelete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						accountDBdao = new AccountDBdao(getApplicationContext());
						accountDBdao.delete(id);
						Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
						finish();
					}
				});
				mButtonCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(SpecificData.this, SpecificData.class);
						intent.putExtra("name", name);
						intent.putExtra("title", title);
						startActivity(intent);
						alertDialog.dismiss();
					}
				});


			}
		});
	}


	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return accounts.size();
		}

		@Override
		public Object getItem(int position) {
			return accounts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.specific_data_data, null);
			Account account = accounts.get(position);
			TextView tv_text1 = (TextView) view
					.findViewById(R.id.ls_sp_tv_time);
			TextView tv_text2 = (TextView) view
					.findViewById(R.id.ls_sp_tv_money);
			TextView tv_text3 = (TextView) view.findViewById(R.id.ls_sp_tv_remark);
			tv_text1.setText(account.getTime());
			tv_text2.setText(account.getMoney() + "");
			tv_text3.setText(account.getRemark() + "");


			return view;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		mTextViewTime.setText(title);

		accountDBdao = new AccountDBdao(getApplicationContext());

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		time1 = year + "/" + month + "/" + day;
		time2 = year + "/" + month + "%";
		time3 = year + "%";

		inflater = LayoutInflater.from(this);

		cornerListView.setAdapter(new MyAdapter());
		
		//GetData();
		try {
			if (title.equals("收入")) {
				accounts = accountDBdao.findTotalIntoByName(name);
			} else if (title.equals("餐饮")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "餐饮");
			} else if (title.equals("交通")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "交通");
			} else if (title.equals("日用")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "日用");
			} else if (title.equals("社交")) {
				accounts = accountDBdao.findTotalTypeOutByName(name, "社交");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解决转屏问题
	@Override
	public void onConfigurationChanged(Configuration newConfiguration) {
		super.onConfigurationChanged(newConfiguration);
		if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else {

		}
	}

}
