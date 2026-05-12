// Record 30-second promo video by playing back the animated HTML page.
// Uses Playwright's built-in video capture (WebM, VP8).
const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const OUT_DIR = path.resolve(__dirname, 'video_raw');
const FINAL_DIR = path.resolve(__dirname, 'out');

(async () => {
  if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR);
  if (!fs.existsSync(FINAL_DIR)) fs.mkdirSync(FINAL_DIR);

  // Need full browser launch (not headless-shell) for video capture.
  const browser = await chromium.launch({
    args: ['--disable-blink-features=AutomationControlled'],
  });
  const ctx = await browser.newContext({
    viewport: { width: 1080, height: 1920 },
    deviceScaleFactor: 1,
    recordVideo: { dir: OUT_DIR, size: { width: 1080, height: 1920 } },
  });
  const page = await ctx.newPage();
  const url = 'file:///' + path.resolve(__dirname, 'html', 'promo.html').replace(/\\/g, '/');
  console.log('Loading', url);
  await page.goto(url, { waitUntil: 'networkidle' });
  // 30 seconds of scenes (6 × 5s)
  await page.waitForTimeout(30000);
  await ctx.close();
  await browser.close();

  // Rename the auto-generated webm
  const files = fs.readdirSync(OUT_DIR).filter(f => f.endsWith('.webm'));
  if (files.length) {
    const src = path.join(OUT_DIR, files[0]);
    const dst = path.join(FINAL_DIR, 'aircast_promo.webm');
    fs.copyFileSync(src, dst);
    console.log('Final video:', dst);
  } else {
    console.error('No webm produced — check headless mode / browser channel.');
  }
})().catch(e => { console.error(e); process.exit(1); });
