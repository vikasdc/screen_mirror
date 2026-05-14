const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');
const OUT = path.resolve(__dirname, 'out');
if (!fs.existsSync(OUT)) fs.mkdirSync(OUT, { recursive: true });
// clear old outputs to avoid stale renames
for (const f of fs.readdirSync(OUT)) {
  if (f.endsWith('.png')) fs.unlinkSync(path.join(OUT, f));
}
const SCREENS = [
  'v10_1_hero', 'v10_2_apphome', 'v10_3_onetap',
  'v10_4_streaming', 'v10_5_gaming', 'v10_6_working',
  'v10_7_alltvs', 'v10_8_languages', 'v10_9_cta'
];
(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({ viewport: { width: 1080, height: 1920 }, deviceScaleFactor: 1 });
  const page = await ctx.newPage();
  for (const name of SCREENS) {
    const url = 'file:///' + path.resolve(__dirname, 'html', `${name}.html`).replace(/\\/g, '/');
    console.log('→', name);
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.waitForTimeout(1200);
    await page.screenshot({ path: path.resolve(OUT, `${name}.png`), clip: { x:0, y:0, width:1080, height:1920 }});
  }
  await browser.close();
  console.log('Done.');
})().catch(e => { console.error(e); process.exit(1); });
