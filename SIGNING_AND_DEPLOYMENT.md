# Lumena App - Signing & Deployment Instructions

## 🔐 Step 1: Configure Signing Credentials

The `keystore.properties` file has been created. You need to fill in the actual passwords:

**File**: `keystore.properties`

```properties
storePassword=YOUR_ACTUAL_KEYSTORE_PASSWORD
keyPassword=YOUR_ACTUAL_KEY_PASSWORD
keyAlias=lumena
storeFile=./app/release-key.jks
```

⚠️ **DO NOT** commit this file to Git - it's already in `.gitignore`

---

## 📦 Step 2: Build Signed Release Bundle

Once you have the keystore passwords, build the signed bundle:

```bash
./gradlew bundleRelease
```

**Output**: `app/build/outputs/bundle/release/app-release.aab`

This file is ready for Google Play Console upload.

---

## 🚀 Step 3: Upload to Google Play Console

1. Go to [play.google.com/console](https://play.google.com/console)
2. Sign in to your developer account
3. Select the Lumena app
4. Click **Release** → **Production** → **Create new release**
5. Upload the `app-release.aab` file
6. Add release notes for v1.3.0:
   ```
   - Removed debug console spam for better performance
   - Enabled code obfuscation for enhanced security
   - Optimized resources and assets
   - Improved app startup time
   - Bug fixes and stability improvements
   ```
7. Review and submit for review

---

## 📱 Alternative: Build & Sign Individual APK

If you need a signed APK instead of AAB:

```bash
./gradlew assembleRelease
```

Then manually sign with jarsigner:

```bash
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore app/release-key.jks \
  app/build/outputs/apk/release/lumena-1.3.0-universal-release.apk \
  lumena
```

---

## ✅ Quality Checklist Before Release

- [ ] `keystore.properties` filled with correct passwords
- [ ] Release signed bundle builds without errors
- [ ] Tested on physical device or emulator
- [ ] Privacy policy hosted and accessible
- [ ] Data Safety form completed in Play Console
- [ ] App screenshots uploaded (minimum 2, recommended 4-8)
- [ ] Release notes written
- [ ] Version 1.3.0 selected in Play Console

---

## 🔍 Post-Release Monitoring

1. **Monitor Crash Reports**: Check Play Console → Quality → Crashes & ANRs
2. **Review User Feedback**: Check Play Console → Reviews for user comments
3. **Check Performance**: Use Android Profiler to verify:
   - Memory usage
   - CPU usage
   - Frame rate

---

## 📋 Optimization Summary

Optimizations applied to v1.3.0:

✅ **Code Minification**
- Obfuscation reduces code size and increases security
- Unused code automatically removed

✅ **Resource Optimization**
- Unused resources removed (isShrinkResources)
- PNG files compressed (isCrunchPngs)
- Reduces APK size by 10-20%

✅ **Removed Debug Code**
- 3 debug println statements removed
- Eliminates console spam
- Improves performance and battery life

✅ **Crash Reporting**
- Automatic crash logging to device
- Files saved in app cache directory
- Can be used for diagnostics

---

## 🆘 Troubleshooting

### Error: "SigningConfig 'release' is missing required property"
**Solution**: Fill in `keystore.properties` with the correct passwords and paths

### Error: "keystore.properties not found"
**Solution**: The file is auto-generated but needs actual credentials. It's in `.gitignore` for security.

### Build takes too long
**Solution**: This is normal for first release build with optimizations. Subsequent builds will be faster.

### APK larger than expected
**Solution**: This is the universal APK (supports all architectures). Use arch-specific versions for distribution:
- **arm64-v8a**: 43.7 MB (modern Android, most compatible)
- **armeabi-v7a**: 37.6 MB (older Android devices)

---

## 📞 Support

For issues with:
- **ProGuard errors**: Check `app/proguard-rules.pro`
- **Build failures**: Run `./gradlew clean` and try again
- **Signing issues**: Verify keystore file and passwords are correct
- **Play Store upload**: Contact Google Play Support

**Last Updated**: January 29, 2026
**Version**: 1.3.0
