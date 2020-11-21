package com.example.sensorexample.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorInfo {

    private final static Map<String, Integer> SENSOR_MAPPING;

    private final static List<String> DEVICE_SENSORS;

    static {
        SENSOR_MAPPING = new HashMap<>();
        DEVICE_SENSORS = new ArrayList<>();
        SENSOR_MAPPING.put("accelerometer", Sensor.TYPE_ACCELEROMETER);
        SENSOR_MAPPING.put("gyroscope", Sensor.TYPE_GYROSCOPE);
        SENSOR_MAPPING.put("magnetometer", Sensor.TYPE_MAGNETIC_FIELD);
        SENSOR_MAPPING.put("orientation", Sensor.TYPE_ORIENTATION);
        SENSOR_MAPPING.put("step counter", Sensor.TYPE_STEP_COUNTER);
        SENSOR_MAPPING.put("thermometer", Sensor.TYPE_AMBIENT_TEMPERATURE);
        SENSOR_MAPPING.put("light sensor", Sensor.TYPE_LIGHT);
        SENSOR_MAPPING.put("proximity", Sensor.TYPE_PROXIMITY);
        SENSOR_MAPPING.put("air pressure", Sensor.TYPE_PRESSURE);
        SENSOR_MAPPING.put("gravity", Sensor.TYPE_GRAVITY);
    }

    private SensorInfo(){
    }

    public static Integer getRealType(String type){
        return SENSOR_MAPPING.get(type);
    }

    public static List<String> getSensorNames(){
        return DEVICE_SENSORS;
    }

    public static void check(SensorManager manager){
        for (Map.Entry<String, Integer> entry : SENSOR_MAPPING.entrySet()){
            if (isExist(manager, entry.getKey())){
                DEVICE_SENSORS.add(entry.getKey());
            }
        }
    }

    private static boolean isExist(SensorManager manager, String type){
        if ("orientation".equals(type)){
            return isExist(manager, "accelerometer") && isExist(manager, "magnetometer");
        }else{
            return manager.getSensorList(SENSOR_MAPPING.get(type)).size() != 0;
        }
    }
}
