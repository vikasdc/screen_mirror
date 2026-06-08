# Aircast developer website

Static site for the Aircast developer presence. Hosts:

- Landing page (`index.html`) — what shows when someone visits the bare domain.
- `app-ads.txt` — AdMob's app-ads.txt verification file. MUST be served at the ROOT of the deployed domain. AdMob's crawler fetches `https://<your-domain>/app-ads.txt` and matches the publisher ID.
- `aircast/privacy.html` — privacy policy. The Play Store listing's "Privacy policy URL" field should point here once deployed.

## Why this exists

AdMob requires a developer website to verify your app for serving ads. The website's `/app-ads.txt` file must contain a line matching your AdMob publisher ID. Google Sites pages don't expose the root path, so app-ads.txt can't live there — hence this folder, which is meant to be deployed to a host that gives you root-level URL control.

## Deploy options (pick one)

### Option A: Netlify drag-and-drop (fastest, 2 minutes)

1. Sign up at https://app.netlify.com (Google login works).
2. Drag the entire `developer-site/` folder onto the Netlify dashboard.
3. Netlify assigns a random subdomain like `cosmic-firefly-12345.netlify.app`. Rename it under Site settings → Change site name → pick something like `aircast` → site is now at `https://aircast.netlify.app`.
4. Verify: visit `https://aircast.netlify.app/app-ads.txt`. You should see the AdMob line.
5. Optional: set a redirect rule for `/` → cleaner browsing if Netlify defaults aren't great.

### Option B: GitHub Pages user site (clean URL, 5 minutes)

1. Create a new public GitHub repo named EXACTLY `<your-github-username>.github.io` (so `vikasdc.github.io`).
2. Copy the contents of `developer-site/` into that repo's root.
3. Push to `main` (default branch).
4. Repo settings → Pages → Source: `main` branch, root folder. Save.
5. Wait 1–2 minutes for GitHub to publish.
6. Site goes live at `https://vikasdc.github.io`. Verify `https://vikasdc.github.io/app-ads.txt`.

### Option C: Cloudflare Pages (3 minutes)

1. Sign up at https://pages.cloudflare.com.
2. Create new project → "Direct Upload" → drag the `developer-site/` folder.
3. Cloudflare assigns `<project-name>.pages.dev`. Site goes live in seconds.
4. Verify `https://<project-name>.pages.dev/app-ads.txt`.

## After deployment

Once your developer site is live and `/app-ads.txt` returns the AdMob line:

1. **Update Play Console "Developer contact: Website"**
   - Play Console → All apps → Aircast → Store presence → Main store listing → Contact details → Website
   - Paste your deployed URL (e.g. `https://aircast.netlify.app`)
   - Save changes
2. **Update Play Console "Privacy policy URL"**
   - Play Console → App content → Privacy policy
   - Change to `https://<your-domain>/aircast/privacy.html`
   - Save changes
3. **Wait 24–48 hours** for Play Console to push the website URL change to Google's app-ads.txt crawler.
4. **Trigger AdMob verification**
   - AdMob console → Apps → Aircast → "Check for updates" button on the app-ads.txt verification card
   - Verification usually completes within a few minutes once Play has synced

## What's in the file

`app-ads.txt` contains exactly one line:

```
google.com, pub-3814847756285692, DIRECT, f08c47fec0942fa0
```

If you ever onboard another ad network (e.g. Meta Audience Network, Unity Ads), each one adds their own line. Spec: https://iabtechlab.com/wp-content/uploads/2019/03/app-ads.txt-v1.0-final-.pdf

## Keeping it in sync

Whenever you change the privacy policy, edit `aircast/privacy.html` here AND redeploy. If you're using GitHub Pages, that's just a `git push`. If Netlify/Cloudflare, drag the updated folder again.

The canonical privacy policy text also lives at `docs/legal/privacy-policy.md` in the Aircast repo — when changes are made there, copy them across to this static file.
