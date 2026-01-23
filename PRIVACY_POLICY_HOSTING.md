# Privacy Policy Hosting Instructions

## Overview
The privacy policy HTML file is ready and needs to be hosted online for Play Store compliance. Google Play Store requires a publicly accessible URL for the privacy policy.

## Files Available
- `privacy-policy.html` - Main privacy policy file (project root)
- `docs/privacy-policy.html` - Copy in docs folder

## Hosting Options

### Option 1: GitHub Pages (Recommended - Free & Easy)

#### Step 1: Enable GitHub Pages
1. Go to your repository: `https://github.com/gideongeny/LUMENA`
2. Click **Settings** → **Pages**
3. Under **Source**, select **Deploy from a branch**
4. Choose **main** branch and **/docs** folder
5. Click **Save**

#### Step 2: Verify File Location
- Ensure `docs/privacy-policy.html` exists (it does)
- Or move `privacy-policy.html` to `docs/` folder if needed

#### Step 3: Access URL
Your privacy policy will be available at:
```
https://gideongeny.github.io/LUMENA/privacy-policy.html
```

#### Step 4: Update Play Console
1. Go to Google Play Console
2. Navigate to **Policy** → **App content** → **Privacy policy**
3. Enter the URL: `https://gideongeny.github.io/LUMENA/privacy-policy.html`
4. Click **Save**

---

### Option 2: GitHub Raw Content (Alternative)

If GitHub Pages doesn't work, you can use raw GitHub content:

```
https://raw.githubusercontent.com/gideongeny/LUMENA/main/privacy-policy.html
```

**Note**: This may not render as nicely as GitHub Pages, but it's still accessible.

---

### Option 3: Personal Website

If you have a personal website:
1. Upload `privacy-policy.html` to your web server
2. Access it via: `https://yourdomain.com/privacy-policy.html`
3. Add the URL to Play Console

---

### Option 4: Free Hosting Services

Other free hosting options:
- **Netlify** - Drag and drop the HTML file
- **Vercel** - Connect GitHub repo
- **GitHub Gist** - For simple hosting (not recommended for Play Store)

---

## Verification

After hosting, verify:
1. ✅ URL is publicly accessible (open in incognito mode)
2. ✅ Privacy policy content is complete
3. ✅ URL is added to Play Console
4. ✅ URL works on mobile browsers

## Testing

Test the privacy policy URL:
```bash
# Check if URL is accessible
curl -I https://gideongeny.github.io/LUMENA/privacy-policy.html

# Should return HTTP 200 OK
```

## Play Console Setup

1. **Go to Play Console**: https://play.google.com/console
2. **Select your app**: Lumena
3. **Navigate to**: Policy → App content → Privacy policy
4. **Enter URL**: `https://gideongeny.github.io/LUMENA/privacy-policy.html`
5. **Click Save**

## Important Notes

- ⚠️ **Keep the URL updated** - If you change the privacy policy, update the HTML file
- ⚠️ **Test regularly** - Ensure the URL remains accessible
- ⚠️ **Version control** - Keep privacy policy changes in git history
- ⚠️ **Last updated date** - Update the "Last Updated" date in the HTML when making changes

## Current Privacy Policy Status

- ✅ HTML file created and formatted
- ✅ Content is complete and Play Store compliant
- ⚠️ **Needs hosting** - Follow steps above to host
- ⚠️ **Needs Play Console URL** - Add URL after hosting

---

**Recommended Action**: Use GitHub Pages (Option 1) - it's free, easy, and reliable.
