package com.example.john.finalproject.Life.account;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.john.finalproject.R;

/**
 * Created by lmh on 2016/12/15.
 */
public class StaticReceiver extends BroadcastReceiver{
    private static final String STATICACTION = "com.example.john.finalproject.Life.account.StaticReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)) {
            Bundle bundle = intent.getExtras();
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.expenditure);
            Notification.Builder builder = new Notification.Builder(context);
            Intent click = new Intent(context, LifeActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,  click, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentTitle("静态广播")
                    .setContentText("请查看您的日支出账单")
                    .setTicker("您的日支出已超过预定的200元")
                    .setLargeIcon(bm)
                    .setSmallIcon(R.mipmap.expenditure)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //  绑定Notification,发送相应的通知
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }


}
