package com.example.sensorexample.ui;

import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.sensorexample.R;
import com.suke.widget.SwitchButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SensorUIAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Set<String> ls;

    public SensorUIAdapter(Set<String> ls){
        super(R.layout.rv_rom, new ArrayList<String>(ls));
        this.ls = ls;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.mainText, s);
    }
}
