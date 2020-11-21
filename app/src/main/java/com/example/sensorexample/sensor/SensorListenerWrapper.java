package com.example.sensorexample.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class SensorListenerWrapper implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent event){
        String msg = gen(event.values);

        onSensorChanged(msg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(float[] values){
        String msg = gen(values);

        onSensorChanged(msg);
    }

    public abstract void onSensorChanged(String msg);

    private String gen(float[] values){
        StringBuffer buffer = new StringBuffer();
        for (float v:values){
            buffer.append(v).append(",");
        }
        return buffer.deleteCharAt(buffer.length()-1).toString();
    }
}
