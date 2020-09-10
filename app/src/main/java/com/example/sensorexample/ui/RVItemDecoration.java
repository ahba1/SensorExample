package com.example.sensorexample.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVItemDecoration extends RecyclerView.ItemDecoration {

    //dp
    private int bottomSpaceValue = 20;
    private int topSpaceValue = 20;
    private int leftSpaceValue = 0;
    private int rightSpaceValue = 0;

    public int getBottomSpaceValue() {
        return bottomSpaceValue;
    }

    public void setBottomSpaceValue(int bottomSpaceValue) {
        this.bottomSpaceValue = bottomSpaceValue;
    }

    public int getTopSpaceValue() {
        return topSpaceValue;
    }

    public void setTopSpaceValue(int topSpaceValue) {
        this.topSpaceValue = topSpaceValue;
    }

    public int getLeftSpaceValue() {
        return leftSpaceValue;
    }

    public void setLeftSpaceValue(int leftSpaceValue) {
        this.leftSpaceValue = leftSpaceValue;
    }

    public int getRightSpaceValue() {
        return rightSpaceValue;
    }

    public void setRightSpaceValue(int rightSpaceValue) {
        this.rightSpaceValue = rightSpaceValue;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(leftSpaceValue, topSpaceValue, rightSpaceValue, bottomSpaceValue);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
