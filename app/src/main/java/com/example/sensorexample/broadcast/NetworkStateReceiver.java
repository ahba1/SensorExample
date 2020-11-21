package com.example.sensorexample.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Patterns;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.WIFI_SERVICE;

public class NetworkStateReceiver extends BroadcastReceiver {

    private Context context;

    public interface NetworkChangeListener{

        void onNetworkChanged(String ip);

    }

    private NetworkChangeListener networkChangeListener;

    private static String ip = "0.0.0.0";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        updateByStream();         
    }

    private String getIp(Context context){
        WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return (ipAddress & 0xff) + "." + (ipAddress>>8 & 0xff) + "." + (ipAddress>>16 & 0xff) + "." + (ipAddress >> 24 & 0xff);
    }

    public void setNetworkChangeListener(NetworkChangeListener l){
        this.networkChangeListener = l;
        networkChangeListener.onNetworkChanged(ip);
    }

    public static String getIp(){
        return ip;
    }

    private boolean updateByWifi(){
        if (!ip.equals(getIp(context))&&!"0.0.0.0".equals(ip)){
            ip = getIp(context);
            networkChangeListener.onNetworkChanged(ip);
            return true;
        }
        return false;
    }

    private void updateByStream(){
        String regex = "^(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))$";
        Pattern p = Pattern.compile(regex);
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()){
                NetworkInterface ntf = en.nextElement();
                Enumeration<InetAddress> inetAddresses = ntf.getInetAddresses();
                while (inetAddresses.hasMoreElements()){
                    String hostAddr = inetAddresses.nextElement().getHostAddress();
                    Matcher matcher = p.matcher(hostAddr);
                    if (matcher.matches()&&!"127.0.0.1".equals(hostAddr)){
                        ip = hostAddr;
                    }
                }
            }
        } catch (SocketException e) {
            ip = "0.0.0.0";
            e.printStackTrace();
        }finally {
            networkChangeListener.onNetworkChanged(ip);
        }
    }
}
