package com.screenmirror;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Opens the Aircast Play Store listing so the user can leave a rating.
 * Tries the Play Store app first (market://), falls back to the web URL.
 */
public final class RatingHelper {

    private RatingHelper() {}

    public static void openPlayStoreListing(Context context) {
        String packageName = context.getPackageName();
        try {
            Intent market = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName));
            market.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(market);
        } catch (ActivityNotFoundException e) {
            try {
                Intent web = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(web);
            } catch (Exception ex) {
                Toast.makeText(context,
                        "Play Store is not available on this device.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
