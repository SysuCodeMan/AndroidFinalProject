package com.example.john.finalproject.Sport;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class MyStepCounterService extends Service implements SensorEventListener {
    int steps;
    int initialSteps;
    List<stepRecord> historys;
    String name;
    SensorManager sensorManager;
    Sensor stepCount;
    stepCountDB db = new stepCountDB(MyStepCounterService.this);
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //private String username;

    public void getUsernameFromPreference() {
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        name = sharedPreferences.getString("username", null);
    }
    public MyStepCounterService() {
        steps = 0;
        name = "Test";
    }
    public void setName(String _name) {
        name = _name;
    }
    @Override
    public void onCreate() {
        initialSteps = 0;
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        stepCount = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(MyStepCounterService.this, stepCount,SensorManager.SENSOR_DELAY_NORMAL);
        getUsernameFromPreference();
        //startTimeCount();
        //loadHistory(); done when on connected
    }
    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MyStepCounterService getService() {
            return MyStepCounterService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimeCount();
        loadHistory();
        getUsernameFromPreference();

        System.out.println("Start Command" + name);
        return START_STICKY;
    }
    public int getSteps() {
        //save();
        if (historys != null && historys.size()>= 1)
            return steps;
        else
            return 0;
    }
    public int getYesterdayStep() {
        //loadHistory();
        if (historys != null && historys.size()>1)
            return Integer.valueOf(historys.get(historys.size()-2).count);
        else return 0;
    }
    public void updateWidget() {
        Intent intent = new Intent("WIDGETUPDATE");
        Bundle bundle = new Bundle();
        int realstep = getSteps() - getYesterdayStep();
        bundle.putString("step_count","" + realstep);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = (int)event.values[0];
        System.out.println("Step:"+steps);
        updateWidget();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private TimeCount time;
    public void startTimeCount() {
        time = new TimeCount(3000, 1000);
        time.start();
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            save();
            startTimeCount();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
    public void save() {
        //DateFormat formatter = SimpleDateFormat.getDateInstance();
        Calendar cal = Calendar.getInstance();
        String year = ""+cal.get(Calendar.YEAR);
        String month = ""+(cal.get(Calendar.MONTH)+1);
        String day = ""+cal.get(Calendar.DAY_OF_MONTH);
        String steps = "" + getSteps();
        if (!db.checkExist(name,month,day)) {
            db.insert2DB(name,month,day,steps);
        } else {
            db.update2DB(name,month,day,steps);
        }
        //Log.i("Tick", "tick");
        System.out.println("tick");
    }


    public void loadHistory() {
        historys = db.query(name);
        if(historys.size() == 0 && !name.equals("Test")) {
            db.insert2DB(name,"","",""+steps);
        }
        save();
    }

    @Override
    public void onDestroy() {
        System.out.println("Service Destroy!");
        super.onDestroy();
    }


}
