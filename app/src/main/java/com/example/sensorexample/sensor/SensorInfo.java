package com.example.sensorexample.sensor;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SensorInfo {

    private static final Map<String, Integer> sensors;

    static {
        sensors = new HashMap<>();
        sensors.put("accelerometer", Sensor.TYPE_ACCELEROMETER);
        sensors.put("gyroscope", Sensor.TYPE_GYROSCOPE);
        sensors.put("magnetometer", Sensor.TYPE_MAGNETIC_FIELD);
        sensors.put("orientation", Sensor.TYPE_ORIENTATION);
        sensors.put("step counter", Sensor.TYPE_STEP_COUNTER);
        sensors.put("thermometer", Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensors.put("light sensor", Sensor.TYPE_LIGHT);
        sensors.put("proximity", Sensor.TYPE_PROXIMITY);
        sensors.put("air pressure", Sensor.TYPE_PRESSURE);
    }

    private SensorInfo(){
    }

    public static Integer getRealType(String type){
        return sensors.get(type);
    }

    public static Set<String> getSensorNames(){
        return sensors.keySet();
    }
}
