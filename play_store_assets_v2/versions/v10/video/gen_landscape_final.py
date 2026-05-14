"""
Build the FINAL landscape Aircast tutorial — 1920x1080 with:
  - Frame-synced voiceover (older deeper male, en-US-GuyNeural, pitch -8Hz)
  - Bensound 'Creative Minds' background music (low volume, faded)
  - Same per-scene placement as portrait version
"""
import asyncio
import edge_tts
import subprocess
import sys
from pathlib import Path

OUT = Path(__file__).parent
FFMPEG = r"C:/Users/dulgu/AppData/Local/Programs/Python/Python311/Lib/site-packages/imageio_ffmpeg/binaries/ffmpeg-win-x86_64-v7.1.exe"

VIDEO_IN   = OUT / "aircast_tutorial_landscape.mp4"
MUSIC_IN   = OUT / "bg_music.mp3"
VIDEO_OUT  = OUT / "aircast_tutorial_landscape_final.mp4"

VOICE = "en-US-GuyNeural"
RATE  = "-4%"
PITCH = "-8Hz"

VOICE_GAIN = 1.4
MUSIC_GAIN = 0.16

SCENES = [
    ( 0.4,  "Meet Aircast."),
    ( 2.8,  "Open the app."),
    ( 5.6,  "Tap, Search for TVs."),
    ( 8.7,  "Pick any TV. They all work."),
    (13.0,  "Connecting in two seconds."),
    (16.5,  "Your phone, on the big screen."),
    (19.2,  "Mirror anything."),
    (22.1,  "Watch movies and shows."),
    (25.1,  "Play games on the big screen."),
    (28.1,  "Present from your phone."),
    (31.0,  "Free, forever."),
    (33.5,  "Install Aircast now."),
]


async def gen_clip(idx: int, text: str) -> Path:
    out_path = OUT / f"_lclip_{idx:02d}.mp3"
    communicate = edge_tts.Communicate(text, VOICE, rate=RATE, pitch=PITCH)
    await communicate.save(str(out_path))
    return out_path


def get_duration(path: Path) -> float:
    r = subprocess.run([FFMPEG, "-i", str(path)], capture_output=True, text=True)
    for line in r.stderr.splitlines():
        if "Duration" in line:
            t = line.split("Duration:")[1].split(",")[0].strip()
            h, m, s = t.split(":")
            return int(h) * 3600 + int(m) * 60 + float(s)
    return 0.0


async def main():
    print(f"Voice: {VOICE} (pitch {PITCH}, rate {RATE})")
    print("Generating per-scene clips:")
    clips = []
    for i, (start, text) in enumerate(SCENES):
        path = await gen_clip(i, text)
        dur = get_duration(path)
        end = start + dur
        next_start = SCENES[i+1][0] if i+1 < len(SCENES) else 36.0
        flag = " !overlap" if end > next_start - 0.05 else ""
        clips.append((start, path, dur))
        print(f"  [{i:02d}] {start:5.2f}s +{dur:4.2f}s -> {end:5.2f}s  (next {next_start:.2f}){flag}  \"{text}\"")

    print("\nMuxing voice + music + landscape video...")

    inputs = []
    for _, p, _ in clips:
        inputs += ["-i", str(p)]
    inputs += ["-i", str(MUSIC_IN)]
    inputs += ["-i", str(VIDEO_IN)]
    music_idx = len(clips)
    video_idx = len(clips) + 1

    parts = []
    for i, (start, _, _) in enumerate(clips):
        ms = int(round(start * 1000))
        parts.append(f"[{i}:a]adelay={ms}|{ms}[v{i}]")

    voice_inputs = "".join(f"[v{i}]" for i in range(len(clips)))
    parts.append(
        f"{voice_inputs}amix=inputs={len(clips)}:duration=longest:normalize=0[voicemix]"
    )
    parts.append(f"[voicemix]volume={VOICE_GAIN}[voice]")
    parts.append(
        f"[{music_idx}:a]atrim=0:36,asetpts=PTS-STARTPTS,"
        f"afade=t=in:st=0:d=1.0,afade=t=out:st=33:d=3.0,"
        f"volume={MUSIC_GAIN},aresample=async=1[music]"
    )
    parts.append("[voice][music]amix=inputs=2:duration=first:normalize=0:weights=1.5 0.7[outa]")

    filter_complex = ";".join(parts)

    cmd = [FFMPEG, "-y"] + inputs + [
        "-filter_complex", filter_complex,
        "-map", f"{video_idx}:v:0",
        "-map", "[outa]",
        "-c:v", "copy",
        "-c:a", "aac", "-b:a", "192k", "-ac", "2",
        "-movflags", "+faststart",
        "-shortest",
        str(VIDEO_OUT),
    ]
    r = subprocess.run(cmd, capture_output=True, text=True)
    if r.returncode != 0:
        print("FFMPEG ERROR:")
        print(r.stderr[-2500:])
        sys.exit(1)

    for _, p, _ in clips:
        try: p.unlink()
        except: pass

    out_kb = VIDEO_OUT.stat().st_size // 1024
    print(f"\n[ok] {VIDEO_OUT.name}  ({out_kb} KB)")


if __name__ == "__main__":
    asyncio.run(main())
