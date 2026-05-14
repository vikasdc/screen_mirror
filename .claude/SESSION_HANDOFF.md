# Session handoff

Single source of truth for "what was in flight last time I worked on this." Read this BEFORE starting work. Update it BEFORE ending work (last action of every session). Sync via git like any other file.

If two machines edit this in the same session, last-write-wins on push. That's fine because each session writes a date-stamped entry in the Log section, so nothing important gets lost — at worst a merge conflict you resolve manually.

---

## Open threads

These are tasks that were active when the last session ended. Treat each as a TodoWrite seed.

- **Production AAB upload pending.** `AndroidApp/app/build.gradle` has uncommitted bump to versionCode 14 / versionName 1.5 (was 13 / 1.4). The bump is intentional — Play Console rejected versionCode 13 on previous upload as duplicate. Next session: confirm the signed AAB at `AndroidApp/app/release/app-release.aab` was rebuilt with the bumped version, then upload to Play Console.
- **Language picker bug fix is shipped in code but not verified on-device.** `LanguagePicker.applyLocale()` + `WalkthroughActivity.bindCastPage()` + `walk_cast_step2` string with `%1$s` placeholder all in place. Confirmed builds clean. NOT YET verified on a real Pixel 8 install via adb (phone biometric blocked it last attempt). When picking this up: install the v1.5 build, force-pick a non-English locale from the in-app picker, confirm the cast walkthrough page reflects the change without an app restart.
- ~~`play_store_assets_v2/versions/v8/`, `v9/`, `v10/` all untracked.~~ RESOLVED 2026-05-14: v10 committed as the only Play Store asset version. v1–v9 and the pre-`versions/` flat layout deleted. `play_store_assets_v2/package.json` moved inside v10 so v10 is self-contained. `versions/v10/feature/feature_graphic.png` is the 1024×500 feature graphic.
- **`aircast_tutorial_landscape_final.mp4` (5.7MB, 36s, 1080p)** is the upload-ready Play Store promo video. Voiceover by Edge TTS Guy Neural, background music is Bensound "Creative Minds" (CC-BY — requires attribution somewhere if Google enforces). Replace `bg_music.mp3` with a CC0 track if attribution is a concern, then re-run `python gen_landscape_final.py`.

## Conventions decided

Patterns and decisions that any future session needs to honor:

- **Per-screen voice clips, not one long MP3.** The promo video voiceover uses ffmpeg `adelay` to place each phrase at the exact second its scene starts. Editing the script means editing the `SCENES` list in `gen_landscape_final.py` then re-running. Don't go back to a single continuous MP3 — that's how the speech got out of sync with visuals in earlier attempts.
- **Brand SVG sizing.** Source SVGs from simple-icons / Wikimedia often have broken viewBoxes (Samsung's content was transformed outside its 64×64 frame). When a logo renders tiny in a pill, fix the SOURCE SVG rather than CSS-scaling on top of it. Standard pattern: rewrite with viewBox 240×60 (or proportional aspect) and either a single recognizable text mark or the brand's iconic shape.
- **Playwright video recording quirk.** At 1920×1080 the page renders slower than real-time. The recorded WebM ends up longer than the wall-clock `waitForTimeout`. Post-process with `setpts=PTS*<actual/intended>` + `fps=30` to normalize. `record_landscape.js` clears `video_raw/` before each run — DO NOT remove that step. Without it the script grabs whichever filename comes last alphabetically (which is usually the WRONG one).
- **Aggressive .gitignore allowlist.** New top-level directories are silently ignored. To add a new tracked dir, append `!/<dir>/` and `!/<dir>/**` to .gitignore BEFORE the inner blacklist (lines 19–56).
- **Keystore lives outside the repo.** `I:/Other computers/My Mac/Documents/aircast-release-key.jks`. Alias is `aircast-key` (not `aircast-release-key` despite the filename). `keystore.properties` is gitignored; copy from `keystore.properties.example` and fill in passwords locally — passwords are NOT stored anywhere in this repo or in memory files.

## Don't-forget

Non-obvious gotchas:

- **Windows file-lock during Android builds.** Two Java daemons (CLI gradlew + Android Studio) deadlock on `app/build/intermediates/.../classes`. Recovery: `cd AndroidApp && ./gradlew --stop && rm -rf app/build` then retry. `gradle.properties` already has `org.gradle.parallel=false` and `org.gradle.workers.max=1` to mitigate.
- **macOS path differences.** The keystore is on a Google Drive "Other computers" mount, accessible as `I:\Other computers\My Mac\Documents\` on Windows. On the Mac the same file is local at `~/Documents/aircast-release-key.jks`. `keystore.properties` will need different `storeFile=` paths per machine — but since `keystore.properties` is gitignored, that's already handled: each machine has its own copy with the right path.
- **FFmpeg comes from imageio-ffmpeg.** Windows path: `C:/Users/dulgu/AppData/Local/Programs/Python/Python311/Lib/site-packages/imageio_ffmpeg/binaries/ffmpeg-win-x86_64-v7.1.exe`. On macOS it'll be a `ffmpeg-osx-arm64-*` binary at the equivalent site-packages path. The `gen_*_final.py` scripts hardcode the Windows path — when running on the Mac, override the `FFMPEG` variable at the top of the script.
- **`handoff/` directory predates this multi-machine workflow.** It was a one-time Claude-account handoff bundle from May 2026. The hard preferences, project facts, and ASO context in `handoff/HANDOFF.md` are still authoritative — don't delete that file. SESSION_HANDOFF.md (this file) supplements it for ongoing session-to-session continuity.
- **Play Store "Contains ads" flag.** Console has it ON. Code has NO AdMob. Pre-launch report will flag this. Fix either by untoggling in Console OR integrating AdMob — open question, not yet decided.

## Log

Append-only daily notes. Newest at the top. Each entry: `## YYYY-MM-DD (machine)` then a few bullets of what was actually done. Useful for "wait, what did I do last Tuesday" recall.

## 2026-05-14 (windows desktop — second pass)

Major cleanup of the Play Store assets directory:
- Deleted `play_store_assets_v2/versions/v1` through `v9` (207 files, ~11k lines removed).
- Deleted `play_store_assets_v2/{html,out,phone_screens,record*.js,render*.js,package-lock.json}` — the pre-`versions/` flat layout from v1–v3 era.
- Moved `play_store_assets_v2/package.json` → `play_store_assets_v2/versions/v10/package.json` so v10 is self-contained (each version owns its own playwright dep).
- Rewrote `play_store_assets_v2/README.md` to describe v10 only.
- Added `!/play_store_assets_v2/**/*.txt` to .gitignore so `voiceover_script.txt` survives the global `**/*.txt` deny.
- Committed and tracked the v10 tree (91 files, ~66MB) — screenshots, tablet variants, feature graphic, two final MP4s, voice clips, source HTMLs, generation scripts.

## 2026-05-14 (windows desktop — first pass)

Initial scaffold of this file. Captured current state:

- Bumped versionCode 13→14 / 1.4→1.5 in build.gradle (uncommitted).
- Built v10 of Play Store assets: 9 portrait screenshots, 1024×500 feature graphic, 36s landscape promo MP4 with Edge TTS voiceover + Bensound music.
- Language picker bug fixed in code (LanguagePicker.applyLocale + WalkthroughActivity binding + %1$s placeholder in strings.xml).
- Language fix NOT verified on physical device yet (biometric lock issue).
- AAB build attempted in Android Studio — hit Windows file lock on first try, cleared via `./gradlew --stop` + delete `app/build`. Now blocked on keystore password.

Open questions left for the user:
1. Provide keystore password to finish building the signed AAB.
2. Decide on attribution for Bensound music in app description, or swap to CC0 track.
3. "Contains ads" Play Console flag still wrong vs code reality.
