package com.example.john.finalproject.Life.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.Life.express.LifeActivityExpress;
import com.example.john.finalproject.MainActivity;
import com.example.john.finalproject.R;
import com.example.john.finalproject.Sport.SportActivity;
import com.example.john.finalproject.Study.StudyActivity;
import com.example.john.finalproject.userDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class LifeActivity extends AppCompatActivity {
    private DrawerLayout life_drawer_layout;
    private ListView life_left_drawer;
    private ImageButton life_sport_button;
    private ImageButton life_life_button;
    private ImageButton life_study_button;
    private ImageButton account;
    private ImageButton express;
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String username;

    private Intent intent = null;
    AccountDBdao accountDBdao;

    private TextView mTextViewTime;
    private Button mButtonAddNodes;

    private Button cancel;
    private Button add;

    private int varlue1;
    private int varlue2;

    private float totalOut;
    private float totalInto;

    private float todayTotalOut;

    private LayoutInflater inflater;

    private boolean flag = true;

    private EditText mEditTextMoney;
    private EditText mEditTextRemark;
    private TextView mEditTextTime;
    private Spinner mSpinnerType;

    private String time;
    private float money;
    private String type;
    private boolean earning;
    private String remark;
    private float income;
    private float eating;
    private float transport;
    private float commodity;
    private float social;

    private boolean open_dialog = false;

    private static final String[] types = { "收入", "餐饮", "交通", "日用", "社交"};

    int pictures[] = {R.mipmap.expenditure, R.mipmap.eating, R.mipmap.transport, R.mipmap.commodity, R.mipmap.social};
    List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
    Map<String, Object>listItem;
    private SimpleAdapter simpleAdapter;

    private float today_income;
    private float today_eating;
    private float today_transport;
    private float today_commodity;
    private float today_social;

    private  ListView listview;

    //private final float[] record = {income, eating, transport, commodity, social};
    private final String[] record = {String.valueOf(today_income), String.valueOf(today_eating),
            String.valueOf(today_transport), String.valueOf(today_commodity), String.valueOf(today_social)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_layout);
        flag = true;
        findView();
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);

        accountDBdao = new AccountDBdao(getApplicationContext());

        totalOut = accountDBdao.fillTotalOut(username);
        totalInto = accountDBdao.fillTotalInto(username);

        time = GetTime();
        todayTotalOut = accountDBdao.fillTodayOut(username, time);

        income = accountDBdao.fillTypeIn(username, "收入");
        eating = 0 - accountDBdao.fillTypeOut(username, "餐饮");
        transport = 0 - accountDBdao.fillTypeOut(username, "交通");
        commodity = 0 - accountDBdao.fillTypeOut(username, "日用");
        social = 0 - accountDBdao.fillTypeOut(username, "社交");

        today_income = accountDBdao.fillTodayTypeIn(username, "收入", time);
        today_eating = 0 - accountDBdao.fillTodayTypeOut(username, "餐饮", time);
        today_transport = 0 - accountDBdao.fillTodayTypeOut(username, "交通", time);
        today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, "日用", time);
        today_social = 0 - accountDBdao.fillTodayTypeOut(username, "社交", time);

        if (today_income > 0) {
            record[0] = "+" + String.valueOf(today_income);
        } else {
            record[0] = String.valueOf(today_income);
        }
        record[1] = String.valueOf(today_eating);
        record[2] = String.valueOf(today_transport);
        record[3] = String.valueOf(today_commodity);
        record[4] = String.valueOf(today_social);

        for(int i = 0; i < types.length; i++){
            listItem = new HashMap<String,Object>();
            listItem.put("pictures", pictures[i]);
            listItem.put("types", types[i]);
            listItem.put("record", record[i]);
            //listItem.put("record", record[i]);
            data.add(listItem);
        }

        simpleAdapter = new SimpleAdapter(this, data, R.layout.item, new String[]{"pictures", "types", "record"},
                new int[]{R.id.pictures, R.id.text, R.id.number});
        listview.setAdapter(simpleAdapter);

        life_sport_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeActivity.this, SportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        life_life_button.setClickable(false);
        life_study_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeActivity.this, StudyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        //  点击记账按钮跳转到生活界面默认记账页面
        account.setClickable(false);
        //  进入到快递查询页面，设置该按钮点击事件无效
        express.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeActivity.this, LifeActivityExpress.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        if (username == null) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            mTextViewTime.setText(GetTime());

            accountDBdao = new AccountDBdao(getApplicationContext());
            totalOut = accountDBdao.fillTotalOut(username);
            totalInto = accountDBdao.fillTotalInto(username);

            todayTotalOut = accountDBdao.fillTodayOut(username, time);

            SetValue(totalOut, totalInto);

            inflater = LayoutInflater.from(this);


            mButtonAddNodes = (Button) this.findViewById(R.id.bt_main_addnotes);
        }

        mButtonAddNodes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                open_dialog = true;
                LayoutInflater factory = LayoutInflater.from(LifeActivity.this);
                final View dialog_view = factory.inflate(R.layout.activity_addnodes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(LifeActivity.this);
                builder.setTitle("记一笔");
                builder.setView(dialog_view);
                mEditTextMoney = (EditText) dialog_view.findViewById(R.id.et_money);
                mEditTextRemark = (EditText)dialog_view.findViewById(R.id.et_remark);
                mEditTextTime = (TextView) dialog_view.findViewById(R.id.et_add_time);

                add = (Button)dialog_view.findViewById(R.id.add_button);
                cancel = (Button)dialog_view.findViewById(R.id.cancel_button);

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                time = year + "/" + month + "/" + day;
                mEditTextTime.setText(time);


                mSpinnerType = (Spinner)dialog_view.findViewById(R.id.sp_type);

                ArrayAdapter<String> adapterType = new ArrayAdapter<String>(LifeActivity.this,
                        R.layout.addnodes_earnings, types);

                adapterType
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mSpinnerType.setAdapter(adapterType);


                builder.create();
                final AlertDialog alertDialog = builder.show();

                add.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mEditTextMoney.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "金额不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            money = Float.parseFloat(mEditTextMoney.getText().toString()
                                    .trim());

                            time = mEditTextTime.getText().toString().trim();
                            type = mSpinnerType.getSelectedItem().toString();


                            if (mSpinnerType.getSelectedItem().toString().equals("收入")) {
                                earning = true;
                            } else {
                                earning = false;
                            }

                            remark = mEditTextRemark.getText().toString().trim();

                            accountDBdao = new AccountDBdao(getApplicationContext());
                            accountDBdao.add(time, money, type, earning, remark, username);

                            totalOut = accountDBdao.fillTotalOut(username);
                            totalInto = accountDBdao.fillTotalInto(username);
                            todayTotalOut = accountDBdao.fillTodayOut(username, time);


                            SetValue(totalInto, totalOut);

                            if (mSpinnerType.getSelectedItem().toString().equals("收入")) {
                                income = accountDBdao.fillTypeIn(username, type);
                                today_income = accountDBdao.fillTodayTypeIn(username, type, time);
                                record[0] = "+" + String.valueOf(today_income);
                            }
                            else if (mSpinnerType.getSelectedItem().toString().equals("餐饮")) {
                                eating = 0 - accountDBdao.fillTypeOut(username, type);
                                today_eating = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[1] = String.valueOf(today_eating);
                            }
                            else if (mSpinnerType.getSelectedItem().toString().equals("交通")) {
                                transport =  0 - accountDBdao.fillTypeOut(username, type);
                                today_transport = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[2] = String.valueOf(today_transport);

                            }
                            else if (mSpinnerType.getSelectedItem().toString().equals("日用")) {
                                commodity = 0 - accountDBdao.fillTypeOut(username, type);
                                today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[3] = String.valueOf(today_commodity);
                            }
                            else {
                                social = 0 - accountDBdao.fillTypeOut(username, type);
                                today_social = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[4] = String.valueOf(today_social);

                            }

                            for(int i = 0; i < types.length; i++){
                                data.remove(0);
                                listItem = new HashMap<String,Object>();
                                listItem.put("pictures", pictures[i]);
                                listItem.put("types", types[i]);
                                listItem.put("record", record[i]);
                                data.add(listItem);
                            }
                            simpleAdapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), "添加账单条目成功", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            if (todayTotalOut > 200 && flag == true) {
                                Intent intent = new Intent("com.example.john.finalproject.Life.account.StaticReceiver");
                                Bundle bundle = new Bundle();
                                bundle.putFloat("totalOut", totalOut);
                                intent.putExtras(bundle);
                                sendBroadcast(intent);
                                flag = false;
                                //Toast.makeText(LifeActivity.this, "work", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

        setDrawer();

        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        intent = new Intent(LifeActivity.this, SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "收入");
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(LifeActivity.this, SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "餐饮");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(LifeActivity.this, SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "交通");
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(LifeActivity.this, SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "日用");
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(LifeActivity.this, SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "社交");
                        startActivity(intent);
                        break;
                }
            }
        });


    }


    public String GetTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String time = year + "/" + month + "/" + day;
        return time;
    }

    public void SetValue(float totalInto, float totalOut) {

        if ((totalInto - totalOut) < 0) {
            varlue1 = 0;
            varlue2 = 1;
        } else if (totalInto == totalOut) {
            varlue1 = 1;
            varlue2 = 1;
        } else {
            varlue1 = (int) totalInto;
            varlue2 = (int) totalOut;
        }

    }

    private void findView() {
        life_drawer_layout = (DrawerLayout) findViewById(R.id.life_drawer_layout);
        life_left_drawer = (ListView) findViewById(R.id.life_left_drawer);
        life_sport_button = (ImageButton) findViewById(R.id.life_sport_button);
        life_life_button = (ImageButton) findViewById(R.id.life_life_button);
        life_study_button = (ImageButton) findViewById(R.id.life_study_button);
        account = (ImageButton)findViewById(R.id.account);
        express = (ImageButton)findViewById(R.id.express);
        mTextViewTime = (TextView) this.findViewById(R.id.tv_main_time);
        listview = (ListView)findViewById(R.id.list);
        cancel = (Button)findViewById(R.id.cancel_button);
        add = (Button)findViewById(R.id.add_button);
    }

    private void setDrawer() {
        final List<Map<String, Object>> data = new ArrayList<>();
        int[] icons = new int[] {R.mipmap.user_icon,R.mipmap.accountpig, R.mipmap.edit_icon, R.mipmap.logout_icon};
        String[] letters = new String[] {username,"消费占比", "修改密码", "退出登录"};
        for (int i = 0; i < 4; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("drawer_icon", icons[i]);
            temp.put("drawer_button", letters[i]);
            data.add(temp);
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[] {"drawer_icon", "drawer_button"}, new int[] {R.id.drawer_icon, R.id.drawer_button});
        life_left_drawer.setAdapter(simpleAdapter);
        life_left_drawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("totalOut", totalOut);
                    bundle.putFloat("totalInto", totalInto);
                    bundle.putFloat("income", income);
                    bundle.putFloat("eating", eating);
                    bundle.putFloat("transport", transport);
                    bundle.putFloat("commdity", commodity);
                    bundle.putFloat("social", social);
                    intent.putExtras(bundle);

                    intent.setClass(LifeActivity.this, Chart.class);
                    startActivity(intent);
                } else if (i == 2) {
                    LayoutInflater factory = LayoutInflater.from(LifeActivity.this);
                    final View dialog_view = factory.inflate(R.layout.password_dialog_layout, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LifeActivity.this);
                    builder.setTitle("修改密码");
                    builder.setView(dialog_view);
                    TextView password_dialog_username = (TextView) dialog_view.findViewById(R.id.password_dialog_username);
                    password_dialog_username.setText(username);
                    final EditText password_dialog_old_password = (EditText) dialog_view.findViewById(R.id.password_dialog_old_password);
                    final EditText password_dialog_new_password = (EditText) dialog_view.findViewById(R.id.password_dialog_new_password);
                    Button password_dialog_quit_button = (Button) dialog_view.findViewById(R.id.password_dialog_quit_button);
                    Button password_dialog_save_button = (Button) dialog_view.findViewById(R.id.password_dialog_save_button);
                    builder.create();
                    final AlertDialog alertDialog = builder.show();
                    password_dialog_quit_button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    password_dialog_save_button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            userDB userdb = new userDB(LifeActivity.this);
                            if (TextUtils.isEmpty(password_dialog_old_password.getText().toString())) {
                                Toast.makeText(LifeActivity.this, "原密码为空", Toast.LENGTH_SHORT).show();
                            }
                            else if (TextUtils.isEmpty(password_dialog_new_password.getText().toString())) {
                                Toast.makeText(LifeActivity.this, "新密码为空", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (userdb.confirmUser(username, password_dialog_old_password.getText().toString())) {
                                    userdb.updateData(username, password_dialog_new_password.getText().toString());
                                    alertDialog.dismiss();
                                    Toast.makeText(LifeActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LifeActivity.this, "原密码输入错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else if (i == 3) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LifeActivity.this);
                    builder.setMessage("确定退出登录？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.putString("state", "logout");
                            editor.commit();
                            Toast.makeText(LifeActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LifeActivity.this,  MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextViewTime.setText(GetTime());

        totalOut = accountDBdao.fillTotalOut(username);
        totalInto = accountDBdao.fillTotalInto(username);
        SetValue(totalInto, totalOut);
        todayTotalOut = accountDBdao.fillTodayOut(username, GetTime());

        today_income = accountDBdao.fillTodayTypeIn(username, "收入", time);
        today_eating = 0 - accountDBdao.fillTodayTypeOut(username, "餐饮", time);
        today_transport = 0 - accountDBdao.fillTodayTypeOut(username, "交通", time);
        today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, "日用", time);
        today_social = 0 - accountDBdao.fillTodayTypeOut(username, "社交", time);

        if (today_income > 0) {
            record[0] = "+" + String.valueOf(today_income);
        } else {
            record[0] = String.valueOf(today_income);
        }
        record[1] = String.valueOf(today_eating);
        record[2] = String.valueOf(today_transport);
        record[3] = String.valueOf(today_commodity);
        record[4] = String.valueOf(today_social);

        for(int i = 0; i < types.length; i++){
            data.remove(0);
            listItem = new HashMap<String,Object>();
            listItem.put("pictures", pictures[i]);
            listItem.put("types", types[i]);
            listItem.put("record", record[i]);
            data.add(listItem);
        }
        simpleAdapter.notifyDataSetChanged();


        if (todayTotalOut > 200 && flag == true) {
            Intent intent = new Intent("com.example.john.finalproject.Life.account.StaticReceiver");
            Bundle bundle = new Bundle();
            bundle.putFloat("totalOut", totalOut);
            intent.putExtras(bundle);
            sendBroadcast(intent);
            flag = false;
            //Toast.makeText(LifeActivity.this, "work", Toast.LENGTH_SHORT).show();
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
