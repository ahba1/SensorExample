package com.example.sensorexample.server;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Response<T> clientError(String msg, T data){
        return new Response<>(404, msg, data);
    }

    public static <T> Response<T> serverError(String msg, T data){
        return new Response<>(500, msg, data);
    }

    public static <T> Response<T> success(String msg, T data){
        return new Response<>(200, msg, data);
    }
}
