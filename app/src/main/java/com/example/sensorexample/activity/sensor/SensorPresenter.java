package com.example.sensorexample.activity.sensor;

import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.sensorexample.broadcast.NetworkStateReceiver;
import com.example.sensorexample.exception.UnsupportedSpuException;
import com.example.sensorexample.sensor.SensorInfo;
import com.example.sensorexample.sensor.SensorListenerWrapper;
import com.example.sensorexample.service.SensorBinder;

import java.util.HashSet;
import java.util.Set;

class SensorPresenter implements Contract.Presenter {

    private final Contract.View view;

    private SensorBinder binder;

    private NetworkStateReceiver networkStateReceiver;

    private final Set<String> chosenTypes = new HashSet<>();

    SensorPresenter(Contract.View view){
        this.view = view;
    }

    @Override
    public void setBinder(SensorBinder binder) {
        this.binder = binder;
    }

    @Override
    public void setSamplingPeriodUs(int pos, int samplingPeriodUs) {
        String type = SensorInfo.getSensorNames().get(pos).toLowerCase();
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
    public void addType(int pos) {
        String type = SensorInfo.getSensorNames().get(pos);
        chosenTypes.add(type);
        view.onHolderSelected(type);
    }

    @Override
    public void startAll() {
        for(String type:chosenTypes){
            startSensor(SensorInfo.getSensorNames().indexOf(type));
        }
        chosenTypes.clear();
    }

    @Override
    public void startSensor(int pos) {
        String type = SensorInfo.getSensorNames().get(pos).toLowerCase();
        //开启传感器监听
        try {
            binder.active(type, new SensorListenerWrapper() {
                @Override
                public void onSensorChanged(String msg) {
                    binder.setSensorMsg(type, msg);
                }
            });
        } catch (UnsupportedSpuException e) {
            view.onError(e);
        }
    }

    @Override
    public void stopSensor(int pos) {
        String type = SensorInfo.getSensorNames().get(pos).toLowerCase();
        binder.sleep(type);
        view.onDataTransmissionStopped(type);
    }
}
