package com.example.sensorexample;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.sensorexample.sensor.SensorInfo;

import java.util.List;

public class SensorApplication extends Application {

    public static String ip;

    @Override
    public void onCreate() {
        super.onCreate();
        //WSHandler.bind("192.168.1.3:5000");
        ip = getIp();
        SensorInfo.check((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        check();
    }

    private String getIp() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return (ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff);
    }

    private void check() {
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = manager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sn : sensorList) {
            Log.v("Type all", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        for (Sensor sn : sensorList) {
            Log.v("Type magnetic", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_PRESSURE);
        for (Sensor sn : sensorList) {
            Log.v("Type pressure", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_PROXIMITY);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_PROXIMITY", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_LIGHT);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_LIGHT", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_AMBIENT_TEMPERATURE", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_STEP_COUNTER);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_STEP_COUNTER", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_GYROSCOPE);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_GYROSCOPE", sn.getName());
        }

        sensorList = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_ACCELEROMETER", sn.getName());
        }


        sensorList = manager.getSensorList(Sensor.TYPE_GRAVITY);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_GRAVITY", sn.getName());
        }
        sensorList = manager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        for (Sensor sn : sensorList) {
            Log.v("TYPE_ROTATION_VECTOR", sn.getName());
        }
    }
}
