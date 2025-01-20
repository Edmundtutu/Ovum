package com.pac.ovum.utils.ui;

import android.os.SystemClock;
import android.view.View;

/**
 * A utility class to handle double click events on a View.
 * This class differentiates between single and double clicks.
 */
public abstract class DoubleClickListener implements View.OnClickListener {

    private long timestampLastClick = 0L;  // Last click timestamp
    private final long doubleClickThreshold;  // Threshold in ms
    private boolean isDoubleClick = false; // Flag to indicate if a double click has occurred

    /**
     * Constructor to set the double click threshold.
     *
     * @param doubleClickThreshold The threshold in milliseconds for detecting double clicks.
     */
    public DoubleClickListener(long doubleClickThreshold) {
        this.doubleClickThreshold = doubleClickThreshold;
    }

    @Override
    public void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();  // Current time in ms

        if (currentTime - timestampLastClick < doubleClickThreshold) {
            // Double-click detected
            isDoubleClick = true; // Set the flag
            onDoubleClick(v);
        } else {
            // Single-click detected
            if (!isDoubleClick) { // Only trigger single click if no double click occurred
                onSingleClick(v);
            }
            isDoubleClick = false; // Reset the flag
        }

        timestampLastClick = currentTime;  // Update last click timestamp
    }

    /**
     * Abstract method to handle double click events.
     *
     * @param v The view that was clicked.
     */
    public abstract void onDoubleClick(View v);

    /**
     * Abstract method to handle single click events.
     *
     * @param v The view that was clicked.
     */
    public abstract void onSingleClick(View v);
}

