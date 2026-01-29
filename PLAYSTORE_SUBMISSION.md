# Play Store Submission Guide

## Pre-Submission Checklist

### ✅ Build & Signing
- [x] Release APK built with ProGuard obfuscation
- [x] Keystore configured (release-key.jks)
- [x] Native library issues resolved (ytdlp removed)
- [x] Version: 1.3.0 (versionCode: 130)

### 📦 App Information

**Package Name:** `com.dn0ne.lumena`
**App Name:** LUMENA
**Category:** Music & Audio
**Content Rating:** Not yet determined (complete in console)

### 🎯 Minimum Requirements
- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36 (Android 15)
- **Java Version:** 17

### 📝 Release Notes

**Version 1.3.0 - Current Release**
- Fixed YouTube extraction with InnerTube support
- Improved widget rendering with Palette color extraction
- Added high refresh rate display mode selection (API 30+)
- Added FPS monitoring diagnostics
- Enhanced error handling for online playback
- Removed youtube-dl native dependency to prevent crashes
- ProGuard obfuscation for optimized APK

---

## Play Store Submission Steps

### 1. Prepare APK
- Release APK location: `app/build/outputs/apk/release/lumena-1.3.0-release.apk`
- Verify file size and ensure it's under 100 MB
- Command to check: `ls -lh app/build/outputs/apk/release/`

### 2. Create/Update App Listing

**App Title:** LUMENA
**Short Description (80 char max):**
```
Music player with YouTube search & local file support
```

**Full Description (4000 char max):**
```
LUMENA is a feature-rich music player that seamlessly combines local music 
library management with online streaming capabilities via YouTube.

Features:
• Local Music Library: Browse, organize, and play your downloaded music
• YouTube Integration: Search and play music directly from YouTube
• Smart Widgets: Beautiful home screen widgets with album art display
• Metadata Editing: Edit track information and cover artwork
• Playlist Support: Create and manage custom playlists
• Material Design 3: Modern, fluid user interface
• Dark Mode Support: Easy on the eyes in any lighting
• High Refresh Rate Support: Smooth 120Hz+ display optimization
• No Ads: Clean, distraction-free experience

LUMENA prioritizes performance, ease of use, and user privacy.
```

### 3. Screenshots & Graphics

**Required Sizes:**
- Phone Screenshots (required): 1080×1920 px (up to 8 images)
- 7-inch Tablet Screenshots: 1200×1920 px (optional)
- Feature Graphic: 1024×500 px (required)
- Icon (512×512 px): Use existing app logo

**Suggested Screenshot Content:**
1. Home screen with local music library
2. Now playing screen with controls
3. YouTube search functionality
4. Widget showcase
5. Settings/theme options
6. Playlist management
7. Search results
8. App widgets

### 4. Content Rating Questionnaire
- **Content Rating Category:** Music Player / Productivity
- **Target Audience:** All ages (Music and Audio category)
- **Ads:** None
- **User-Generated Content:** No
- **Restricted Content:** No

### 5. Privacy & Permissions

**Permissions Used:**
- READ_MEDIA_AUDIO: Access local music library
- WRITE_EXTERNAL_STORAGE: Edit track metadata (Android < 13)
- INTERNET: Download YouTube metadata and audio

**Privacy Policy URL:**
- Hosted at: https://gideongeny.github.io/LUMENA (docs/privacy-policy.html)
- Include in Play Store listing

### 6. Release Configuration

**Release Type:** Production
**Staged Rollout:** Optional (start with 5-10%)
**Auto-update Details:** Not required

---

## Build Release APK

```bash
cd /path/to/LUMENA
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/lumena-1.3.0-release.apk
```

## Sign APK (if needed)
The APK is already signed during build with release-key.jks configured in `keystore.properties`.

Verify signature:
```bash
jarsigner -verify -verbose app/build/outputs/apk/release/lumena-1.3.0-release.apk
```

---

## Console Configuration

1. **Go to:** Google Play Console > LUMENA > Release
2. **Create Release:**
   - Select "Production"
   - Upload APK
   - Add release notes
   - Set rollout percentage (100% for full release)

3. **Complete Listing:**
   - Add app title, description, screenshots
   - Set category, content rating
   - Link privacy policy
   - Add developer contact info

4. **Review Policy Compliance:**
   - No dangerous permissions misuse
   - Privacy policy covers data collection
   - Content rating appropriate for target audience

---

## Post-Launch

### Monitoring
- Monitor crash reports in Play Console
- Track user ratings and reviews
- Monitor app performance metrics

### Updates
- For future versions, increment `versionCode` in `build.gradle.kts`
- Update `versionName` for user-facing version
- Add release notes describing changes

### Analytics
- Enable Firebase Analytics (optional)
- Track key user flows and retention

---

## Support Resources
- [Play Store Policies](https://play.google.com/about/developer-content-policy/)
- [App Quality Guidelines](https://support.google.com/googleplay/android-developer)
- [Submission FAQ](https://support.google.com/googleplay/android-developer/answer/113469)

**Status:** Ready for submission after screenshots and metadata are completed.
