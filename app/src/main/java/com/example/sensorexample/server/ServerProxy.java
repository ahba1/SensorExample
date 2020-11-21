package com.example.sensorexample.server;

public interface ServerProxy {

    void startHttpService();

    void startWebSocketService();

    void closeHttpService();

    void closeWebSocketService();

    void closeAll();

    void setTaskCallBack(ServerManager.TaskCallBack callBack);

}
