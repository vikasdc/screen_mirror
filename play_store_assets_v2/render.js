// Render Play Store screenshots from HTML at 1080x1920.
// Usage: node render.js
const { chromium } = require('playwright');
const path = require('path');

const SCREENS = [
  '1_hero',
  '2_onetap',
  '3_alltvs',
  '4_languages',
  '5_free',
  '6_hd',
];

(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({
    viewport: { width: 1080, height: 1920 },
    deviceScaleFactor: 1,
  });
  const page = await ctx.newPage();

  for (const name of SCREENS) {
    const url = 'file:///' + path.resolve(__dirname, 'html', `${name}.html`).replace(/\\/g, '/');
    console.log(`Rendering ${name} from ${url}`);
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.waitForTimeout(500);
    const out = path.resolve(__dirname, 'out', `${name}.png`);
    await page.screenshot({ path: out, fullPage: false, clip: { x: 0, y: 0, width: 1080, height: 1920 } });
    console.log(`  → ${out}`);
  }

  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
