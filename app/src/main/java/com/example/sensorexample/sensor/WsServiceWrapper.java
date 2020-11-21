package com.example.sensorexample.sensor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * websocket服务包装类，注册订阅消息
 */
public abstract class WsServiceWrapper implements WsService {

    public WsServiceWrapper(){
        EventBus.getDefault().register(this);
    }

    //子线程后台运行
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    @Override
    public void send(SensorEvent event) {
        send(event.toString());
    }

    protected abstract void send(String msg);
}
