package com.example.sensorexample.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Message;

import com.example.sensorexample.activity.sensor.SensorActivity;
import com.example.sensorexample.exception.UnsupportedSpuException;
import com.example.sensorexample.exception.UnsupportedTaskException;
import com.example.sensorexample.sensor.SensorInfo;
import com.example.sensorexample.sensor.SensorListenerWrapper;
import com.example.sensorexample.sensor.SensorWrapper;
import com.example.sensorexample.sensor.Task;
import com.example.sensorexample.server.ServerManager;
import com.example.sensorexample.server.ServerProxy;
import com.example.sensorexample.util.OrientationUtil;

import java.util.HashMap;
import java.util.Map;

public class SensorBinder extends Binder {

    private final static long DEFAULT_SPU = 100;

    private final static long MIN_SPU = 50;

    private final SensorManager manager;

    private final ServerProxy serverProxy;

    //传感器包装对象缓存池
    private final Map<String, SensorWrapper> sensorWrapperMap;

    private SensorActivity.ItemEventHandler handler;

    public void setHandler(SensorActivity.ItemEventHandler handler){
        this.handler = handler;
    }

    public SensorBinder(SensorService sensorService){
        manager = (SensorManager)sensorService.getSystemService(Context.SENSOR_SERVICE);
        sensorWrapperMap = new HashMap<>();
        serverProxy = new ServerManager(sensorService, 7999);
    }

    private SensorWrapper createSensorWrapper(String type, SensorListenerWrapper listenerWrapper, long samplingPeriodUs){
        SensorWrapper wrapper;
        if ("orientation".equals(type)){
            wrapper = new SensorWrapper(type, null, listenerWrapper);
            final float[][] accelerometerValues = new float[1][3];
            final float[][] magneticFieldValues = new float[1][3];
            final Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            manager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    accelerometerValues[0] = event.values;
                    float[] values = OrientationUtil.getOrientation(accelerometerValues[0], magneticFieldValues[0]);
                    listenerWrapper.onSensorChanged(values);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, accelerometer, (int)samplingPeriodUs);
            manager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    magneticFieldValues[0] = event.values;
                    float[] values = OrientationUtil.getOrientation(accelerometerValues[0], magneticFieldValues[0]);
                    listenerWrapper.onSensorChanged(values);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, magnetic, (int)samplingPeriodUs);
        }else{
            int realType = SensorInfo.getRealType(type);
            Sensor sensor = manager.getDefaultSensor(realType);
            wrapper = new SensorWrapper(type, sensor, listenerWrapper);
        }
        return wrapper;
    }
    //自定义速率，安卓版本至少是2.3，API level 9
    //创建对应的传感器包装对象，设置启动延迟，发送速率和回调接口向ws发送信息
    public void active(String type, SensorListenerWrapper listenerWrapper, long delay, long samplingPeriodUs) throws UnsupportedSpuException {
        if (samplingPeriodUs<MIN_SPU){
            throw new UnsupportedSpuException(MIN_SPU, samplingPeriodUs);
        }
        if (!sensorWrapperMap.containsKey(type)||sensorWrapperMap.get(type)==null){
            SensorWrapper wrapper = createSensorWrapper(type, listenerWrapper, samplingPeriodUs);
            wrapper.active(manager, delay, samplingPeriodUs);
            sensorWrapperMap.put(type, wrapper);
            Message message = handler.obtainMessage();
            message.what = SensorActivity.WS_OPEN;
            message.obj = type;
            handler.sendMessage(message);
        }else {
            SensorWrapper wrapper = sensorWrapperMap.get(type);
            if (wrapper!=null){
                wrapper.setListenerWrapper(listenerWrapper);
                wrapper.active(manager, 0);
            }
        }
    }

    public void active(String type, SensorListenerWrapper listenerWrapper, long samplingPeriodUs) throws UnsupportedSpuException {
        active(type, listenerWrapper, 0, samplingPeriodUs);
    }

    public void active(String type, SensorListenerWrapper wrapper) throws UnsupportedSpuException {
        active(type, wrapper, DEFAULT_SPU);
    }

    //对外操作接口
    public void active(Task task) throws UnsupportedSpuException, UnsupportedTaskException {
        if (task == null)
            throw new UnsupportedTaskException();
        for (String type:task.getSensor()){
            active(type, new SensorListenerWrapper() {
                @Override
                public void onSensorChanged(String msg) {
                    setSensorMsg(type, msg);
                }
            }, task.getDelay(), task.getSpu());
        }
    }

    public void sleep(String type){
        SensorWrapper wrapper = sensorWrapperMap.get(type);
        if (wrapper!=null){
            wrapper.sleep(manager);
        }
        Message message = handler.obtainMessage();
        message.what = SensorActivity.WS_CLOSED;
        message.obj = type;
        handler.sendMessage(message);
    }

    public void closeAll(){
        for (String s:sensorWrapperMap.keySet()){
            sleep(s);
        }
    }

    public void setSensorMsg(String type, String msg){
        SensorWrapper wrapper = sensorWrapperMap.get(type);
        if (wrapper!=null){
            wrapper.setMsg(msg);
        }
    }

    public void setSamplingPeriodUs(String type, int samplingPeriodUs){
        if (sensorWrapperMap.get(type)==null){
            int realType = SensorInfo.getRealType(type);
            Sensor sensor = manager.getDefaultSensor(realType);
            sensorWrapperMap.put(type, new SensorWrapper(type, sensor, null));
        }
        SensorWrapper wrapper = sensorWrapperMap.get(type);
        if (wrapper!=null){
            wrapper.setSamplingPeriodUs(samplingPeriodUs);
        }
    }

    public void startHttpService(){
        serverProxy.startHttpService();
    }

    public void startWebSocketService(){
        serverProxy.startWebSocketService();
    }

    public void setTaskCallBack(ServerManager.TaskCallBack callBack){
        serverProxy.setTaskCallBack(callBack);
    }
}
