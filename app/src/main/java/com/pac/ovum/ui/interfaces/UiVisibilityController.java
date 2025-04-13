package com.pac.ovum.ui.interfaces;

/**
 * Interface to allow fragments to control UI visibility in parent activity
 */
public interface UiVisibilityController {
    /**
     * Hides the bottom navigation and FAB
     */
    void hideBottomNavAndFab();

    /**
     * Shows the bottom navigation and FAB
     */
    void showBottomNavAndFab();
} 