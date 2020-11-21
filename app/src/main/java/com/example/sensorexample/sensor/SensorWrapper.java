package com.example.sensorexample.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;

import com.example.sensorexample.broadcast.NetworkStateReceiver;

import org.greenrobot.eventbus.EventBus;

/**
 * 传感器包装类，实现Runnable接口，单独开一个线程去采集数据，并缓存，且对外暴露访问接口
 *
 */
public class SensorWrapper implements Runnable{

    private final Sensor sensor;

    private final String type;

    private SensorListenerWrapper listenerWrapper;

    private final Handler handler;

    private volatile String msg;

    private volatile long samplingPeriodUs;

    private long delay;
    //Ms
    private final static int MAX_LATENCY = 100;

    private SensorData data;

    public SensorWrapper(String type, Sensor sensor, SensorListenerWrapper listenerWrapper) {
        this.type = type;
        this.sensor = sensor;
        this.listenerWrapper = listenerWrapper;
        this.handler = new Handler(Looper.getMainLooper());
    }

    //传感器设置的spu并不是严格遵守这个采集时延，一般来说实际spu会小于理论设置的spu
    //内部设置一个msg的变量去保存每一次上传的传感器数据，并在run方法中进行发送，使用handler对发送时延进行控制
    public void active(SensorManager manager, long delay, long samplingPeriodUs){
        this.samplingPeriodUs = samplingPeriodUs;
        this.delay = delay;
        data = new SensorData(type, msg, samplingPeriodUs);
        //设置最大延迟，防止传感器启动太占用资源，在达到指定延迟之后还未启动就关闭
        manager.registerListener(listenerWrapper, sensor, (int)samplingPeriodUs, MAX_LATENCY);
        //设置启动延迟
        handler.postDelayed(this, delay);
    }

    public void active(SensorManager manager, long delay){
        active(manager, delay, samplingPeriodUs);
    }

    public void sleep(SensorManager manager){
        manager.unregisterListener(listenerWrapper, sensor);
        handler.removeCallbacks(this);
    }

    public void setListenerWrapper(SensorListenerWrapper wrapper){
        this.listenerWrapper = wrapper;
    }

    public synchronized void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public long getSamplingPeriodUs() {
        return samplingPeriodUs;
    }

    public void setSamplingPeriodUs(long samplingPeriodUs) {
        this.samplingPeriodUs = samplingPeriodUs;
    }

    @Override
    public void run() {
        if (msg!=null&&!msg.equals("")){
            data.setRecord(msg);
            EventBus.getDefault().post(new SensorEvent(NetworkStateReceiver.getIp(), data));
        }
        handler.postDelayed(this, samplingPeriodUs);
    }
}
