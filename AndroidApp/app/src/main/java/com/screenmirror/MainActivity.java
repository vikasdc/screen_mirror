package com.screenmirror;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
