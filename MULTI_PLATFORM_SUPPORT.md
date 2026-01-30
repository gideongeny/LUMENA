# LUMENA Multi-Platform Support

This document outlines the changes made to support tablets, Android TVs, and web platforms.

## Overview

LUMENA now supports:
- 📱 **Smartphones** - Original support maintained
- 📱 **Tablets** - 7" and 10"+ tablets with optimized layouts
- 📺 **Android TV** - Full Android TV support with leanback UI
- 🌐 **Web App** - Progressive Web App for browsers

## Android Changes

### 1. Tablet Support

#### Manifest Changes
- Added `supports-screens` configuration for large and xlarge screens
- Set `requiresSmallestWidthDp="600"` for tablet optimization
- Added `resizeableActivity="true"` for multi-window support

#### Resource Qualifiers
Created device-specific dimension files:

**`values-sw600dp/dimens.xml`** - 7" tablets
- Larger text sizes (24sp/18sp/14sp)
- Increased padding (24dp/16dp/12dp)
- Icon size: 48dp
- Cover art: 320dp

**`values-sw720dp/dimens.xml`** - 10"+ tablets
- Even larger text (28sp/20sp/16sp)
- More padding (32dp/20dp/16dp)
- Icon size: 56dp
- Cover art: 400dp

### 2. Android TV Support

#### Manifest Changes
```xml
<!-- TV Features -->
<uses-feature android:name="android.hardware.touchscreen" android:required="false" />
<uses-feature android:name="android.software.leanback" android:required="false" />
<uses-feature android:name="android.hardware.microphone" android:required="false" />

<!-- TV Launcher -->
<category android:name="android.intent.category.LEANBACK_LAUNCHER" />
```

#### Dependencies
Added leanback library to `build.gradle.kts`:
```kotlin
implementation("androidx.leanback:leanback:1.0.0")
```

#### TV-Specific Resources
**`values-television/dimens.xml`**
- Large text for 10-foot UI (32sp/24sp/18sp)
- Generous padding (48dp/32dp/24dp)
- Large icons: 64dp
- Large cover art: 480dp
- Focus padding: 8dp for D-pad navigation

#### TV Banner
Created `drawable/tv_banner.xml` for Android TV launcher

### 3. Build Configuration

The app now:
- Supports screen orientations: `unspecified`
- Enables multi-window mode
- Maintains compatibility with phones while adding tablet/TV support

## Web App

### Architecture

Created a complete Progressive Web App (PWA) in the `webapp/` directory:

```
webapp/
├── index.html          # Main application
├── manifest.json       # PWA manifest
├── sw.js              # Service worker
├── css/
│   └── styles.css     # Responsive styles
├── js/
│   ├── app.js         # Main app logic
│   ├── player.js      # Audio player
│   └── storage.js     # Storage utilities
└── README.md          # Web app documentation
```

### Features

#### Core Functionality
- ✅ Local music file upload and playback
- ✅ Search functionality
- ✅ Playlist management
- ✅ Settings and preferences
- ✅ Responsive design (mobile, tablet, desktop, TV)

#### PWA Features
- ✅ Offline support via Service Workers
- ✅ Install to home screen
- ✅ Media Session API integration
- ✅ Background playback
- ✅ System media controls

#### Responsive Breakpoints
- **Mobile**: < 768px
- **Tablet**: 768px - 1024px
- **Desktop**: 1024px - 1920px
- **TV/Large**: > 1920px

### Technologies Used
- Pure HTML5, CSS3, and JavaScript (no frameworks)
- Web Audio API for playback
- LocalStorage for data persistence
- Service Workers for offline functionality
- Media Session API for system integration

## Testing

### Android Testing

#### Tablet Testing
1. Test on 7" tablet (sw600dp):
   ```bash
   adb shell wm density 213
   adb shell wm size 600x1024
   ```

2. Test on 10" tablet (sw720dp):
   ```bash
   adb shell wm density 160
   adb shell wm size 800x1280
   ```

#### TV Testing
1. Use Android TV emulator in Android Studio
2. Or test on physical Android TV device
3. Verify D-pad navigation works
4. Check banner appears in TV launcher

### Web App Testing

#### Local Testing
```bash
cd webapp
python -m http.server 8000
# Open http://localhost:8000
```

#### Browser Testing
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Opera 76+

#### Device Testing
- Mobile devices (iOS/Android)
- Tablets (iPad, Android tablets)
- Desktop browsers
- Smart TVs with browsers

## Deployment

### Android Deployment
Build the APK/AAB as usual:
```bash
./gradlew assembleRelease
# or
./gradlew bundleRelease
```

The same APK/AAB now supports phones, tablets, and Android TV.

### Web App Deployment

#### Option 1: GitHub Pages
```bash
cd webapp
git subtree push --prefix webapp origin gh-pages
```

#### Option 2: Netlify
1. Drag and drop `webapp` folder to Netlify
2. Or connect GitHub repository

#### Option 3: Vercel
```bash
cd webapp
vercel deploy
```

#### Option 4: Firebase Hosting
```bash
firebase init hosting
firebase deploy
```

## User Experience

### Tablets
- Larger touch targets for easier interaction
- More content visible on screen
- Optimized spacing and typography
- Split-screen support

### Android TV
- D-pad navigation support
- Large, readable text from 10 feet away
- Focus indicators for navigation
- Remote control support
- Appears in TV launcher with banner

### Web App
- Works on any device with a browser
- Install as standalone app
- Offline functionality
- Cross-platform compatibility
- No app store required

## Future Enhancements

### Potential Improvements
- [ ] Chromecast support
- [ ] Android Auto integration
- [ ] Wear OS companion app
- [ ] Cloud sync between devices
- [ ] Web app backend for streaming
- [ ] TV-specific UI layouts in Compose
- [ ] Tablet landscape optimizations

## Compatibility

### Minimum Requirements
- **Android**: API 24+ (Android 7.0)
- **Web**: Modern browsers with ES6 support
- **Tablets**: 7" minimum screen size
- **TV**: Android TV 5.0+

### Tested Devices
- ✅ Phones (various sizes)
- ✅ 7" tablets
- ✅ 10" tablets
- ✅ Android TV emulator
- ✅ Chrome browser (desktop/mobile)
- ✅ Firefox browser
- ✅ Safari (iOS/macOS)

## Support

For issues or questions:
1. Check the README files
2. Review this documentation
3. Open an issue on GitHub
4. Check existing issues for solutions

## License

Same as the main LUMENA project.
