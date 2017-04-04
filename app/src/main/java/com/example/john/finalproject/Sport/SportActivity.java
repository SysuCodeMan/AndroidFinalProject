package com.example.john.finalproject.Sport;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.example.john.finalproject.Life.account.LifeActivity;
import com.example.john.finalproject.MainActivity;
import com.example.john.finalproject.R;
import com.example.john.finalproject.Study.StudyActivity;
import com.example.john.finalproject.userDB;

/**
 * Created by Xia on 2016/11/30.
 */
public class SportActivity extends AppCompatActivity {
    private DrawerLayout sport_drawer_layout;
    private ListView sport_left_drawer;
    private ImageButton sport_sport_button;
    private ImageButton sport_life_button;
    private ImageButton sport_study_button;
    private ImageButton iconCD;

    ConnectivityManager connectivityManager;
    TextView cityName, temperature, windLev, airQuality, stepET, compassDegree;
    SensorManager sensorManager;
    Sensor accelerrometerSensor, magneticSensor;
    ImageView compass,weatherPic;
    MyStepCounterService stepService;
    RoundProgress roundProgress;

    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String username;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);
        if (username == null) username = "Test";
        setContentView(R.layout.sport_layout);
        findView();
        setAim();
        RotationDegree = 0;
        getSensor();
        compass.setRotation(RotationDegree);
        setDrawer();
        bindButton();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        connectService();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        iconCD.callOnClick();//获取天气
    }

    @Override
    protected void onStart() {
        //connectService();
        super.onStart();
    }


    private void findView() {
        sport_drawer_layout = (DrawerLayout) findViewById(R.id.sport_drawer_layout);
        sport_left_drawer = (ListView) findViewById(R.id.sport_left_drawer);
        sport_sport_button = (ImageButton) findViewById(R.id.sport_sport_button);
        sport_life_button = (ImageButton) findViewById(R.id.sport_life_button);
        sport_study_button = (ImageButton) findViewById(R.id.sport_study_button);
        iconCD = (ImageButton) findViewById(R.id.iconCD);
        cityName = (TextView) findViewById(R.id.cityName);
        temperature = (TextView) findViewById(R.id.temperature);
        windLev = (TextView) findViewById(R.id.windLev);
        airQuality = (TextView) findViewById(R.id.airQuality);
        stepET = (TextView) findViewById(R.id.stepET);
        compassDegree = (TextView) findViewById(R.id.compassDegree);
        compass = (ImageView) findViewById(R.id.compass);
        weatherPic = (ImageView)findViewById(R.id.weather_pic);
        roundProgress = (RoundProgress) findViewById(R.id.roundProgress);
    }

    private void getSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void setAim() {
        String aim = sharedPreferences.getString("maxProgress", "10000");
        roundProgress.setMaxProgress(Integer.valueOf(aim));
    }

    private void setDrawer() {
        final List<Map<String, Object>> data = new ArrayList<>();
        int[] icons = new int[]{R.mipmap.user_icon,R.mipmap.target, R.mipmap.history, R.mipmap.edit_icon, R.mipmap.logout_icon};
        String[] letters = new String[]{username,"目标设置", "历史数据", "修改密码", "退出登录"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("drawer_icon", icons[i]);
            temp.put("drawer_button", letters[i]);
            data.add(temp);
        }
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
                new String[]{"drawer_icon", "drawer_button"}, new int[]{R.id.drawer_icon, R.id.drawer_button});
        sport_left_drawer.setAdapter(simpleAdapter);
        sport_left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    LayoutInflater factory = LayoutInflater.from(SportActivity.this);
                    View dialogview = factory.inflate(R.layout.mydialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SportActivity.this);
                    builder.setView(dialogview);

                    final EditText setStepET = (EditText) dialogview.findViewById(R.id.setStepET);
                    setStepET.setText("" + roundProgress.getMaxProgress());
                    AlertDialog AD = builder
                            .setPositiveButton("保存修改",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            int aim = Integer.valueOf(setStepET.getText().toString());
                                            roundProgress.setMaxProgress(aim);
                                            editor.putString("maxProgress", setStepET.getText().toString());
                                            editor.commit();
                                        }
                                    })
                            .setNegativeButton("放弃修改",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    }).create();
                    AD.show();
                } else if (i == 2) {
                    queryStepRecords(username);
                } else if (i == 3) {
                    LayoutInflater factory = LayoutInflater.from(SportActivity.this);
                    final View dialog_view = factory.inflate(R.layout.password_dialog_layout, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SportActivity.this);
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
                    password_dialog_quit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    password_dialog_save_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            userDB userdb = new userDB(SportActivity.this);
                            if (TextUtils.isEmpty(password_dialog_old_password.getText().toString())) {
                                Toast.makeText(SportActivity.this, "原密码为空", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password_dialog_new_password.getText().toString())) {
                                Toast.makeText(SportActivity.this, "新密码为空", Toast.LENGTH_SHORT).show();
                            } else {
                                if (userdb.confirmUser(username, password_dialog_old_password.getText().toString())) {
                                    userdb.updateData(username, password_dialog_new_password.getText().toString());
                                    alertDialog.dismiss();
                                    Toast.makeText(SportActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SportActivity.this, "原密码输入错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else if (i == 4) {
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SportActivity.this);
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
                            stepService.save();
                            unbindService(sc);
                            CookieManager cookieManager = CookieManager.getInstance();
                            cookieManager.removeAllCookie();
                            Toast.makeText(SportActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SportActivity.this, MainActivity.class);
                            startActivity(intent);

                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
    }

    private void bindButton() {
        sport_sport_button.setClickable(false);
        sport_life_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SportActivity.this, LifeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        sport_study_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SportActivity.this, StudyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        iconCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.start();
                mLocationClient.requestLocation();
            }
        });
    }

    private void queryWeather() {
        connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getWeather();
                }
            }).start();
        } else {
            Toast.makeText(SportActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void getWeather() {
        String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
        HttpURLConnection connection = null;
        if (addr == null) return;
        String city = addr.split("省")[1].split("市")[0];
        Log.i("CityForQuery", city);
        try {
            Log.i("key", "Start connection");
            connection = (HttpURLConnection) ((new URL(url.toString()).openConnection()));
            connection.setRequestMethod("POST");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String request = URLEncoder.encode(city, "utf-8");
            //out.writeBytes("theCityCode=" + request + "&theUserID=f9d64987923e4deea67e2431e1a40fa6");
            out.writeBytes("theCityCode=" + request + "&theUserID=");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                Message message
                        = handler.obtainMessage(1);
                message.sendToTarget();
                //Toast.makeText(MainActivity.this, "连接失败，请重试", Toast.LENGTH_SHORT);
            } else {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Message message
                        = handler.obtainMessage(0);
                message.obj = parseXMLWithPull(response.toString());
                message.sendToTarget();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private ArrayList<String> parseXMLWithPull(String string) {
        Log.i("XML", string);
        ArrayList<String> answer = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(string));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("string")) {
                            String value = parser.nextText();
                            Log.v("XML", value);
                            answer.add(value);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return answer;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                Toast.makeText(SportActivity.this, "连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<String> response = (ArrayList<String>) message.obj;
            if (response.get(0).contains("查询结果为空")) {
                Toast.makeText(SportActivity.this, "查询结果为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.get(0).contains("发现错误：免费用户不能使用高速访问。")) {
                Toast.makeText(SportActivity.this, "免费用户不能使用高速访问", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.get(0).contains("发现错误：免费用户24小时内访问超过规定数量。")) {
                Toast.makeText(SportActivity.this, "免费用户24小时内访问超过规定数量", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.get(4).equals("今日天气实况：暂无实况")) {
                Toast.makeText(SportActivity.this, "暂无该城市天气实况", Toast.LENGTH_SHORT).show();
            }
            /*else if (!responseData.get(1).equals(searchED.getText().toString())){
                Toast.makeText(SportActivity.this, "当前城市不存在，请重新输入", Toast.LENGTH_SHORT).show();
            }*/
            else {

                cityName.setText(response.get(1));
                String info[] = response.get(4).split("；");
                temperature.setText(info[0].split("：")[2]);
                windLev.setText(info[1].split("：")[1]);
                //wetInPre.setText(info[2]);
                airQuality.setText(response.get(5).split("。")[1]);
                //temperatureLH.setText(response.get(8));
                String[] img1 = response.get(10).split("\\.");
                String[] img2 = response.get(11).split("\\.");
                int Img1 =getResources().getIdentifier("a_"+img1[0], "mipmap", getPackageName());
                int Img2 =getResources().getIdentifier("a_"+img2[0], "mipmap", getPackageName());
                weatherPic.setImageResource(Img1);
                Toast.makeText(SportActivity.this, "获取本地天气成功", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(message);
        }
    };


    float RotationDegree = 0;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] accValues = new float[3];
        float[] magValues = new float[3];

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = sensorEvent.values.clone();
                    break;
                default:
                    break;
            }
            float[] R = new float[9];
            float[] values = new float[3];
            sensorManager.getRotationMatrix(R, null, accValues, magValues);
            sensorManager.getOrientation(R, values);
            float tempDegree = (float) ((-1.0) * Math.toDegrees(values[0]));
            //System.out.println(tempDegree);
            if (Math.abs(RotationDegree - tempDegree) > 5)
                spin(tempDegree);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private void spin(float tempDegree) {
        float realDegree = (float) ((-1.0) * tempDegree);
        if (realDegree < 0) realDegree += 360;
        float realDegreePlus = (float) (realDegree + 22.5);
        if (realDegreePlus >= 360) realDegreePlus -= 360;
        String dir = directions[(int) (realDegreePlus / 45)];
        compassDegree.setText(dir + (int) realDegree + "°");
        float fromdegree = RotationDegree;
        float todegree = tempDegree;
        if (fromdegree - todegree > 180) {
            fromdegree -= 360;
        } else if (todegree - fromdegree > 180) {
            fromdegree += 360;
        }
        RotateAnimation ra = new RotateAnimation(fromdegree, todegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        compass.startAnimation(ra);
        RotationDegree = tempDegree;
        //compass.setRotation(RotationDegree);
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            stepService = ((MyStepCounterService.MyBinder) (service)).getService();
            int today = stepService.getSteps();
            int yesterday = stepService.getYesterdayStep();
            stepET.setText("" + (today - yesterday));
            roundProgress.setProgress(today - yesterday);
            startStepCounter();
            //stepService.setName(username);
            stepService.loadHistory();
            stepService.startTimeCount();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            stepService = null;
        }
    };


    private void connectService() {
        Intent intent = new Intent(this, MyStepCounterService.class);
        startService(intent);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    private void startStepCounter() {
        stephandler.post(runnable);
    }

    Handler stephandler = new Handler() {
        public void handleMessage(Message msg) {
            int realStep = msg.arg1;
            stepET.setText("" + realStep);
            roundProgress.setProgress(realStep);
            stephandler.post(runnable);
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message msg = stephandler.obtainMessage();
            int nowStep = stepService.getSteps();
            int yesStep = stepService.getYesterdayStep();
            msg.arg1 = nowStep-yesStep;
            stephandler.sendMessage(msg);
        }
    };



    private void queryStepRecords(String name) {
        stepService.save();
        Intent intent = new Intent(SportActivity.this, StepHistory.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }


    private String addr;

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps

        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            boolean errorflag = true;
            StringBuffer sb = new StringBuffer(256);
            String error_code;
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                errorflag = false;
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                errorflag = false;
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                errorflag = false;
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            mLocationClient.stop();
            if (!errorflag) {
                addr = sb.toString().split("\n")[5].split(":")[1];
                Log.i("FinalAddr", addr);
                queryWeather();
            } else {
                Toast.makeText(SportActivity.this,"定位失败，请检查定位服务设置",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(sensorEventListener, magneticSensor,
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, accelerrometerSensor,
                SensorManager.SENSOR_DELAY_UI);
//        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        username = sharedPreferences.getString("username", null);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        //unbindService(sc);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stepService.save();
        unbindService(sc);
        super.onDestroy();
    }

}
