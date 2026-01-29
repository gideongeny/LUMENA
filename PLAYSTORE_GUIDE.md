# Google Play Store Upload Guide for Lumena

## Prerequisites

1. **Google Play Console Account**
   - Create a Google Play Developer account at [play.google.com/console](https://play.google.com/console)
   - Pay the one-time $25 registration fee
   - Complete your developer profile

2. **App Signing Key**
   - Generate a release keystore for signing your app
   - Keep this keystore safe - you'll need it for all future updates

## Step 1: Generate Release Keystore

```bash
keytool -genkey -v -keystore lumena-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias lumena
```

**Important**: Save the keystore password and key alias password securely!

## Step 2: Configure Signing in build.gradle.kts

1. Create `keystore.properties` file in the project root (add to .gitignore):
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=lumena
storeFile=path/to/lumena-release-key.jks
```

2. Update `app/build.gradle.kts` to use release signing:

```kotlin
android {
    // ... existing code ...
    
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }
            
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    
    buildTypes {
        release {
            // ... existing code ...
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## Step 3: Build Release APK/AAB

### Option A: Build AAB (Recommended for Play Store)
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

### Option B: Build APK
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/lumena-1.2.0-release.apk`

## Step 4: Create App in Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Click **Create app**
3. Fill in:
   - **App name**: Lumena
   - **Default language**: English (United States)
   - **App or game**: App
   - **Free or paid**: Free
   - **Declarations**: Accept terms

## Step 5: Complete Store Listing

### Required Information:
- **App name**: Lumena
- **Short description**: Music player designed with Material You
- **Full description**: (Use content from `fastlane/metadata/android/en-US/full_description.txt`)
- **App icon**: Upload `fastlane/metadata/android/en-US/images/icon.png` (512x512px)
- **Feature graphic**: 1024x500px image
- **Screenshots**: Upload screenshots from `fastlane/metadata/android/en-US/images/phoneScreenshots/`
  - At least 2 phone screenshots required
  - Recommended: 4-8 screenshots

### Privacy Policy
- Create a privacy policy (required for apps that access user data)
- Host it online (GitHub Pages, your website, etc.)
- Add the URL in Play Console

## Step 6: Content Rating

1. Complete the content rating questionnaire
2. Answer questions about your app's content
3. Get your rating (usually "Everyone" for music players)

## Step 7: Set Up App Access

- Choose if your app is available to all users or specific groups
- For initial release, you can use **Internal testing** or **Closed testing**

## Step 8: Upload Your App

1. Go to **Production** (or Testing track)
2. Click **Create new release**
3. Upload your AAB file (`app-release.aab`)
4. Add **Release notes** (what's new in this version)
5. Click **Save**

## Step 9: Review and Submit

1. Review all sections:
   - ✅ Store listing
   - ✅ App content
   - ✅ Pricing & distribution
   - ✅ Content rating
   - ✅ Target audience
   - ✅ News apps (if applicable)
   - ✅ Ads (if applicable)
   - ✅ Data safety
   - ✅ App access

2. Click **Submit for review**

## Step 10: Wait for Review

- Review typically takes 1-3 days
- You'll receive email notifications about status
- Address any issues if the app is rejected

## Important Notes

### Data Safety Section
You'll need to declare:
- What data your app collects (if any)
- How data is used
- Data sharing practices

For Lumena:
- **Audio files**: Collected, used for playback, not shared
- **Media library access**: Required for music playback
- **Internet**: Used for lyrics and metadata fetching

### Permissions Declaration
Declare why you need each permission:
- `READ_MEDIA_AUDIO`: To scan and play music files
- `READ_EXTERNAL_STORAGE`: For Android 12 and below compatibility
- `WRITE_EXTERNAL_STORAGE`: For saving metadata edits
- `INTERNET`: For fetching lyrics and metadata

### Version Management
- Each update needs a higher `versionCode`
- Current: `1_002_000` (1.2.0)
- Next update: Increment versionCode and versionName

## Testing Before Release

1. **Internal Testing**: Test with a small group
2. **Closed Testing**: Test with a larger group
3. **Open Testing**: Public beta testing
4. **Production**: Public release

## Resources

- [Play Console Help](https://support.google.com/googleplay/android-developer)
- [App Bundle Guide](https://developer.android.com/guide/app-bundle)
- [Play Store Policies](https://play.google.com/about/developer-content-policy/)

## Troubleshooting

### Common Issues:
1. **App rejected for permissions**: Add detailed explanations in Data Safety
2. **Icon not showing**: Ensure 512x512px PNG with transparency
3. **Screenshots rejected**: Follow size requirements (16:9 or 9:16 ratio)
4. **Signing errors**: Verify keystore configuration

Good luck with your Play Store release! 🚀

