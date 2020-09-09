package com.example.sensorexample.ws;

import android.util.Log;

import com.example.sensorexample.sensor.SensorInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WSHandler{

    private static String baseUrl;

    private final static Map<String, WebSocket> webSockets;

    private final static OkHttpClient client;

    static {
        //静态代码块初始化缓存池和okhttp客户端
        webSockets = new HashMap<>();
        client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();
    }

    private WSHandler(){

    }

    public static void bind(String url){
        baseUrl = url;
        closeAll();
    }

    public static void connect(String type) {
        //构造request对象
        connect(type, new WSListener());
    }

    public static void connect(String type, WSListener listener){
        webSockets.put(type, buildWS(type, listener));
    }

    public static WebSocket buildWS(String type, WebSocketListener listener){
        String url = "ws://"+baseUrl+"//"+type;
        Log.v("url", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Upgrade", "websocket")
                .build();

        return client.newWebSocket(request, listener);
    }

    public static void reConnect(String type) {
        WebSocket websocket = webSockets.get(type);
        if (webSockets.containsKey(type)&&websocket!=null) {
            websocket = client.newWebSocket(Objects.requireNonNull(webSockets.get(type)).request(), new WSListener());
            webSockets.put(type, websocket);
        }else{
            connect(type);
        }
    }

    public static void send(String type, String msg) {
        WebSocket webSocket = webSockets.get(type);
        if (webSocket != null) {
            webSocket.send(msg);
        }
    }

    public static void cancel(String type) {
        WebSocket webSocket = webSockets.get(type);
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    public static void close(String type) {
        WebSocket webSocket = webSockets.get(type);
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }

    public static void closeAll(){
        for (String type:webSockets.keySet()){
            close(type);
        }
    }

    public static void connectAll(){
        closeAll();
        for (String type: SensorInfo.getSensorNames()){
            connect(type);
        }
    }
}
