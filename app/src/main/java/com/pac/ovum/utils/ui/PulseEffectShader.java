package com.pac.ovum.utils.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.View;

public class PulseEffectShader {

    // Applies the pulsing animation to the given ImageView
    @SuppressLint("ObjectAnimatorBinding")
    public static void applyPulsingEffect(View imageView, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "alpha", 0.2f, 1.0f, 0.2f);
        animator.setDuration(duration);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    // Configures the background and animation based on days left
    public static void configureImage(View image, long daysLeftCount, int lateDrawableRes, int dueDrawableRes) {
        if (daysLeftCount < 1) {
            // For late period
            image.setBackgroundResource(lateDrawableRes);
            applyPulsingEffect(image, 500); // Faster pulsing for urgency
        } else if (daysLeftCount <= 5) {
            // For due soon
            image.setBackgroundResource(dueDrawableRes);
            applyPulsingEffect(image, 1000); // Slower pulsing
        }
    }
}

