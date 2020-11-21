package com.example.sensorexample.sensor;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SensorData implements Serializable {

    private String type;
    private String record;
    private double spu;

    public SensorData(String type, String record, double spu) {
        this.type = type;
        this.record = record;
        this.spu = spu;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
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

    @NotNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
