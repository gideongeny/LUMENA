# Create GitHub Release

## ✅ Tag Created and Pushed

I've created and pushed the release tag `v1.2.0` to GitHub.

## Next Steps: Create Release on GitHub

### Option 1: Using GitHub Web Interface (Recommended)

1. **Go to your repository**: [https://github.com/gideongeny/LUMENA](https://github.com/gideongeny/LUMENA)

2. **Click "Releases"** (on the right sidebar, or go to: https://github.com/gideongeny/LUMENA/releases)

3. **Click "Draft a new release"**

4. **Fill in the details**:
   - **Tag**: Select `v1.2.0` (should appear in dropdown)
   - **Release title**: `Lumena v1.2.0 - First Release`
   - **Description**: Copy from `RELEASE_NOTES_v1.2.0.md` or use the content below

5. **Release Notes** (copy this):
```markdown
# Lumena v1.2.0 - First Release

## 🎉 First Release of Lumena

Lumena is a modern music player for Android, redesigned from the ground up with a focus on user experience and beautiful design.

## ✨ Features

- **Multiple Audio Formats**: Enjoy your favorite music in MP3, FLAC, OGG, WAV, and more
- **Smart Organization**: Easily browse tracks, albums, artists, and genres
- **Custom Playlists**: Create and manage your own playlists
- **Synchronized Lyrics**: Enhance your listening experience with synchronized lyrics from LRCLIB
- **Metadata Management**: Manually update track details or fetch accurate info from MusicBrainz
- **Material You Design**: Beautiful UI designed with Material You and supports dynamic color palettes
- **Dark Theme**: Full dark theme support
- **Offline Playback**: Play your music without an internet connection

## 📱 Requirements

- Android 7.0 (API 24) or higher
- Storage permission for music files

## 🔗 Links

- **Repository**: https://github.com/gideongeny/LUMENA
- **Feedback**: gideongeng@gmail.com

## 📝 Changes in This Release

### Initial Release
- Complete rebrand from Lotus to Lumena
- Updated app icon and branding
- Fixed app stability issues
- Improved performance
- Updated repository and contact information

## 📄 License

Lumena is licensed under GPLv3
```

6. **Attach APK** (optional):
   - If you have a release APK, click "Attach binaries"
   - Upload `app/build/outputs/apk/release/lumena-1.2.0-release.apk`

7. **Click "Publish release"**

### Option 2: Using GitHub CLI

If you have GitHub CLI installed:

```bash
gh release create v1.2.0 \
  --title "Lumena v1.2.0 - First Release" \
  --notes-file RELEASE_NOTES_v1.2.0.md \
  app/build/outputs/apk/release/lumena-1.2.0-release.apk
```

### Option 3: Build and Attach APK

To include an APK in the release:

1. **Build release APK**:
   ```bash
   ./gradlew assembleRelease
   ```

2. **Find the APK**:
   - Location: `app/build/outputs/apk/release/lumena-1.2.0-release.apk`

3. **Upload when creating release** on GitHub

## Release Information

- **Version**: 1.2.0
- **Version Code**: 1002000
- **Tag**: v1.2.0
- **Application ID**: com.dn0ne.lumena
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## After Creating Release

Once published, users can:
- Download the release from: https://github.com/gideongeny/LUMENA/releases
- View release notes
- Download APK files
- See changelog

Your release will be live at: https://github.com/gideongeny/LUMENA/releases/tag/v1.2.0

