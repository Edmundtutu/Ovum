package com.example.ovum.ui;

import android.os.SystemClock;
import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {

    private long timestampLastClick = 0L;  // Last click timestamp
    private final long doubleClickThreshold = 200;  // Threshold in ms

    @Override
    public void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();  // Current time in ms

        if (currentTime - timestampLastClick < doubleClickThreshold) {
            // Double-click detected
            onDoubleClick(v);
        } else {
            // Single-click detected
            onSingleClick(v);
        }

        timestampLastClick = currentTime;  // Update last click timestamp
    }

    // Abstract methods for single and double click behaviors
    public abstract void onDoubleClick(View v);
    public abstract void onSingleClick(View v);
}

