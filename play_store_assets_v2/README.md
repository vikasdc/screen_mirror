# Aircast Play Store assets

Generated 1080×1920 phone screenshots and 30-second promo videos for the
Google Play listing.

## Final asset set (v2)

Use these for the next Play Store upload. Designed in HTML/CSS and rendered
through headless Chromium; sources live in `html/`.

### Screenshots (carousel order)

| File | Concept | Surface | Caption |
|------|---------|---------|---------|
| `out/v2_1_hero.png`        | Phone + TV mirroring, asymmetric editorial | dark  | "Phone, on _TV._" |
| `out/v2_2_livingroom.png`  | Lifestyle: living-room interior, hand-held phone implied | dark | "Same screen. _Bigger_ moment." |
| `out/v2_3_brands.png`      | Compatibility wordmark lockup, brand names as huge type | cream | "Works with the TV you _already own._" |
| `out/v2_4_languages.png`   | "Cast" in 15 languages as a typographic mosaic | cream | "The word for _cast,_ in your language." |
| `out/v2_5_kitchen.png`     | Second hand-held lifestyle, bedroom mirror demo | dark | "Lie back. Watch _bigger._" |
| `out/v2_6_theme.png`       | Diagonal dark/light split, abstract UI cards | hybrid | "Night / Day" |

### Promo videos

- **`out/aircast_tutorial.mp4`** — 30-second walkthrough showing the actual flow:
  home → tap Search → cast picker → pick Samsung → mirror live → end card.
  Use this in the Play Console Promo Video field (via YouTube upload).
- `out/aircast_promo.mp4` — 30-second carousel of v1 promo scenes. Kept as a
  spare; the tutorial is the recommended primary.

Both videos are H.264 MP4 + WebM (Playwright capture). Either format uploads
to YouTube.

### Brand voice notes (for future iterations)

- **The app contains ads.** Never claim "no ads", "ad-free", or "no trackers"
  in any marketing asset — would be misinformation. Truthful value props
  acceptable: "no sign-up", "no account", "no email needed", "free to
  download", "works offline".
- Typography: Instrument Serif (display, regular weight, italic accent on one
  word per headline) + Inter (body, 400/500/600). Both free, on Google Fonts.
- Palette: editorial dark (`#0B0D10` bg, `#F4F4F2` text, `#7AE0C2` mint
  accent, `#FF6B5B` warm signal) and cream paper (`#F4EFE7` bg, `#1B1A17`
  text, `#2E5D4F` deep eucalyptus accent). Avoid the saturated
  orange→pink→purple gradient that every competitor cast app uses.

## Regenerate

```sh
# Install deps once
npm install playwright
npx playwright install chromium

# Render all six screenshots
node render_v2.js

# Record the tutorial video (WebM, 30s)
node record_tutorial.js

# Optional: convert WebM to H.264 MP4 (needs ffmpeg)
ffmpeg -i out/aircast_tutorial.webm \
       -c:v libx264 -pix_fmt yuv420p -preset slow -crf 20 \
       -movflags +faststart out/aircast_tutorial.mp4
```

## Legacy v1 set (kept for reference)

`out/1_hero.png` through `out/6_hd.png` are the earlier saturated-gradient
designs. Kept temporarily in case a comparison is useful. Safe to delete
once v2 is uploaded.
