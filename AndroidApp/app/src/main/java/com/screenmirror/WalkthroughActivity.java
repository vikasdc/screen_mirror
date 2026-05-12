package com.screenmirror;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

/**
 * First-run onboarding tour — also re-launchable from the home screen "How to
 * use" entry. Six pages:
 *   1. Language picker: choose the app language up-front
 *   2. Intro: phone-to-TV concept
 *   3. Brand grid: works with most TVs and streaming devices
 *   4. TV setup: brand-aware paths to enable Wireless Display
 *   5. Same Wi-Fi: with phone-hotspot fallback for users without home Wi-Fi
 *   6. Start mirroring: open Aircast, tap Search, pick a TV
 *
 * Skip and Get Started both mark the walkthrough as completed and route the
 * user to MainActivity. When launched from "How to use" ({@code EXTRA_FROM_HELP}
 * is true), finish back to the existing MainActivity instead of starting a new
 * one.
 */
public class WalkthroughActivity extends AppCompatActivity {

    public static final String EXTRA_FROM_HELP = "from_help";

    private static final int VIEW_LANGUAGE = 0;
    private static final int VIEW_STANDARD = 1;
    private static final int VIEW_BRANDS = 2;
    private static final int VIEW_TV_SETUP = 3;
    private static final int VIEW_WIFI = 4;
    private static final int VIEW_CAST = 5;

    private static class Page {
        final int viewType;
        final int titleRes;
        final int bodyRes;
        final int iconRes;

        Page(int viewType, int titleRes, int bodyRes, int iconRes) {
            this.viewType = viewType;
            this.titleRes = titleRes;
            this.bodyRes = bodyRes;
            this.iconRes = iconRes;
        }
    }

    private static final Page[] PAGES = {
            new Page(VIEW_LANGUAGE, 0, 0, 0),
            new Page(VIEW_STANDARD, R.string.walk_intro_title, R.string.walk_intro_body,
                    R.drawable.ic_walk_phone_to_tv),
            new Page(VIEW_BRANDS, 0, 0, 0),
            new Page(VIEW_TV_SETUP, 0, 0, 0),
            new Page(VIEW_WIFI, 0, 0, 0),
            new Page(VIEW_CAST, 0, 0, 0)
    };

    private ViewPager2 pager;
    private LinearLayout dotsHost;
    private Button btnNext;
    private Button btnSkip;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new AppPreferences(this);
        AppPreferences.applyThemeMode(prefs.getThemeMode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        pager = findViewById(R.id.walkPager);
        dotsHost = findViewById(R.id.walkDots);
        btnNext = findViewById(R.id.btnWalkNext);
        btnSkip = findViewById(R.id.btnWalkSkip);

        pager.setAdapter(new WalkAdapter());
        buildDots();
        updateForPage(0);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateForPage(position);
            }
        });

        btnSkip.setOnClickListener(v -> finishWalkthrough());
        btnNext.setOnClickListener(v -> {
            int current = pager.getCurrentItem();
            if (current < PAGES.length - 1) {
                pager.setCurrentItem(current + 1, true);
            } else {
                finishWalkthrough();
            }
        });
    }

    private void buildDots() {
        dotsHost.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int dotSize = (int) (10 * density);
        int dotMargin = (int) (4 * density);
        for (int i = 0; i < PAGES.length; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dotSize, dotSize);
            lp.setMargins(dotMargin, 0, dotMargin, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.dot_inactive);
            dotsHost.addView(dot);
        }
    }

    private void updateForPage(int position) {
        for (int i = 0; i < dotsHost.getChildCount(); i++) {
            dotsHost.getChildAt(i).setBackgroundResource(
                    i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
        boolean lastPage = position == PAGES.length - 1;
        btnNext.setText(lastPage ? R.string.walk_get_started : R.string.walk_next);
        btnSkip.setVisibility(lastPage ? View.INVISIBLE : View.VISIBLE);
    }

    private void finishWalkthrough() {
        prefs.setWalkthroughDone(true);
        if (getIntent().getBooleanExtra(EXTRA_FROM_HELP, false)) {
            // Launched from MainActivity's "How to use" — just return.
            finish();
        } else {
            // First-run path: start MainActivity and finish self.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    /** Populate the language picker as a 2-column grid of card tiles. The
     *  active locale's tile is styled with an emerald stroke + tinted fill +
     *  emerald text so the user sees their current selection at a glance. */
    private void bindLanguagePage(View root) {
        GridLayout grid = root.findViewById(R.id.walkLanguageList);
        if (grid == null) return;
        grid.removeAllViews();

        float density = root.getResources().getDisplayMetrics().density;
        int margin = (int) (6 * density);
        int activeStrokePx = (int) (2 * density);
        int idleStrokePx = (int) (1 * density);

        int activeStroke = ContextCompat.getColor(root.getContext(), R.color.emerald_accent);
        int activeFill = ContextCompat.getColor(root.getContext(), R.color.emerald_tonal_bg);
        int activeText = ContextCompat.getColor(root.getContext(), R.color.emerald_accent);
        int idleStroke = ContextCompat.getColor(root.getContext(), R.color.slate_card_border);
        int idleFill = ContextCompat.getColor(root.getContext(), R.color.slate_card);
        int idleText = ContextCompat.getColor(root.getContext(), R.color.walkthrough_text_primary);

        String activeTag = activeLanguageTag();
        LayoutInflater inflater = LayoutInflater.from(root.getContext());

        for (final String[] lang : LanguagePicker.LANGUAGES) {
            MaterialCardView tile = (MaterialCardView)
                    inflater.inflate(R.layout.walkthrough_language_row, grid, false);
            TextView nativeName = tile.findViewById(R.id.langNative);
            TextView englishName = tile.findViewById(R.id.langEnglish);

            nativeName.setText(lang[1]);
            englishName.setText(lang[2]);

            boolean isActive = matchesActive(lang[0], activeTag);
            tile.setStrokeColor(isActive ? activeStroke : idleStroke);
            tile.setStrokeWidth(isActive ? activeStrokePx : idleStrokePx);
            tile.setCardBackgroundColor(isActive ? activeFill : idleFill);
            nativeName.setTextColor(isActive ? activeText : idleText);

            // Equal-width columns: 0dp width with column weight = 1f shares
            // the available row width across the two cells.
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            lp.setMargins(margin, margin, margin, margin);
            tile.setLayoutParams(lp);

            tile.setOnClickListener(v -> AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(lang[0])));
            grid.addView(tile);
        }
    }

    /** Bind page 5 (cast). Step 2 references the localised home button name
     *  so the walkthrough always matches what the user will see in the app. */
    private void bindCastPage(View root) {
        TextView step2 = root.findViewById(R.id.walkCastStep2);
        if (step2 != null) {
            step2.setText(getString(R.string.walk_cast_step2,
                    getString(R.string.home_search_for_tvs)));
        }
    }

    private static String activeLanguageTag() {
        LocaleListCompat current = AppCompatDelegate.getApplicationLocales();
        Locale active = current.isEmpty() ? Locale.getDefault() : current.get(0);
        return active == null ? "" : active.toLanguageTag();
    }

    /** Match a candidate BCP-47 tag against the active tag, falling back to
     *  language-prefix match when the active tag carries a region we don't
     *  ship (e.g. system pt-PT against our pt-BR). */
    private static boolean matchesActive(String candidate, String activeTag) {
        if (candidate.equalsIgnoreCase(activeTag)) return true;
        String activeLang = activeTag.split("-", 2)[0];
        if ("id".equalsIgnoreCase(activeLang)) activeLang = "in";
        String candLang = candidate.split("-", 2)[0];
        return candLang.equalsIgnoreCase(activeLang);
    }

    /**
     * If the given drawable is an animated vector, kick off its animation. The
     * AVDs in the walkthrough illustrations don't start automatically.
     */
    private static void startAnimationIfNeeded(ImageView imageView) {
        if (imageView == null) return;
        Drawable d = imageView.getDrawable();
        if (d instanceof Animatable) {
            ((Animatable) d).start();
        }
    }

    private class WalkAdapter extends RecyclerView.Adapter<WalkVH> {

        @Override
        public int getItemViewType(int position) {
            return PAGES[position].viewType;
        }

        @NonNull
        @Override
        public WalkVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutRes;
            switch (viewType) {
                case VIEW_LANGUAGE:
                    layoutRes = R.layout.walkthrough_page_language;
                    break;
                case VIEW_BRANDS:
                    layoutRes = R.layout.walkthrough_page_brands;
                    break;
                case VIEW_TV_SETUP:
                    layoutRes = R.layout.walkthrough_page_tv_setup;
                    break;
                case VIEW_WIFI:
                    layoutRes = R.layout.walkthrough_page_wifi;
                    break;
                case VIEW_CAST:
                    layoutRes = R.layout.walkthrough_page_cast;
                    break;
                case VIEW_STANDARD:
                default:
                    layoutRes = R.layout.walkthrough_page;
                    break;
            }
            View v = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
            return new WalkVH(v, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull WalkVH holder, int position) {
            Page page = PAGES[position];
            if (page.viewType == VIEW_STANDARD && holder.icon != null) {
                holder.icon.setImageResource(page.iconRes);
                holder.title.setText(page.titleRes);
                holder.body.setText(page.bodyRes);
            } else if (page.viewType == VIEW_LANGUAGE) {
                bindLanguagePage(holder.itemView);
            } else if (page.viewType == VIEW_CAST) {
                bindCastPage(holder.itemView);
            }
            // Kick off any animated vector drawable on this page. Pages that
            // have an animated illustration give it id @+id/walkAnimatedIcon.
            View animated = holder.itemView.findViewById(R.id.walkAnimatedIcon);
            if (animated instanceof ImageView) {
                startAnimationIfNeeded((ImageView) animated);
            }
            startAnimationIfNeeded(holder.icon);
        }

        @Override
        public int getItemCount() {
            return PAGES.length;
        }
    }

    private static class WalkVH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView body;

        WalkVH(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_STANDARD) {
                icon = itemView.findViewById(R.id.walkIcon);
                title = itemView.findViewById(R.id.walkTitle);
                body = itemView.findViewById(R.id.walkBody);
            }
        }
    }
}
