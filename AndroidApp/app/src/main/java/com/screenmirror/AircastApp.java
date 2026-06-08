package com.screenmirror;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

/**
 * Applies the user's saved theme preference at process launch so the very
 * first activity is inflated against the correct day/night configuration
 * (no flicker, no recreate). Defaults to dark to preserve the existing UI
 * for users who installed before this update.
 *
 * Also kicks off Mobile Ads SDK initialization here so the first ad
 * request from MainActivity doesn't pay the init cost on the hot path.
 * MobileAds.initialize is itself non-blocking (returns immediately, runs
 * a worker thread internally) so this is cheap.
 */
public class AircastApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.applyThemeMode(new AppPreferences(this).getThemeMode());

        // Fire-and-forget; SDK initializes on a background thread. The
        // callback is intentionally a no-op — the banner ad request in
        // MainActivity handles the case where SDK isn't yet ready by
        // queueing internally.
        MobileAds.initialize(this, initStatus -> {});
    }
}
