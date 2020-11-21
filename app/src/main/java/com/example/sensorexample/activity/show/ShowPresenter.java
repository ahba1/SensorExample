package com.example.sensorexample.activity.show;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

class ShowPresenter implements Contract.Presenter {

    private Contract.View view;

    ShowPresenter(Contract.View view){
        this.view  = view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void loadWebViewContent(WebView container) {
        WebSettings settings = container.getSettings();
        settings.setJavaScriptEnabled(true);
        container.loadUrl("file:android_asset/android.html");
    }
}
