"""
Generate the voiceover audio for the Aircast tutorial video using Microsoft
Edge's neural TTS (free, high-quality).

Produces three takes so you can pick the voice that fits best:
  - aircast_vo_jenny.mp3   (en-US-JennyNeural, warm friendly female)
  - aircast_vo_aria.mp3    (en-US-AriaNeural, modern energetic female)
  - aircast_vo_andrew.mp3  (en-US-AndrewNeural, warm male)
"""
import asyncio
import edge_tts
from pathlib import Path

OUT = Path(__file__).parent

# SSML with inter-scene pauses to roughly align with video frames.
# Edge-TTS understands a subset of SSML: <break>, <prosody>, <say-as>.
SCRIPT = """
<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xml:lang="en-US">
  <prosody rate="-3%">
    Meet Aircast.<break time="1300ms"/>
    Open the app<break time="350ms"/>
    tap, Search for TVs.<break time="650ms"/>
    Pick your TV.<break time="200ms"/>
    Samsung, <say-as interpret-as="characters">LG</say-as>, Fire <say-as interpret-as="characters">TV</say-as>, Roku<break time="200ms"/>
    it works with all of them.<break time="450ms"/>
    Connected.<break time="200ms"/>
    In about two seconds.<break time="400ms"/>
    Done.<break time="200ms"/>
    Your phone's now on the big screen.<break time="500ms"/>
    Mirror anything<break time="350ms"/>
    watch movies and shows,<break time="500ms"/>
    play games on the <say-as interpret-as="characters">TV</say-as>,<break time="500ms"/>
    present right from your phone.<break time="500ms"/>
    Free, forever.<break time="200ms"/>
    No sign-up.<break time="350ms"/>
    Install Aircast<break time="200ms"/>
    and start casting.
  </prosody>
</speak>
""".strip()

# Plain-text fallback (edge-tts CLI works best with plain text + rate/volume flags)
PLAIN = (
    "Meet Aircast. Open the app — tap Search for TVs. "
    "Pick your TV. Samsung, L G, Fire T V, Roku — it works with all of them. "
    "Connected. In about two seconds. "
    "Done. Your phone's now on the big screen. "
    "Mirror anything — watch movies and shows, play games on the TV, "
    "present right from your phone. "
    "Free, forever. No sign-up. Install Aircast and start casting."
)

VOICES = {
    "jenny":  "en-US-JennyNeural",
    "aria":   "en-US-AriaNeural",
    "andrew": "en-US-AndrewNeural",
}

async def generate(label: str, voice: str):
    out_path = OUT / f"aircast_vo_{label}.mp3"
    # use plain text — edge-tts SSML support is partial and inconsistent
    communicate = edge_tts.Communicate(PLAIN, voice, rate="-5%")
    await communicate.save(str(out_path))
    size_kb = out_path.stat().st_size // 1024
    print(f"  [ok] {label:<8} {voice:<24} {size_kb} KB  ->  {out_path.name}")

async def main():
    print("Generating Aircast voiceovers...")
    for label, voice in VOICES.items():
        await generate(label, voice)
    print("\nDone. Listen to all three and pick the best one for the video.")

if __name__ == "__main__":
    asyncio.run(main())
