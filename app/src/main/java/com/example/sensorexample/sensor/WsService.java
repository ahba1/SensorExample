package com.example.sensorexample.sensor;

/**
 * 传感器发送数据的接口
 * 观察者模式，在传感器数据发生变化时发送数据
 */
public interface WsService {

    void send(SensorEvent event);

}
