package com.example.sensorexample.activity.show;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sensorexample.databinding.ActivityShowBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShowActivity extends AppCompatActivity implements Contract.View {

    private Contract.Presenter presenter;

    private ActivityShowBinding binding;

    public ShowActivity(){
        this.presenter = new ShowPresenter(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        presenter.loadWebViewContent(binding.wvContainer);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }
}
