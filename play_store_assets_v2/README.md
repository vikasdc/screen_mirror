# Aircast Play Store assets

1080×1920 phone screenshots and 30-second promo videos for the Google
Play listing. Each iteration kept in its own `versions/vN/` folder so
you can compare or roll back.

```
play_store_assets_v2/
├─ versions/
│  ├─ v1/   saturated-gradient + 3x3 brand tile grid (the cliche)
│  ├─ v2/   editorial dark + Instrument Serif (AI-template look)
│  ├─ v3/   NeoPOP + JetBrains Mono caps + Erode italic (cursive flagged
│  │       as AI-slop by user)
│  └─ v4/   ← CURRENT: real brand SVG logos + JetBrains Mono + Satoshi
│           sans-serif (no italic, no cursive, no serif)
├─ phone_screens/   real app screenshots shared by all versions
└─ package.json     shared playwright dependency
```

## v4 — current recommended set

### Typography

- **Headlines / mono labels**: JetBrains Mono ExtraBold (800), caps,
  -2% tracking, 0.95 line-height. Distinctive technical voice — reads
  as "engineered, not templated."
- **Body / sub-headlines / captions**: Satoshi (Fontshare, ITF Free
  Font License — explicitly licensed for commercial use). Weight 500
  for body, 600-700 for emphasis. **No italic, no serif, no cursive
  anywhere.** This is the specific fix vs v3 where Erode italic read as
  AI-cursive.

### Brand logos

Real SVG marks downloaded from simple-icons (CC0) and vectorlogo.zone
(brand-color full-vector). 11 brands sit in `versions/v4/brand_logos/`:
samsung, lg, sony, roku, chromecast, amazon (used for Fire TV),
xiaomi (used for Mi TV), tcl, panasonic, google, amazonalexa. Used
both in the compatibility grid (`v4_2_brands.png`) and inside the
cast-picker rows of `v4_3_onetap.png` and the v4 tutorial video.

### Screenshots (carousel order)

| File | Concept | Surface |
|------|---------|---------|
| `versions/v4/out/v4_1_hero.png` | "MIRROR YOUR PHONE TO ANY TV." mono caps, green/blue accent words, phone + TV with cinematic landscape | Paper + grid |
| `versions/v4/out/v4_2_brands.png` | "EVERY TV. EVERY STICK." 3×3 grid of WHITE TILES with the actual brand SVG logos inside (Samsung's blue oval, LG's pink character, Sony's wordmark on black, Amazon's "a", Chromecast wifi-arc, Roku R, TCL, Xiaomi Mi, Panasonic) | Paper + grid |
| `versions/v4/out/v4_3_onetap.png` | "TAP. PICK. DONE." Cast-picker mock with real brand logos in each row, Samsung Living Room selected with mint highlight | Dark navy |
| `versions/v4/out/v4_4_proof.png` | Pull-quote "Took longer to find the remote than to set this up." — PLAY STORE REVIEW ★★★★★. NeoPOP chunky shadow on devices | Brand green |
| `versions/v4/out/v4_5_speed.png` | Massive numeral "1" with NeoPOP offset shadow. "Tap to cast. That's it." | Brand blue |
| `versions/v4/out/v4_6_honest.png` | Real Aircast logo big, "FREE. WITH ADS." (truthful), black "GET IT ON GOOGLE PLAY" CTA | Paper |

### Promo video

**`versions/v4/video/aircast_tutorial_v4.mp4`** — 30-second tutorial.

7 scenes, continuous spatial narrative. Real brand SVG logos visible
in the scene-4 cast picker (Samsung, LG, Amazon, Roku, Chromecast).
All captions in JetBrains Mono caps + Satoshi sub (no italic):

1. 0-3s — Particles converge from edges, assemble into real Aircast logo
2. 3-7s — Phone arrives on interior plate with overshoot
3. 7-12s — Phone tilts perspective(1600px) rotateY(8deg), thumb taps
4. 12-17s — Cast picker emerges with real brand logos, rows stagger in
5. 17-23s — Phone+TV split, SVG beam draws between them with traveling particles
6. 23-27s — Hero shot: TV fills frame, phone in corner, rotateY orbit
7. 27-30s — End card on paper with real logo, mono wordmark, Satoshi
   tagline "Cast in one tap. Free, with ads.", black "GET IT ON GOOGLE PLAY"

Upload to YouTube → paste URL into Play Console's Promo Video field.

### Design tokens

```
--air-green : #34A853  /* the brand green from the app icon */
--air-blue  : #4285F4  /* the brand blue */
--air-ink   : #0F1A2E  /* dark surface */
--air-paper : #F5F1E8  /* warm paper surface */
```

### Brand voice

App is **free, with ads**. NEVER claim "ad-free", "no ads", or "0
trackers" — would be misinformation. Truthful value props acceptable:
"no account", "no subscription", "no email", "free to download", "no
setup wizard".

## Regenerate v4

```sh
cd versions/v4
node render_v4.js       # 6 PNG screenshots
node record_v4.js       # 30s WebM tutorial
# WebM → MP4
ffmpeg -i video/aircast_tutorial_v4.webm \
       -c:v libx264 -pix_fmt yuv420p -preset slow -crf 20 \
       -movflags +faststart video/aircast_tutorial_v4.mp4
```

## Legacy versions

- `versions/v1/` — first pass, saturated-gradient cliche
- `versions/v2/` — editorial dark + Instrument Serif (AI-template)
- `versions/v3/` — NeoPOP + Erode italic (cursive flagged as AI-slop)

Kept for comparison. Safe to delete once v4 is uploaded to Play Console.
