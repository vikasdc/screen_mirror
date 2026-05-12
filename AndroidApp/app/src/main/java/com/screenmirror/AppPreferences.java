package com.screenmirror;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Wrapper around SharedPreferences for the few settings the app persists:
 * theme override and whether the first-run walkthrough has been completed.
 *
 * Default theme is THEME_DARK so existing users see no visual change after
 * the update — the slate-dark UI continues to render as before. The light
 * mode is opt-in from SettingsActivity.
 */
public class AppPreferences {

    private static final String PREFS_NAME = "aircast_prefs";
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_WALKTHROUGH_DONE = "walkthrough_done";

    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME, THEME_DARK);
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME, mode).apply();
        applyThemeMode(mode);
    }

    public static void applyThemeMode(int mode) {
        int nightMode;
        switch (mode) {
            case THEME_LIGHT:
                nightMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case THEME_SYSTEM:
                nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
            case THEME_DARK:
            default:
                nightMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    public boolean isWalkthroughDone() {
        return prefs.getBoolean(KEY_WALKTHROUGH_DONE, false);
    }

    public void setWalkthroughDone(boolean done) {
        prefs.edit().putBoolean(KEY_WALKTHROUGH_DONE, done).apply();
    }
}
