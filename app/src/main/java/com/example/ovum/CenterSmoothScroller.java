package com.example.ovum;
import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;

public class CenterSmoothScroller extends LinearSmoothScroller {

    public CenterSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        int boxCenter = (boxEnd - boxStart) / 2;
        int boxScreenCenter = boxStart + boxCenter;
        int viewCenter = (viewEnd - viewStart) / 2;
        int viewScreenCenter = viewStart + viewCenter;
        return boxScreenCenter - viewScreenCenter;
    }
}