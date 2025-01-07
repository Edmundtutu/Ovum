package com.pac.ovum.utils.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import com.pac.ovum.R;

/**
 * Utility class for creating and managing balloons.
 */
public class BalloonUtil {

    /**
     * Creates a balloon with the specified context.
     *
     * @param context the context to use for creating the balloon
     * @return a configured Balloon instance
     */
    public static Balloon createBalloonForHorizontalCalendar(@NonNull Context context) {
        return new Balloon.Builder(context)
                .setLayout(R.layout.bubble) // Your balloon layout
                .setArrowPosition(0.5f)
                .setCornerRadius(8f)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
//                .setLifecycleOwner(null) // Set lifecycle owner if needed
                .build();
    }
}