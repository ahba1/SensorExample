package com.example.sensorexample.sensor;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SensorEvent implements Serializable {

    private String from;
    private SensorData data;

    public SensorEvent(String from, SensorData data) {
        this.from = from;
        this.data = data;
    }

    public SensorData getData() {
        return data;
    }

    public void setData(SensorData data) {
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @NotNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
