package com.example.sensorexample.activity;

import com.example.sensorexample.mvp.BasePresenter;
import com.example.sensorexample.mvp.BaseView;
import com.example.sensorexample.service.SensorBinder;

public interface Contract {

    interface View extends BaseView<Presenter> {
        void onNetworkChanged(String newIp);

        void onDataTransmitting(int pos);

        void onDataTransmissionStopped(int pos);
    }

    interface Presenter extends BasePresenter {
        void startSensor(int pos);

        void stopSensor(int pos);

        void setBinder(SensorBinder binder);

        void setSamplingPeriodUs(int pos, int samplingPeriodUs);

        void registerNetworkBoardCast();

        void unregisterNetworkBoardCast();
    }
}
