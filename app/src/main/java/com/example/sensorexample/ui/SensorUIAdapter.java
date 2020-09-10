package com.example.sensorexample.ui;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.sensorexample.R;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SensorUIAdapter extends BaseQuickAdapter<String, BaseViewHolder> implements FlexibleDividerDecoration.VisibilityProvider {

    private final ArrayList<String> ls;

    public SensorUIAdapter(ArrayList<String> ls){
        super(R.layout.rv_rom, ls);
        this.ls = ls;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.mainText, s);
    }

    public int getPosByType(String type){
        return ls.indexOf(type);
    }

    @Override
    public boolean shouldHideDivider(int i, RecyclerView recyclerView) {
        return true;
    }
}
