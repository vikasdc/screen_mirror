const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');
const OUT_DIR = path.resolve(__dirname, 'video_raw');
const FINAL_DIR = __dirname;
(async () => {
  if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR);
  // clear stale recordings so the script grabs THIS run's output
  for (const f of fs.readdirSync(OUT_DIR)) {
    if (f.endsWith('.webm')) fs.unlinkSync(path.join(OUT_DIR, f));
  }
  const browser = await chromium.launch();
  const ctx = await browser.newContext({
    viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1,
    recordVideo: { dir: OUT_DIR, size: { width: 1920, height: 1080 } },
  });
  const page = await ctx.newPage();
  const url = 'file:///' + path.resolve(__dirname, 'tutorial_landscape.html').replace(/\\/g, '/');
  console.log('Loading', url);
  await page.goto(url, { waitUntil: 'networkidle' });
  await page.waitForTimeout(36500);
  await ctx.close();
  await browser.close();
  const files = fs.readdirSync(OUT_DIR).filter(f => f.endsWith('.webm'));
  if (files.length) {
    const src = path.join(OUT_DIR, files[files.length - 1]);
    const dst = path.join(FINAL_DIR, 'aircast_tutorial_landscape.webm');
    fs.copyFileSync(src, dst);
    console.log('Saved:', dst);
  }
})().catch(e => { console.error(e); process.exit(1); });
