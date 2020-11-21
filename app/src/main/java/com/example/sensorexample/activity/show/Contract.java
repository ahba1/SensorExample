package com.example.sensorexample.activity.show;

import android.webkit.WebView;

import com.example.sensorexample.mvp.BasePresenter;
import com.example.sensorexample.mvp.BaseView;

public interface Contract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter{
        void loadWebViewContent(WebView container);
    }
}
