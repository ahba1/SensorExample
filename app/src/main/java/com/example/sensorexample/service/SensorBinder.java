package com.example.sensorexample.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.sensorexample.activity.SensorActivity;
import com.example.sensorexample.broadcast.NetworkStateReceiver;
import com.example.sensorexample.sensor.SensorInfo;
import com.example.sensorexample.sensor.SensorListenerWrapper;
import com.example.sensorexample.sensor.SensorWrapper;
import com.example.sensorexample.sensor.Task;
import com.example.sensorexample.ws.WSHandler;
import com.example.sensorexample.ws.WSListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * service对应的binder，数据传输使用带外传输的方法，使用control实现对一个device的命令控制传输
 * 使用缓冲池（WSHandler）进行数据传输
 */
public class SensorBinder extends Binder {

    private final SensorManager manager;

    //传感器包装对象缓存池
    private final Map<String, SensorWrapper> sensorWrapperMap;

    //控制信道
    private WebSocket control;

    private final SensorService sensorService;

    private OnBindUrlListener onBindUrlListener;

    private SensorActivity.ItemEventHandler handler;

    public interface OnBindUrlListener{
        void onSuccess();

        void onFailure();
    }

    public SensorBinder(SensorService sensorService){
        this.sensorService = sensorService;
        manager = (SensorManager)sensorService.getSystemService(Context.SENSOR_SERVICE);
        sensorWrapperMap = new HashMap<>();
    }

    //自定义速率，安卓版本至少是2.3，API level 9
    //创建对应的传感器包装对象，设置启动延迟，发送速率和回调接口向ws发送信息
    public void active(String type, SensorListenerWrapper listenerWrapper, long delay, long samplingPeriodUs){
        if (!sensorWrapperMap.containsKey(type)||sensorWrapperMap.get(type)==null){
            int realType = SensorInfo.getRealType(type);
            Sensor sensor = manager.getDefaultSensor(realType);
            SensorWrapper wrapper = new SensorWrapper(sensorService, type, sensor, listenerWrapper);
            wrapper.active(manager, delay, samplingPeriodUs);
            sensorWrapperMap.put(type, wrapper);
            Message message = handler.obtainMessage();
            message.what = SensorActivity.WS_OPEN;
            message.obj = type;
            handler.sendMessage(message);
        }else {
            Objects.requireNonNull(sensorWrapperMap.get(type)).setListenerWrapper(listenerWrapper);
            Objects.requireNonNull(sensorWrapperMap.get(type)).active(manager, 0, samplingPeriodUs);
        }
    }

    public void active(String type, SensorListenerWrapper listenerWrapper, long samplingPeriodUs){
        active(type, listenerWrapper, 0, samplingPeriodUs);
    }

    public void active(String type, SensorListenerWrapper wrapper){
        active(type, wrapper, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void active(Task task){
        for (String type:task.getSensor()){
            WSHandler.connect(type);
            active(type, new SensorListenerWrapper() {
                @Override
                public void onSensorChanged(String msg) {
                    setSensorMsg(type, msg);
                }
            }, task.getDelay(), task.getSpu());
        }
    }

    public void sleep(String type){
        Objects.requireNonNull(sensorWrapperMap.get(type)).sleep(manager);
        Message message = handler.obtainMessage();
        message.what = SensorActivity.WS_CLOSED;
        message.obj = type;
        handler.sendMessage(message);
    }

    public void closeAll(){
        for (String s:sensorWrapperMap.keySet()){
            sleep(s);
        }
        control.cancel();
    }

    public void setSensorMsg(String type, String msg){
        Objects.requireNonNull(sensorWrapperMap.get(type)).setMsg(msg);
    }

    public void setSamplingPeriodUs(String type, int samplingPeriodUs){
        Objects.requireNonNull(sensorWrapperMap.get(type)).setSamplingPeriodUs(samplingPeriodUs);
    }

    //在绑定url之后，打开控制信道，并传递本机的ip地址
    public void bindUrl(String url, OnBindUrlListener l, SensorActivity.ItemEventHandler handler){
        this.onBindUrlListener = l;
        this.handler = handler;
        WSHandler.bind(url);
        control = WSHandler.buildWS("control/"+ NetworkStateReceiver.getIp(), new WSListener(){

            @Override
            public void onOpen(@NotNull WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                l.onSuccess();
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                Log.v("wsText", text);
                Task task = JSON.parseObject(text, Task.class);
                active(task);
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                WSHandler.closeAll();
            }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                WSHandler.closeAll();
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                onBindUrlListener.onFailure();
            }
        });
    }

    public void unBind(){
        closeAll();
        control.cancel();
    }
}
