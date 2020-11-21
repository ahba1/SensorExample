package com.example.sensorexample.sensor;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    private List<String> device;
    private int delay;
    private List<String> sensor;
    private int spu;

    public Task() {
    }

    public Task(List<String> device, int delay, List<String> sensor, int spu) {
        this.device = device;
        this.delay = delay;
        this.sensor = sensor;
        this.spu = spu;
    }

    public List<String> getDevice() {
        return device;
    }

    public void setDevice(List<String> device) {
        this.device = device;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public List<String> getSensor() {
        return sensor;
    }

    public void setSensor(List<String> sensor) {
        this.sensor = sensor;
    }

    public int getSpu() {
        return spu;
    }

    public void setSpu(int spu) {
        this.spu = spu;
    }

}
