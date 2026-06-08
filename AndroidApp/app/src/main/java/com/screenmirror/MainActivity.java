package com.screenmirror;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    private Button btnSearchTVs;
    private ImageView imgHeroCast;
    private LinearLayout rateRow;
    private View howToUseRow;
    private LinearLayout themeSwitchRow;
    private MaterialSwitch themeSwitch;
    private TextView themeSwitchLabel;
    private LinearLayout languageRow;
    private TextView languageLabel;
    private AppPreferences prefs;
    private boolean syncingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new AppPreferences(this);
        AppPreferences.applyThemeMode(prefs.getThemeMode());

        super.onCreate(savedInstanceState);

        if (!prefs.isWalkthroughDone()) {
            startActivity(new Intent(this, WalkthroughActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        imgHeroCast = findViewById(R.id.imgHeroCast);

        btnSearchTVs = findViewById(R.id.btnSearchTVs);
        btnSearchTVs.setOnClickListener(v -> openNativeSettings());

        rateRow = findViewById(R.id.rateRow);
        rateRow.setOnClickListener(v -> RatingHelper.openPlayStoreListing(this));

        howToUseRow = findViewById(R.id.howToUseRow);
        howToUseRow.setOnClickListener(v -> {
            Intent i = new Intent(this, WalkthroughActivity.class);
            i.putExtra(WalkthroughActivity.EXTRA_FROM_HELP, true);
            startActivity(i);
        });

        themeSwitchRow = findViewById(R.id.themeSwitchRow);
        themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitchLabel = findViewById(R.id.themeSwitchLabel);

        boolean dark = isInDarkMode();
        syncingSwitch = true;
        themeSwitch.setChecked(!dark);
        syncingSwitch = false;
        updateLabel(dark);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (syncingSwitch) return;
            int newMode = isChecked ? AppPreferences.THEME_LIGHT : AppPreferences.THEME_DARK;
            prefs.setThemeMode(newMode);
        });

        themeSwitchRow.setOnClickListener(v -> themeSwitch.toggle());

        languageRow = findViewById(R.id.languageRow);
        languageLabel = findViewById(R.id.languageLabel);
        languageLabel.setText(LanguagePicker.currentNativeName());
        languageRow.setOnClickListener(v -> LanguagePicker.show(this));

        loadBannerAd();
    }

    /**
     * Inflates an adaptive banner AdView into the adSlotTop FrameLayout
     * and requests one ad. Uses anchored adaptive sizing — banner height
     * is calculated from the screen width so the visual presence matches
     * the device. On a typical phone this yields a 50–100dp banner that
     * fits inside the 90dp reservation in activity_main.xml.
     *
     * Failure modes: SDK init not finished (the ad request queues and
     * fires later — fine), no network (fails silently, slot stays empty),
     * unfilled (no ad available — slot stays empty). None of these crash.
     */
    private void loadBannerAd() {
        FrameLayout adSlot = findViewById(R.id.adSlotTop);
        if (adSlot == null) return;

        AdView adView = new AdView(this);
        adView.setAdUnitId(BuildConfig.ADMOB_BANNER_UNIT_ID);
        adView.setAdSize(adaptiveBannerSize());
        adSlot.removeAllViews();
        adSlot.addView(adView);

        adView.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Computes the anchored adaptive banner ad size for the current
     * screen width. Recommended by AdMob over the legacy SMART_BANNER
     * constant. Returns a size whose height is optimal for the device.
     */
    private AdSize adaptiveBannerSize() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int widthDp = Math.round(dm.widthPixels / dm.density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, widthDp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kick the AVD on every visible attach. start() is idempotent.
        if (imgHeroCast != null) {
            Drawable d = imgHeroCast.getDrawable();
            if (d instanceof Animatable) {
                ((Animatable) d).start();
            }
        }
    }

    private boolean isInDarkMode() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    private void updateLabel(boolean dark) {
        if (themeSwitchLabel != null) {
            themeSwitchLabel.setText(dark
                    ? R.string.theme_switch_to_light
                    : R.string.theme_switch_to_dark);
        }
    }

    private void openNativeSettings() {
        try {
            startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
        } catch (Exception e) {
            try {
                startActivity(new Intent(Settings.ACTION_CAST_SETTINGS));
            } catch (Exception ignored) {
                try {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } catch (Exception ex) {
                    Toast.makeText(this, R.string.toast_cast_settings_unavailable, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
