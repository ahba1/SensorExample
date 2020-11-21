package com.example.sensorexample.util;

import android.hardware.SensorManager;

public class OrientationUtil {

    public static float[] getOrientation(float[] accelerometerValues, float[] magneticFieldValues){
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        return values;
    }

}
