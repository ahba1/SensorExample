package com.example.sensorexample.activity.sensor;

import com.example.sensorexample.exception.UnsupportedSpuException;
import com.example.sensorexample.mvp.BasePresenter;
import com.example.sensorexample.mvp.BaseView;
import com.example.sensorexample.service.SensorBinder;

public interface Contract {

    interface View extends BaseView<Presenter> {
        void onNetworkChanged(String newIp);

        void onDataTransmitting(String type);

        void onDataTransmissionStopped(String type);

        void onHolderSelected(String type);

        SensorActivity.ItemEventHandler getHandler();

        void onError(UnsupportedSpuException exception);
    }

    interface Presenter extends BasePresenter {
        void startSensor(int pos);

        void stopSensor(int pos);

        void setBinder(SensorBinder binder);

        void setSamplingPeriodUs(int pos, int samplingPeriodUs);

        void registerNetworkBoardCast();

        void unregisterNetworkBoardCast();

        void addType(int pos);

        void startAll();
    }
}
