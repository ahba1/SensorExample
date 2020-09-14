package com.example.sensorexample.sensor;

import com.alibaba.fastjson.JSON;

public class SensorData {

    private String deviceIp;
    private String remoteIp;
    private String record;
    private double spu;

    public SensorData(String deviceIp, String remoteIp, String record, double spu) {
        this.deviceIp = deviceIp;
        this.remoteIp = remoteIp;
        this.record = record;
        this.spu = spu;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public double getSpu() {
        return spu;
    }

    public void setSpu(double spu) {
        this.spu = spu;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
