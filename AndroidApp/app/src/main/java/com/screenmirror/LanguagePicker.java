package com.screenmirror;

import android.app.Activity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * In-app language picker. Calls into AppCompat's per-app locale API which
 * routes through the system LocaleManager on Android 13+ and through
 * AppLocalesMetadataHolderService on Android 7-12. Selection persists
 * across process death and triggers Activity recreation automatically.
 */
public final class LanguagePicker {

    /** {BCP-47 tag, native display name, English name}. Order = picker order. */
    static final String[][] LANGUAGES = new String[][]{
            {"en",    "English",     "English"},
            {"es",    "Español",     "Spanish"},
            {"pt-BR", "Português",   "Portuguese"},
            {"hi",    "हिन्दी",         "Hindi"},
            {"in",    "Bahasa",      "Indonesian"},
            {"ru",    "Русский",     "Russian"},
            {"de",    "Deutsch",     "German"},
            {"fr",    "Français",    "French"},
            {"it",    "Italiano",    "Italian"},
            {"ja",    "日本語",        "Japanese"},
            {"ko",    "한국어",        "Korean"},
            {"zh-CN", "中文",          "Chinese"},
            {"tr",    "Türkçe",      "Turkish"},
            {"vi",    "Tiếng Việt",  "Vietnamese"},
            {"ar",    "العربية",       "Arabic"},
    };

    private LanguagePicker() {}

    /** Open the picker. Selecting an item applies the locale app-wide and
     *  forces Activity recreation so the new locale takes effect immediately. */
    public static void show(Activity activity) {
        String[] items = new String[LANGUAGES.length];
        for (int i = 0; i < LANGUAGES.length; i++) {
            items[i] = LANGUAGES[i][1] + "   ·   " + LANGUAGES[i][2];
        }

        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.language_picker_title)
                .setSingleChoiceItems(items, currentIndex(), (dialog, which) -> {
                    String tag = LANGUAGES[which][0];
                    dialog.dismiss();
                    applyLocale(activity, tag);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Apply a new app locale and force the host Activity to recreate.
     *
     *  Why explicit recreate: on Android 13+ with AppCompat 1.6,
     *  setApplicationLocales schedules a configuration change but the recreate
     *  is sometimes dropped when the Activity was PAUSED (a dialog had focus).
     *  Symptom: the chip label updates from currentNativeName() but the rest
     *  of the UI keeps stale (old-locale) strings until the next app launch.
     *
     *  The 50ms post gives the system LocaleManager time to commit the new
     *  locale before recreate() fires, so the new Activity instance attaches
     *  with the correct Configuration.
     */
    public static void applyLocale(Activity activity, String tag) {
        AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(tag));
        activity.getWindow().getDecorView().postDelayed(activity::recreate, 50);
    }

    /** Native-script name of the active language (e.g. "हिन्दी" when in Hindi),
     *  for showing in the home-screen chip. */
    public static String currentNativeName() {
        return LANGUAGES[currentIndex()][1];
    }

    private static int currentIndex() {
        LocaleListCompat current = AppCompatDelegate.getApplicationLocales();
        Locale active = current.isEmpty() ? Locale.getDefault() : current.get(0);
        if (active == null) return 0;

        // 1. Exact BCP-47 tag match (case-insensitive).
        String activeTag = active.toLanguageTag();
        for (int i = 0; i < LANGUAGES.length; i++) {
            if (LANGUAGES[i][0].equalsIgnoreCase(activeTag)) return i;
        }

        // 2. Language prefix match. Normalise deprecated codes: id->in, iw->he.
        String lang = active.getLanguage();
        if ("id".equals(lang)) lang = "in";
        if ("iw".equals(lang)) lang = "he";
        for (int i = 0; i < LANGUAGES.length; i++) {
            String prefix = LANGUAGES[i][0].split("-", 2)[0];
            if (prefix.equalsIgnoreCase(lang)) return i;
        }

        // 3. Fall back to English.
        return 0;
    }
}
