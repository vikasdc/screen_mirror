package com.screenmirror;

import android.app.Application;

/**
 * Applies the user's saved theme preference at process launch so the very
 * first activity is inflated against the correct day/night configuration
 * (no flicker, no recreate). Defaults to dark to preserve the existing UI
 * for users who installed before this update.
 */
public class AircastApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.applyThemeMode(new AppPreferences(this).getThemeMode());
    }
}
