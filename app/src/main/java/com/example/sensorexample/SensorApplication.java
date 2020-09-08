package com.example.sensorexample;

import android.app.Application;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SensorApplication extends Application {

    public static String ip;

    @Override
    public void onCreate() {
        super.onCreate();
        //WSHandler.bind("192.168.1.3:5000");
        ip = getIp();

    }

    private String getIp(){
        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return (ipAddress & 0xff) + "." + (ipAddress>>8 & 0xff) + "." + (ipAddress>>16 & 0xff) + "." + (ipAddress >> 24 & 0xff);
    }
}
