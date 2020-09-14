package com.example.sensorexample.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.sensorexample.broadcast.NetworkStateReceiver;
import com.example.sensorexample.sensor.SensorInfo;
import com.example.sensorexample.sensor.SensorListenerWrapper;
import com.example.sensorexample.service.SensorBinder;
import com.example.sensorexample.ws.WSHandler;
import com.example.sensorexample.ws.WSListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.WebSocket;

class SensorPresenter implements Contract.Presenter {

    private final Contract.View view;

    private SensorBinder binder;

    private final ArrayList<String> info =  new ArrayList<>(SensorInfo.getSensorNames());

    private NetworkStateReceiver networkStateReceiver;

    SensorPresenter(Contract.View view){
        this.view = view;
    }

    @Override
    public void setBinder(SensorBinder binder) {
        this.binder = binder;
    }

    @Override
    public void setSamplingPeriodUs(int pos, int samplingPeriodUs) {
        final String type = info.get(pos).toLowerCase();
        binder.setSamplingPeriodUs(type, samplingPeriodUs);
    }

    @Override
    public void registerNetworkBoardCast() {
        if (networkStateReceiver == null){
            networkStateReceiver = new NetworkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkStateReceiver.setNetworkChangeListener(view::onNetworkChanged);
        view.getContext().registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void unregisterNetworkBoardCast() {
        view.getContext().unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void startSensor(int pos) {
        final String type = info.get(pos).toLowerCase();
        //ws连接
        WSHandler.connect(type.replace(" ", ""), new WSListener(){
            @Override
            public void onOpen(@NotNull WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                view.onDataTransmitting(type);
            }
        });
        //开启传感器监听
        binder.active(type, new SensorListenerWrapper() {
            @Override
            public void onSensorChanged(String msg) {
                Log.v("TAG", msg);
                binder.setSensorMsg(type, msg);
            }
        });
    }

    @Override
    public void stopSensor(int pos) {
        String type = info.get(pos).toLowerCase();
        binder.sleep(type);
        WSHandler.close(type);
        view.onDataTransmissionStopped(type);
    }
}
