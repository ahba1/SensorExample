 package com.example.sensorexample.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


import com.example.sensorexample.activity.SensorActivity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * sensor后台工作，管理所有的sensor，控制sensor
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class SensorService extends Service {

    private SensorBinder binder;

    //唯一通道ID
    private final static String CHANNEL_ID = "foreground channel 01";

    //notification id
    private final static int ID = 777;
    @Override
    public void onCreate() {
        super.onCreate();
        //服务创建时，初始化所有的sensor
        binder = new SensorBinder(this);
        Notification notification = createForegroundNotification();
        startForeground(ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binder.closeAll();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createForegroundNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //安卓8.0以上的系统，使用消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName = "sensor notification";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("it is a sensor notification");
            if (notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        //通知的小图标
        //builder.setSmallIcon();
        builder.setContentTitle("sensor stream service");
        builder.setContentText("");
        Intent intent = new Intent(this, SensorActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        return builder.build();
    }

}
