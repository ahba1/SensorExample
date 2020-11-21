package com.example.sensorexample.exception;

public class UnsupportedTaskException extends SensorException {

    public UnsupportedTaskException(){
        super("undefined control command");
    }

}
