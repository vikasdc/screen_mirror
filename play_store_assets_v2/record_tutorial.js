const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const OUT_DIR = path.resolve(__dirname, 'video_raw');
const FINAL_DIR = path.resolve(__dirname, 'out');

(async () => {
  if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR);
  if (!fs.existsSync(FINAL_DIR)) fs.mkdirSync(FINAL_DIR);

  const browser = await chromium.launch();
  const ctx = await browser.newContext({
    viewport: { width: 1080, height: 1920 },
    deviceScaleFactor: 1,
    recordVideo: { dir: OUT_DIR, size: { width: 1080, height: 1920 } },
  });
  const page = await ctx.newPage();
  const url = 'file:///' + path.resolve(__dirname, 'html', 'tutorial.html').replace(/\\/g, '/');
  console.log('Loading', url);
  await page.goto(url, { waitUntil: 'networkidle' });
  // 30.5 seconds of scenes (give the end card a beat to settle)
  await page.waitForTimeout(30500);
  await ctx.close();
  await browser.close();

  const files = fs.readdirSync(OUT_DIR).filter(f => f.endsWith('.webm'));
  if (files.length) {
    const src = path.join(OUT_DIR, files[files.length - 1]);
    const dst = path.join(FINAL_DIR, 'aircast_tutorial.webm');
    fs.copyFileSync(src, dst);
    console.log('Saved:', dst);
  } else {
    console.error('No webm produced');
  }
})().catch(e => { console.error(e); process.exit(1); });
