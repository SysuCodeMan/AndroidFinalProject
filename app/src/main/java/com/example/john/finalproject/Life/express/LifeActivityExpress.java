package com.example.john.finalproject.Life.express;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.finalproject.Life.account.LifeActivity;
import com.example.john.finalproject.MainActivity;
import com.example.john.finalproject.R;
import com.example.john.finalproject.Sport.SportActivity;
import com.example.john.finalproject.Study.StudyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by limin on 2016/12/8.
 */
public class LifeActivityExpress extends BaseActivity implements View.OnClickListener {
    private DrawerLayout life_drawer_layout;
    private ListView life_left_drawer;
    private ImageButton life_sport_button;
    private ImageButton life_life_button;
    private ImageButton life_study_button;
    private ImageButton account;
    private ImageButton express;
    private String username;
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private EditText number;
    private Button search;
    private boolean canVis = true;  //判断当前历史记录能不能显示
    private Button btnTrue;

    private EditText etExpressNum;
    private TextView tvSearchTip;

    private ScrollView svSearchList;
    private ScrollListView lvSearchResult;

    private TextView tvClear;
    private ExpressHandleData handleData;  //处理搜索记录

    private ListView lvExpressData;

    @Override
    public void setContentView() {
        setContentView(R.layout.life_layout_express);
    }

    @Override
    public void initViews() {
        btnTrue = (Button) findViewById(R.id.btn_true);
        etExpressNum = (EditText) findViewById(R.id.et_express_num);

        svSearchList = (ScrollView) findViewById(R.id.sv_search_list);
        tvSearchTip = (TextView) findViewById(R.id.tv_search_tip);
        lvSearchResult = (ScrollListView) findViewById(R.id.lv_search_result);
        tvClear = (TextView) findViewById(R.id.tv_clear);
        lvExpressData = (ListView) findViewById(R.id.lv_express_data);

        life_drawer_layout = (DrawerLayout) findViewById(R.id.life_drawer_layout);
        life_left_drawer = (ListView) findViewById(R.id.life_left_drawer);
        life_sport_button = (ImageButton) findViewById(R.id.life_sport_button);
        life_life_button = (ImageButton) findViewById(R.id.life_life_button);
        life_study_button = (ImageButton) findViewById(R.id.life_study_button);
        account = (ImageButton) findViewById(R.id.account);
        express = (ImageButton) findViewById(R.id.express);
    }

    @Override
    public void initListeners() {
        btnTrue.setOnClickListener(this);
        tvClear.setOnClickListener(this);

        //编辑框焦点事件
        etExpressNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("setOnFocusChangeListener");

                if (hasFocus && canVis) { //得到焦点事件
                    if (handleData.queryData("", lvSearchResult) == 0) {
                        disView(0);
                    } else {
                        disView(1);
                    }
                } else {//隐藏键盘，隐藏列表
                    closeKeybord(etExpressNum, LifeActivityExpress.this);
                    disView(0);
                }
            }
        });

        etExpressNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("afterTextChanged");
                if (handleData.queryData("", lvSearchResult) == 0) {
                    disView(0);
                } else if (canVis) {
                    disView(1);
                }
                if (s.toString().trim().length() == 0) {
                    tvSearchTip.setText("搜索历史");
                } else {
                    tvSearchTip.setText("搜索结果");
                }
                String tempName = etExpressNum.getText().toString();
                // 根据tempName去模糊查询数据库中有没有数据
                handleData.queryData(tempName, lvSearchResult);
            }
        });

        // 点击listView收缩
        lvExpressData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                disView(0);
            }
        });


        //  历史记录点击事件
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                canVis = false;
                TextView textView = (TextView) view.findViewById(R.id.tv_record_text);
                String name = textView.getText().toString();
                checkExpress(name);
            }
        });

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);

        final List<Map<String, Object>> data = new ArrayList<>();
        int[] icons = new int[]{R.mipmap.user_icon, R.mipmap.edit_icon, R.mipmap.logout_icon};
        String[] letters = new String[]{username, "修改密码", "退出登录"};
        for (int i = 0; i < 3; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("drawer_icon", icons[i]);
            temp.put("drawer_button", letters[i]);
            data.add(temp);
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[]{"drawer_icon", "drawer_button"}, new int[]{R.id.drawer_icon, R.id.drawer_button});
        life_left_drawer.setAdapter(simpleAdapter);
        life_left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LifeActivityExpress.this);
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
                            Toast.makeText(LifeActivityExpress.this, "退出成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LifeActivityExpress.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });

        life_sport_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeActivityExpress.this, SportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        life_life_button.setClickable(false);
        life_study_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeActivityExpress.this, StudyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        //  点击记账按钮跳转到生活界面默认记账页面
        express.setClickable(false);
        //  进入到快递查询页面，设置该按钮点击事件无效
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeActivityExpress.this, LifeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

    }

    @Override
    public void initData() {
        handleData = new ExpressHandleData(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnTrue) {
            String str = etExpressNum.getText().toString().trim();
            if (str.equals("")) {
                ShowToast("快递单号不能为空噢!");
                return;
            }
            canVis = false;
            checkExpress(str);
        } else if (v == tvClear) { // 清空搜索历史
            handleData.deleteData();
            handleData.queryData("", lvSearchResult);
        } else if (v == etExpressNum) { // 编辑框点击事件
            if (handleData.queryData("", lvSearchResult) == 0) {
                disView(0);
            } else {
                disView(1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            String str = data.getStringExtra("code");
            checkExpress(str);
        }
    }

    // 执行查询

    void checkExpress(final String str) {
        disView(0);
        handleData.insertData(str);
        closeKeybord(etExpressNum, this);
        etExpressNum.setText(str);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadExpress(str);
            }
        }).start();

    }

    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == SUCCESS) {
                Express express = (Express) msg.getData().getSerializable("json");
                if (express != null) {
                    setData(express);
                }
                disView(0);
                canVis = true;
            } else {
                ShowToast(msg.getData().getString("mess"));
                disView(0);
                canVis = true;
            }
        }
    };

    // 线程解析快递
    private void loadExpress(String pid) {
        ExpressHandleWeb handleWeb = new ExpressHandleWeb();
        String webCode = "";
        try {
            webCode = handleWeb.posturl("http://m.kuaidi100.com/autonumber/auto?num=" + pid, "utf-8");
            if (!webCode.contains("comCode")) {
                sendMessToHandler(ERROR, "快递单号错误!");
                return;
            }
        } catch (Exception me) {
            System.out.println(me.getMessage() + "获取快递类型异常");
            sendMessToHandler(ERROR, me.getMessage());
        }

        Pattern p = Pattern.compile("comCode\":\"(\\w+)\"");
        Matcher matcher = p.matcher(webCode);
        while (matcher.find()) { //每一个快递公司
            //解析json
            String url = "http://www.kuaidi.com/index-ajaxselectcourierinfo-" + pid + "-" + matcher.group(1) + ".html";
            String temp = handleWeb.posturl(url, "utf-8");
            if (parseJson(temp)) {
                return;
            }
        }
        sendMessToHandler(ERROR, "查询失败,请确认单号!");
    }

    private boolean parseJson(String json) {
        Express express = new Express();
        try {
            JSONObject object = new JSONObject(json);
            if (object.getBoolean("success")) {
                express.setName(decodeUnicode(object.getString("company")));
                express.setLogoUrl("http://www.kuaidi.com" + object.getString("ico"));
                express.setOfficialUrl(object.getString("url"));

                JSONArray allContent = object.getJSONArray("data");
                for (int i = 0; i < allContent.length(); i++) {
                    JSONObject data = allContent.getJSONObject(i);
                    String time = data.getString("time");
                    String context = decodeUnicode(data.getString("context"));

                    Content content = new Content();
                    content.setTime(time);
                    content.setContext(context);
                    express.content.add(content);  //添加进对象；
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("json", express);
                msg.setData(bundle);
                msg.what = SUCCESS;
                handler.sendMessage(msg);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendMessToHandler(ERROR, e.getMessage());
            return false;
        }
    }

    public void sendMessToHandler(int messId, String mess) {
        Message message = new Message();
        message.what = messId;
        Bundle bundle = new Bundle();
        bundle.putString("mess", mess);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void setData(Express express) {
        MessListAdapter adapter = new MessListAdapter(this, express.getContent());
        lvExpressData.setAdapter(adapter);
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    // 历史记录的显示隐藏动画
    private void disView(int mode) {
        if (mode == 0 && svSearchList.getVisibility() == View.VISIBLE) {
            svSearchList.setVisibility(View.GONE);
            svSearchList.setAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_top_out));
        } else if (mode == 1 && svSearchList.getVisibility() == View.GONE) {
            svSearchList.setVisibility(View.VISIBLE);
            svSearchList.setAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_top_in));
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
