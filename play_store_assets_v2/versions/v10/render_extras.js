const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const ROOT = __dirname;
const TAB_HTML = path.resolve(ROOT, 'tablet', 'html');
const OUT_7  = path.resolve(ROOT, 'tablet', 'out_7');
const OUT_10 = path.resolve(ROOT, 'tablet', 'out_10');
const OUT_FT = path.resolve(ROOT, 'feature');
for (const d of [OUT_7, OUT_10, OUT_FT]) {
  if (!fs.existsSync(d)) fs.mkdirSync(d, { recursive: true });
}

const TABLET_SCREENS = [
  'tab_1_hero','tab_2_apphome','tab_3_streaming','tab_4_gaming',
  'tab_5_alltvs','tab_6_languages','tab_7_working','tab_8_cta'
];

(async () => {
  const browser = await chromium.launch();

  // --- 7" tablet 1920×1080 ---
  {
    const ctx = await browser.newContext({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
    const page = await ctx.newPage();
    for (const name of TABLET_SCREENS) {
      const url = 'file:///' + path.resolve(TAB_HTML, `${name}.html`).replace(/\\/g, '/');
      console.log('7"', name);
      await page.goto(url, { waitUntil: 'networkidle' });
      await page.waitForTimeout(1000);
      await page.screenshot({ path: path.resolve(OUT_7, `${name}.png`), clip: { x:0, y:0, width:1920, height:1080 }});
    }
    await ctx.close();
  }

  // --- 10" tablet 2560×1440 (same HTML, scaled up via deviceScaleFactor) ---
  {
    const ctx = await browser.newContext({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2560/1920 });
    const page = await ctx.newPage();
    for (const name of TABLET_SCREENS) {
      const url = 'file:///' + path.resolve(TAB_HTML, `${name}.html`).replace(/\\/g, '/');
      console.log('10"', name);
      await page.goto(url, { waitUntil: 'networkidle' });
      await page.waitForTimeout(1000);
      await page.screenshot({ path: path.resolve(OUT_10, `${name}.png`), clip: { x:0, y:0, width:1920, height:1080 }});
    }
    await ctx.close();
  }

  // --- Feature graphic 1024×500 ---
  {
    const ctx = await browser.newContext({ viewport: { width: 1024, height: 500 }, deviceScaleFactor: 1 });
    const page = await ctx.newPage();
    const url = 'file:///' + path.resolve(OUT_FT, 'feature.html').replace(/\\/g, '/');
    console.log('FT feature');
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.resolve(OUT_FT, 'feature_graphic.png'), clip: { x:0, y:0, width:1024, height:500 }});
    await ctx.close();
  }

  await browser.close();
  console.log('Done.');
})().catch(e => { console.error(e); process.exit(1); });
