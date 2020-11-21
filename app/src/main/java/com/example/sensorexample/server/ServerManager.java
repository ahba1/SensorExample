package com.example.sensorexample.server;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.example.sensorexample.exception.UnsupportedSpuException;
import com.example.sensorexample.exception.UnsupportedTaskException;
import com.example.sensorexample.sensor.Task;
import com.example.sensorexample.sensor.WsServiceWrapper;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ServerManager extends WsServiceWrapper implements ServerProxy {

    private final Context context;
    private static Server server;

    public ServerManager(Context context, int port){
        this.context = context;
        server = server == null? new Server(port): server;
    }

    @Override
    protected void send(String msg) {
        server.sendMsg(msg);
    }

    @Override
    public void startHttpService() {
        server.startHttpService();
    }

    @Override
    public void startWebSocketService() {
        server.startWebSocketService();
    }

    @Override
    public void closeHttpService() {
        server.closeHttpService();
    }

    @Override
    public void closeWebSocketService() {
        server.closeWebSocketService();
    }

    @Override
    public void closeAll() {
        server.closeAll();
    }

    @Override
    public void setTaskCallBack(TaskCallBack callBack){
        server.setTaskCallBack(callBack);
    }

    private Task parse(JSONObject o){
        return JSON.parseObject(o.toString(), Task.class);
    }

    public interface TaskCallBack{
        void onTaskReceived(Task task) throws UnsupportedTaskException, UnsupportedSpuException;
    }

    class Server implements ServerProxy{

        private final AsyncHttpServer server;
        private final Set<WebSocket> clients;
        private int port;
        private TaskCallBack callBack;

        Server(int port){
            server = new AsyncHttpServer();
            clients = new HashSet<>();
            this.port = port;
            server.listen(AsyncServer.getDefault(), port);
        }

        @Override
        public void setTaskCallBack(TaskCallBack callBack){
            this.callBack = callBack;
        }

        @Override
        public void startHttpService() {
            server.get("/", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    response.setContentType("text/html;charset=utf-8");
                    InputStream fis = null;
                    try {
                        fis = context.getAssets().open("browser.html");
                        response.sendStream(fis, fis.available());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if (fis!=null){
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            server.post("/control", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    String contentType = request.getBody().getContentType();
                    if ("application/json".equals(contentType)){
                        JSONObject body = ((JSONObjectBody)request.getBody()).get();
                        Task task = parse(body);
                        //回调接口启动task
                        try {
                            callBack.onTaskReceived(task);
                            response.code(200).send(Response.success("success", task).toString());
                        } catch (UnsupportedTaskException | UnsupportedSpuException e) {
                            response.code(404).send(Response.clientError(e.getMessage(), task).toString());
                        }
                    }else {
                        response.code(404).send(Response.clientError("wrong contentType", contentType).toString());
                    }
                }
            });
        }

        @Override
        public void startWebSocketService() {
            server.websocket("/data", new AsyncHttpServer.WebSocketRequestCallback() {
                @Override
                public void onConnected(WebSocket webSocket, AsyncHttpServerRequest request) {
                    clients.add(webSocket);
                }
            });
        }

        @Override
        public void closeHttpService() {
            server.stop();
        }

        @Override
        public void closeWebSocketService() {
            server.stop();
            sendMsg("websocket has been closed by the server");
        }

        @Override
        public void closeAll() {
            closeHttpService();
            closeWebSocketService();
            server.stop();
        }

        public void sendMsg(String msg){
            for(WebSocket ws:clients){
                ws.send(msg);
            }
        }
    }
}
