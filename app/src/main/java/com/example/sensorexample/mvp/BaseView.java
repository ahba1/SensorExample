package com.example.sensorexample.mvp;

import android.content.Context;

public interface BaseView<T> {
    void setPresenter(T presenter);
    Context getContext();
}
