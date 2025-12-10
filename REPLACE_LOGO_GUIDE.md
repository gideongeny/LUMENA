# How to Replace the App Logo

## Option 1: Use Your Custom Logo Image

### Step 1: Prepare Your Logo
- Your logo image should be:
  - **Format**: PNG with transparency
  - **Size**: At least 512x512 pixels (larger is better)
  - **Background**: Transparent or solid color
  - **Content**: Your logo centered in the image

### Step 2: Replace the Icon Files

1. **Copy your logo file** to these locations:
   ```
   app/src/main/res/drawable/ic_launcher_foreground_new.png
   app/src/main/res/drawable-hdpi/ic_launcher_foreground_new.png
   app/src/main/res/drawable-mdpi/ic_launcher_foreground_new.png
   app/src/main/res/drawable-xhdpi/ic_launcher_foreground_new.png
   app/src/main/res/drawable-xxhdpi/ic_launcher_foreground_new.png
   ```

2. **Also update the Play Store icon**:
   ```
   fastlane/metadata/android/en-US/images/icon.png
   fastlane/metadata/android/en-US/images/icon-fit.png
   ```

### Step 3: Quick Replace Script (Windows PowerShell)

If your logo file is named `my-logo.png` in the project root:

```powershell
# Replace all icon files with your logo
$logoPath = "my-logo.png"
Copy-Item $logoPath "app\src\main\res\drawable\ic_launcher_foreground_new.png" -Force
Copy-Item $logoPath "app\src\main\res\drawable-hdpi\ic_launcher_foreground_new.png" -Force
Copy-Item $logoPath "app\src\main\res\drawable-mdpi\ic_launcher_foreground_new.png" -Force
Copy-Item $logoPath "app\src\main\res\drawable-xhdpi\ic_launcher_foreground_new.png" -Force
Copy-Item $logoPath "app\src\main\res\drawable-xxhdpi\ic_launcher_foreground_new.png" -Force
Copy-Item $logoPath "fastlane\metadata\android\en-US\images\icon.png" -Force
Copy-Item $logoPath "fastlane\metadata\android\en-US\images\icon-fit.png" -Force
```

## Option 2: Use Android Studio Image Asset Studio

1. **Open Android Studio**
2. Right-click on `app/src/main/res` folder
3. Select **New → Image Asset**
4. Choose **Launcher Icons (Adaptive and Legacy)**
5. **Foreground Layer**:
   - Click the image icon
   - Select your logo file
   - Adjust scaling if needed
6. **Background Layer**:
   - Choose a solid color (e.g., white, or match your logo)
7. Click **Next** and **Finish**

This will automatically generate all required icon sizes!

## Option 3: Online Icon Generator

Use online tools to generate Android icons:
- [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html)
- [App Icon Generator](https://www.appicon.co/)

Upload your logo and download the generated icons, then replace the files in the `res` folders.

## After Replacing

1. **Rebuild the app** to see the new icon:
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

2. **Test on device/emulator** to verify the icon looks good

3. **Commit the changes**:
   ```bash
   git add app/src/main/res/drawable*/ic_launcher_foreground_new.png
   git add fastlane/metadata/android/en-US/images/icon*.png
   git commit -m "Update app logo"
   git push
   ```

## Tips

- **Keep it simple**: Icons should be recognizable at small sizes
- **Use transparency**: PNG with alpha channel works best
- **Test on device**: Icons can look different on actual devices
- **Adaptive icons**: Make sure important parts are in the center (safe zone)

