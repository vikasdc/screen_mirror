const { chromium } = require('playwright');
const path = require('path');
const SCREENS = ['v7_1_hero','v7_2_concert','v7_3_onetap','v7_4_compat','v7_5_languages','v7_6_cta'];
(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({ viewport: { width: 1080, height: 1920 }, deviceScaleFactor: 1 });
  const page = await ctx.newPage();
  for (const name of SCREENS) {
    const url = 'file:///' + path.resolve(__dirname, 'html', `${name}.html`).replace(/\\/g, '/');
    console.log('→', name);
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.waitForTimeout(1500);
    await page.screenshot({ path: path.resolve(__dirname, 'out', `${name}.png`), clip: { x:0, y:0, width:1080, height:1920 }});
  }
  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
