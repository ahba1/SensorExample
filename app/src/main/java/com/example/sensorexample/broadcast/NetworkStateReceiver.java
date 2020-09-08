package com.example.sensorexample.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class NetworkStateReceiver extends BroadcastReceiver {

    public interface NetworkChangeListener{

        void onNetworkChanged(String ip);

    }

    private NetworkChangeListener networkChangeListener;

    private static String ip = "0.0.0.0";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connectivityManager.getAllNetworks();
            for (Network network:networks){
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo.isConnected()&&!ip.equals(getIp(context))){
                    ip = getIp(context);
                    networkChangeListener.onNetworkChanged(ip);
                    break;
                }
            }
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
}
