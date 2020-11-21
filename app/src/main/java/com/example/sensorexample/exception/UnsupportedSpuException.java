package com.example.sensorexample.exception;

public class UnsupportedSpuException extends SensorException{

    public UnsupportedSpuException(long minSpu, long usedSpu){
        super("spu cannot be smaller than "+minSpu+", current spu is "+usedSpu);
    }
}
