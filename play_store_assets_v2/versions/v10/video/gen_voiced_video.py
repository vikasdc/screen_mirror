"""
Generate the Aircast tutorial video with a properly frame-synced voiceover.

Approach:
  1. Render each script line as its own short MP3 with edge-tts.
  2. Use ffmpeg's adelay filter to place each clip at the exact second its
     scene starts on screen.
  3. Mix all delayed clips into one audio track.
  4. Mux the audio track with the source MP4.

The result: when the screen shows "TAP SEARCH", the voice is saying that line,
not still finishing "Open the app." from 2 seconds ago.
"""
import asyncio
import edge_tts
import subprocess
import sys
from pathlib import Path

OUT = Path(__file__).parent
FFMPEG = r"C:/Users/dulgu/AppData/Local/Programs/Python/Python311/Lib/site-packages/imageio_ffmpeg/binaries/ffmpeg-win-x86_64-v7.1.exe"

VIDEO_IN  = OUT / "aircast_tutorial_v10.mp4"
VIDEO_OUT = OUT / "aircast_tutorial_v10_voiced.mp4"

# Voice settings — Jenny is the warm friendly female; slow slightly for clarity
VOICE = "en-US-JennyNeural"
RATE  = "-4%"

# (start_seconds, "phrase")
# Each line is timed so it BEGINS just after the matching scene starts.
# Scene transitions in tutorial_v10.html happen at these timestamps:
#   2.4s  scene 2 (app home)
#   5.6s  scene 2 finger tap section
#   8.3s  scene 3 (TV list)
#  12.5s  scene 3 Samsung select / connecting
#  15.0s  connecting screen
#  16.5s  connected check
#  18.5s  mirror screen (beach)
#  21.8s  streaming (mountain)
#  24.8s  gaming
#  27.8s  presenting
#  31.0s  CTA install
SCENES = [
    ( 0.4,  "Meet Aircast."),
    ( 2.9,  "Open the app."),
    ( 5.9,  "Tap Search for TVs."),
    ( 8.6,  "Pick any TV. Samsung, LG, Fire TV, Roku — all of them work."),
    (12.7,  "Connecting in two seconds."),
    (16.7,  "Your phone is now on the big screen."),
    (19.0,  "Mirror anything."),
    (22.2,  "Watch movies and shows."),
    (25.2,  "Play games on the TV."),
    (28.2,  "Present right from your phone."),
    (31.4,  "Aircast. Free, forever. Install now."),
]


async def gen_clip(idx: int, text: str) -> Path:
    out_path = OUT / f"_clip_{idx:02d}.mp3"
    communicate = edge_tts.Communicate(text, VOICE, rate=RATE)
    await communicate.save(str(out_path))
    return out_path


def get_duration(path: Path) -> float:
    result = subprocess.run(
        [FFMPEG, "-i", str(path)],
        capture_output=True, text=True
    )
    # ffmpeg writes info to stderr
    for line in result.stderr.splitlines():
        if "Duration" in line:
            # "  Duration: 00:00:01.23, ..."
            t = line.split("Duration:")[1].split(",")[0].strip()
            h, m, s = t.split(":")
            return int(h) * 3600 + int(m) * 60 + float(s)
    return 0.0


async def main():
    print("Generating per-scene voiceover clips...")
    clips = []
    for i, (start, text) in enumerate(SCENES):
        path = await gen_clip(i, text)
        dur = get_duration(path)
        end = start + dur
        clips.append((start, path, dur))
        # Print a quick alignment check
        next_start = SCENES[i+1][0] if i+1 < len(SCENES) else 36.0
        overlap = " OVERLAP!" if end > next_start - 0.1 else ""
        print(f"  [{i:02d}] {start:5.2f}s  +{dur:4.2f}s  ends {end:5.2f}s  (next at {next_start:.2f}s){overlap}  \"{text}\"")

    print("\nBuilding ffmpeg mux...")

    # Build input list: clips first, then video
    inputs = []
    for _, path, _ in clips:
        inputs += ["-i", str(path)]
    inputs += ["-i", str(VIDEO_IN)]
    video_idx = len(clips)

    # Build complex filter
    parts = []
    for i, (start, _, _) in enumerate(clips):
        ms = int(round(start * 1000))
        # delay both channels (handles mono and stereo)
        parts.append(f"[{i}:a]adelay={ms}|{ms}[a{i}]")

    mix_inputs = "".join(f"[a{i}]" for i in range(len(clips)))
    parts.append(
        f"{mix_inputs}amix=inputs={len(clips)}:duration=longest:normalize=0[mixed]"
    )
    # gentle output volume boost (amix divides by N, even with normalize=0 we want it loud)
    parts.append("[mixed]volume=1.6[outa]")

    filter_complex = ";".join(parts)

    cmd = [FFMPEG, "-y"] + inputs + [
        "-filter_complex", filter_complex,
        "-map", f"{video_idx}:v:0",
        "-map", "[outa]",
        "-c:v", "copy",
        "-c:a", "aac", "-b:a", "192k",
        "-movflags", "+faststart",
        "-shortest",
        str(VIDEO_OUT),
    ]
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.returncode != 0:
        print("FFMPEG ERROR:")
        print(result.stderr[-2000:])
        sys.exit(1)

    # cleanup temp clips
    for _, path, _ in clips:
        try: path.unlink()
        except: pass

    out_size = VIDEO_OUT.stat().st_size // 1024
    print(f"\n[ok] Final: {VIDEO_OUT.name}  ({out_size} KB)")


if __name__ == "__main__":
    asyncio.run(main())
