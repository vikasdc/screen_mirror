package com.screenmirror;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSearchTVs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Binding
        btnSearchTVs = findViewById(R.id.btnSearchTVs);

        // Primary Search Button - Triggers Native Cast Settings
        btnSearchTVs.setOnClickListener(v -> openNativeSettings());
    }

    private void openNativeSettings() {
        try {
            // First attempt: Specific Wireless Display settings
            startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
        } catch (Exception e) {
            try {
                // Second attempt: Standard Cast settings
                startActivity(new Intent(Settings.ACTION_CAST_SETTINGS));
            } catch (Exception ignored) {
                // Fallback: General Wireless settings
                try {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } catch (Exception ex) {
                    Toast.makeText(this, "Please open Cast settings in your System Menu.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
