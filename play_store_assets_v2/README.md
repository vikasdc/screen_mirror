# Aircast Play Store assets

1080×1920 screenshots and 30-second promo videos for the Google Play
listing. Designed in HTML/CSS, rendered through headless Chromium.

## v3 — current recommended set

Direction synthesised from a research swarm: CRED-school NeoPOP flat
brand-colour panels, JetBrains Mono ExtraBold caps, Erode Light Italic
for warmth, the real Aircast overlapping-rectangle logo, brand tiles
where the wordmarks actually fill the tile space. Anti-AI-slop: no
Instrument Serif, no Inter, no saturated radial gradients, no Vercel-
template editorial-dark cliche.

### Screenshots (carousel order)

| File | Concept | Surface |
|------|---------|---------|
| `out/v3_1_hero.png`    | Mono caps "MIRROR YOUR PHONE TO ANY TV." with green/blue accent words. Real Aircast logo top-left. Phone + TV mirror with cinematic landscape. Live spec tags. | Paper + grid |
| `out/v3_2_brands.png`  | "EVERY TV. EVERY STICK." 3×3 grid of saturated brand-colour tiles. Wordmarks fill 75-85% of each tile. Samsung blue, LG red, Sony black, Fire TV orange, Chromecast Google blue, Roku purple, Hisense teal, Mi orange, Android TV green. | Paper + grid |
| `out/v3_3_onetap.png`  | "TAP. PICK. DONE." (with DONE in green). Erode italic dek. Phone showing cast picker with Samsung Living Room selected. ELAPSED 00:00:02 stat tag. | Dark navy |
| `out/v3_4_proof.png`   | Pull-quote "Took longer to find the remote than to set this up." with — PLAY STORE REVIEW ★★★★★. Phone + TV pair as visible proof. NeoPOP chunky offset shadow on the device frames. | Brand green |
| `out/v3_5_speed.png`   | Massive numeral "1" filling 60% of canvas with NeoPOP chunky offset shadow. Erode italic "tap to cast. that's it." Small phone in lower-right with Search-for-TVs button highlighted. AVG SETUP · 2 SECONDS tag. | Brand blue |
| `out/v3_6_honest.png`  | Real Aircast logo big and centred. "FREE. WITH ADS." headline (truthful, not buried). Black "GET IT ON GOOGLE PLAY" CTA. Footer tags: 15 LANGUAGES · ANDROID 7+. | Paper |

### Promo video

**`out/aircast_tutorial_v3.mp4`** — 30-second advanced tutorial.

Seven scenes, continuous spatial narrative (per the agent brief: every
transition is motivated, things morph/match-on-action instead of
fading):

1. 0-3s — Particles drift in from all four edges, magnetise to centre,
   assemble into the real overlapping-rectangle Aircast logo + wordmark.
2. 3-7s — Phone slides in on an arc, overshoots, settles on a warm
   interior plate. Caption "MIRROR YOUR PHONE — to any TV, instantly".
3. 7-12s — Phone rotates to `perspective(1600px) rotateY(8deg)`.
   Skin-tone thumb sprite fades in over the Search-for-TVs button.
   Mint tap ripple fires. Caption "TAP TO SEARCH — no setup, no
   account".
4. 12-17s — Cast picker modal emerges. Rows stagger in with
   `cubic-bezier(0.34, 1.56, 0.64, 1)` overshoot. Samsung Living Room
   row shown selected with green check. Caption "PICK YOUR TV — five
   devices found in 0.4s".
5. 17-23s — Phone left, TV right. SVG beam draws from phone screen to
   TV screen with green→blue gradient stroke. Particles travel along
   the beam path via `animateMotion`. Caption "MIRRORING LIVE — 1080p ·
   <200ms latency".
6. 23-27s — Hero shot: TV scales to fill ~60% of frame, phone in lower-
   right at 30% scale. Slow rotateY orbit. Caption "ANY PHONE. ANY TV.
   one tap.".
7. 27-30s — End card on paper. Real Aircast logo with green rect, then
   blue rect, fading in with stagger. Wordmark "AIRCAST". Erode italic
   tagline "Cast in one tap. Free, with ads." Black "GET IT ON GOOGLE
   PLAY" CTA with green play arrow.

Upload to YouTube → paste URL into Play Console's Promo Video field.

### Design system tokens

- **Colours**: Aircast green `#34A853`, Aircast blue `#4285F4`, ink
  `#0F1A2E`, paper `#F5F1E8`. The green + blue ARE the brand (they
  come from the app icon — two overlapping rounded rectangles
  representing phone + TV).
- **Type**: JetBrains Mono ExtraBold 800 for headlines (mono caps,
  -2% tracking, 0.95 line-height). Erode Light Italic 300i for
  warmth in subheads + pull quotes. Both free under SIL OFL /
  ITF Free Font License — explicitly licensed for commercial use
  including Play Store assets.
- **Logo**: two overlapping rounded rectangles, green `#34A853` (the
  phone screen) overlapping blue `#4285F4` (the TV display). Always
  used at the proper aspect — never substituted with a letter.
- **Brand voice**: app is free, with ads. Never claim "ad-free" or
  "no ads". Truthful value props: "no account", "no subscription",
  "free", "no setup wizard".

## v2 — legacy editorial-dark direction (kept for reference)

`out/v2_*.png` and `out/aircast_tutorial.mp4` — first redesign pass.
Used Instrument Serif + Inter (the AI-slop pair) and editorial dark
gradients. Superseded by v3. Safe to delete once v3 is uploaded to
Play Console.

## v1 — original saturated-gradient set (kept for reference)

`out/1_hero.png` through `out/6_hd.png` plus `aircast_promo.mp4`.
Initial pass using the same saturated orange→pink→purple gradients as
every competitor. Superseded by v3.

## Regenerate

```sh
# Install once
npm install playwright
npx playwright install chromium
pip install imageio-ffmpeg

# Render all six v3 screenshots
node render_v3.js

# Record the v3 tutorial video (30s WebM)
node record_tutorial_v3.js

# Convert WebM → H.264 MP4
ffmpeg -i out/aircast_tutorial_v3.webm \
       -c:v libx264 -pix_fmt yuv420p -preset slow -crf 20 \
       -movflags +faststart out/aircast_tutorial_v3.mp4
```
