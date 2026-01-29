# Prompt for Antigravity: Fix Lumena App for Play Store Release

## Task
Fix all critical issues in the Lumena Android music player app to make it ready for Google Play Store release. Build a properly signed release bundle (AAB) after fixes are complete.

## Context
This is an Android music player app (Lumena) built with Kotlin, Jetpack Compose, targeting Android 7.0+ (API 24), with target SDK 35. The app is functionally complete but has critical infrastructure issues preventing Play Store release.

## Critical Issues to Fix

### 1. Release Signing Configuration (CRITICAL)
**File**: `app/build.gradle.kts`
**Current Issue**: Line 77 uses debug signing for release builds
**Required Fix**:
- Create a release signing configuration
- Generate a release keystore (or use existing if present)
- Configure `signingConfigs` block with release keystore
- Update release buildType to use release signing
- Remove the TODO comment on line 75
- Add `keystore.properties` to `.gitignore` if not already there

**Implementation**:
```kotlin
signingConfigs {
    create("release") {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()
        if (keystorePropertiesFile.exists()) {
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
}
```

If `keystore.properties` doesn't exist, create it with placeholder values and instructions.

### 2. Privacy Policy Hosting (CRITICAL)
**File**: `app/src/main/java/com/dn0ne/player/app/presentation/components/settings/PrivacyPolicyPage.kt`
**Current Issue**: Privacy policy only exists in-app, no public URL
**Required Fix**:
- Create a privacy policy HTML file in a `docs/` or `privacy/` folder
- Or create a simple GitHub Pages setup
- Add a URL string resource pointing to the hosted privacy policy
- Update PrivacyPolicyPage to optionally link to the hosted version
- Document where to host it (GitHub Pages recommended)

**Action**: Create `privacy-policy.html` in project root with the privacy policy content, and add instructions for hosting.

### 3. Deprecated Permissions (CRITICAL)
**File**: `app/src/main/AndroidManifest.xml`
**Current Issue**: Using `WRITE_EXTERNAL_STORAGE` and `requestLegacyExternalStorage` (deprecated on Android 13+)
**Required Fix**:
- Add `android:maxSdkVersion="32"` to `WRITE_EXTERNAL_STORAGE` permission
- Keep `requestLegacyExternalStorage` for Android 10-12 compatibility
- Add comments explaining the permission usage
- Ensure metadata editing works with Scoped Storage on Android 13+

**Implementation**:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
```

### 4. ProGuard Configuration (HIGH PRIORITY)
**File**: `app/proguard-rules.pro`
**Current Issue**: Line 23 has `-dontobfuscate` which disables code obfuscation
**Required Fix**:
- Remove or comment out `-dontobfuscate`
- Ensure all necessary `-keep` rules are present
- Add rules for Realm, Koin, and other libraries if needed
- Test that release build works with obfuscation enabled

### 5. Backup Rules Configuration (HIGH PRIORITY)
**Files**: 
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
**Current Issue**: Both files are empty templates
**Required Fix**:
- Configure backup rules to exclude sensitive data
- Exclude Realm database files (they should be recreated)
- Include SharedPreferences for settings
- Document what is/isn't backed up

**Implementation**:
```xml
<full-backup-content>
    <include domain="sharedpref" path="."/>
    <exclude domain="file" path="databases/"/>
</full-backup-content>
```

### 6. Error Handling - Stack Trace Clipboard (MEDIUM PRIORITY)
**Files**: 
- `app/src/main/java/com/dn0ne/player/app/data/MetadataWriterImpl.kt`
- `app/src/main/java/com/dn0ne/player/core/data/MusicScanner.kt`
**Current Issue**: Exceptions copy full stack traces to clipboard (security/privacy concern)
**Required Fix**:
- Remove clipboard copying of stack traces
- Use proper logging (Log.e or Timber)
- Show user-friendly error messages to users
- Only log detailed errors in debug builds

### 7. Add Crash Reporting (RECOMMENDED)
**File**: `app/build.gradle.kts` and new files
**Current Issue**: No crash reporting system
**Required Fix**:
- Add Firebase Crashlytics dependency (optional but recommended)
- Or add basic crash logging
- Configure crash reporting for release builds
- Add initialization in Application class

**Note**: This is optional but highly recommended. If Firebase is not desired, at least add basic crash logging.

## Build Requirements

After fixing all issues:
1. Build a release AAB: `./gradlew bundleRelease`
2. Verify the AAB is properly signed (not debug signed)
3. Test that the release build works correctly
4. Ensure ProGuard doesn't break functionality

## Files to Modify

1. `app/build.gradle.kts` - Fix signing, add dependencies if needed
2. `app/src/main/AndroidManifest.xml` - Fix permissions
3. `app/proguard-rules.pro` - Enable obfuscation
4. `app/src/main/res/xml/backup_rules.xml` - Configure backup
5. `app/src/main/res/xml/data_extraction_rules.xml` - Configure data extraction
6. `app/src/main/java/com/dn0ne/player/app/data/MetadataWriterImpl.kt` - Fix error handling
7. `app/src/main/java/com/dn0ne/player/core/data/MusicScanner.kt` - Fix error handling
8. `.gitignore` - Ensure keystore files are ignored
9. Create `privacy-policy.html` - For hosting privacy policy
10. Create `keystore.properties.example` - Template for keystore config

## Testing Checklist

After fixes:
- [ ] Release build compiles successfully
- [ ] Release AAB is signed with release key (not debug)
- [ ] App runs without crashes
- [ ] ProGuard doesn't break functionality
- [ ] Permissions work correctly on Android 13+
- [ ] Error messages are user-friendly
- [ ] No stack traces in clipboard

## Additional Notes

- The app uses Realm database, Koin for DI, Media3 for playback
- Target SDK is 35 (Android 15)
- Min SDK is 24 (Android 7.0)
- Package name: `com.dn0ne.lumena`
- Current version: 1.2.0 (versionCode: 1_002_000)

## Priority Order

1. **MUST FIX**: Release signing (#1)
2. **MUST FIX**: Privacy policy hosting (#2)
3. **MUST FIX**: Deprecated permissions (#3)
4. **SHOULD FIX**: ProGuard (#4)
5. **SHOULD FIX**: Backup rules (#5)
6. **SHOULD FIX**: Error handling (#6)
7. **RECOMMENDED**: Crash reporting (#7)

## Expected Outcome

After completion:
- App can be built with `./gradlew bundleRelease`
- Release AAB is properly signed
- All critical Play Store blockers are resolved
- App is ready for Play Store submission (after manual testing)

---

**Start by fixing issue #1 (Release Signing) as it's the most critical blocker for Play Store release.**
