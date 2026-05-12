const { chromium } = require('playwright');
const path = require('path');
const SCREENS = ['v3_1_hero','v3_2_brands','v3_3_onetap','v3_4_proof','v3_5_speed','v3_6_honest'];
(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({ viewport: { width: 1080, height: 1920 }, deviceScaleFactor: 1 });
  const page = await ctx.newPage();
  for (const name of SCREENS) {
    const url = 'file:///' + path.resolve(__dirname, 'html', `${name}.html`).replace(/\\/g, '/');
    console.log('→', name);
    await page.goto(url, { waitUntil: 'networkidle' });
    await page.waitForTimeout(1000); // allow webfonts (Erode from fontshare) to load
    await page.screenshot({ path: path.resolve(__dirname, 'out', `${name}.png`), clip: { x:0, y:0, width:1080, height:1920 }});
  }
  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
