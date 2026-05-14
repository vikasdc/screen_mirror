# Aircast Play Store assets

Final set of Play Store listing assets for Aircast. Single source — `versions/v10/` is THE version. Earlier iterations (v1–v9) and the pre-`versions/` flat layout were removed; if you need them, git log them.

## What's in v10

```
versions/v10/
├─ html/                      9 portrait screenshot HTMLs (1080×1920)
│  ├─ v10_1_hero.html        MIRROR. CAST. — phone+TV beach mirror
│  ├─ v10_2_apphome.html     OPEN. SEARCH. — Aircast home screen mockup
│  ├─ v10_3_onetap.html      ONE TAP. — cast picker (Samsung selected)
│  ├─ v10_4_streaming.html   STREAM. BIGGER. — mountain documentary
│  ├─ v10_5_gaming.html      GAME. HUGE. — synthwave racing scene
│  ├─ v10_6_working.html     PRESENT. PROUDLY. — revenue chart slide
│  ├─ v10_7_alltvs.html      EVERY TV. — 6 brand pills vertical stack
│  ├─ v10_8_languages.html   15. — 12 floating language pills
│  └─ v10_9_cta.html         FREE. FOREVER. + INSTALL button
├─ tablet/                    7" and 10" tablet variants (1920×1080 landscape)
│  └─ html/                   each screenshot has a tab_*.html version
├─ feature/
│  └─ feature.html            1024×500 Play Store feature graphic
├─ video/                     promo videos
│  ├─ tutorial_v10.html       36s portrait video source
│  ├─ tutorial_landscape.html 36s landscape video source
│  ├─ aircast_tutorial_v10_final.mp4         portrait + voice + music (ready)
│  ├─ aircast_tutorial_landscape_final.mp4   landscape + voice + music (ready)
│  ├─ bg_music.mp3            Bensound 'Creative Minds' (CC-BY)
│  ├─ aircast_vo_jenny.mp3    standalone voiceover options
│  ├─ aircast_vo_aria.mp3
│  ├─ aircast_vo_andrew.mp3
│  ├─ gen_final_video.py      per-scene voice + music mux (portrait)
│  ├─ gen_landscape_final.py  per-scene voice + music mux (landscape)
│  ├─ gen_voiceover.py        3-voice generator for A/B/C selection
│  └─ voiceover_script.txt    timed script + SSML + delivery notes
├─ brand_logos/               clean SVGs (Samsung, LG, Sony, Roku, Fire TV, Chromecast, etc.)
├─ photos/                    sunset_beach.jpg, mountain.jpg, concert_lights.jpg
├─ out/                       rendered PNG output (9 screenshots)
├─ render_v10.js              renders the 9 portrait screenshots
├─ record_v10.js              records the 36s portrait video
├─ render_extras.js           renders feature graphic + tablet variants
└─ package.json               playwright dep
```

## Design system (locked)

- **Font**: Bricolage Grotesque 800 (display) + Inter Medium/500 (body)
- **Palette**: warm cream paper (`#FAF3E5` → `#E8D6B0`) with olive (`#4A5D23`), terracotta (`#D2654B`), saffron (`#E5A04D`), plum (`#6E2B47`) accents
- **Brand colors retained for the Aircast mark**: green `#34A853`, blue `#4285F4`
- **Text rule**: 2–3 word headlines max, no body sublines

## To regenerate

```sh
cd play_store_assets_v2/versions/v10
npm install                         # one-time, installs playwright
node render_v10.js                  # renders 9 portrait PNG screenshots
node record_v10.js                  # records 36s portrait video WebM
node render_extras.js               # renders feature graphic + tablet variants
cd video
python gen_landscape_final.py       # builds landscape MP4 with voice + music
python gen_final_video.py           # builds portrait MP4 with voice + music
```

FFmpeg comes from imageio-ffmpeg (pip-installed). The `gen_*.py` scripts hardcode the Windows binary path — update the `FFMPEG` variable at the top of the script when running on macOS.

## Voiceover

Per-scene phrases generated via Edge TTS, placed at exact second timestamps using ffmpeg `adelay`. Default voice: `en-US-GuyNeural` with pitch `-8Hz` for an older-sounding male delivery. See `video/voiceover_script.txt` for the full timed script.

## Background music

`bg_music.mp3` is Bensound "Creative Minds" (CC-BY — credit Bensound if you keep it, or drop in a CC0 replacement from Pixabay / YouTube Audio Library and re-run `gen_landscape_final.py`).

## Upload checklist

| Play Console field | File |
|---|---|
| Phone screenshots (8 max) | `versions/v10/out/v10_1_hero.png` through `v10_9_cta.png` (pick any 8 of the 9) |
| 7" tablet screenshots | `versions/v10/tablet/out_7/tab_*.png` |
| 10" tablet screenshots | `versions/v10/tablet/out_10/tab_*.png` |
| Feature graphic | `versions/v10/feature/feature_graphic.png` |
| Promo video | `versions/v10/video/aircast_tutorial_landscape_final.mp4` (landscape is preferred for Play Store) |
