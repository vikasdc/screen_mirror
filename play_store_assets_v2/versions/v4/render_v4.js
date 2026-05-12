const { chromium } = require('playwright');
const path = require('path');
const SCREENS = ['v4_1_hero','v4_2_brands','v4_3_onetap','v4_4_proof','v4_5_speed','v4_6_honest'];
(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({ viewport: { width: 1080, height: 1920 }, deviceScaleFactor: 1 });
  const page = await ctx.newPage();
  for (const name of SCREENS) {
    const url = 'file:///' + path.resolve(__dirname, 'html', `${name}.html`).replace(/\\/g, '/');
    console.log('→', name);
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.waitForTimeout(1200); // allow webfonts + SVG to load
    await page.screenshot({ path: path.resolve(__dirname, 'out', `${name}.png`), clip: { x:0, y:0, width:1080, height:1920 }});
  }
  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
