# Fix: Activity Class Error

## Error Message
```
Activity class {com.dn0ne.lotus/com.dn0ne.player.MainActivity} does not exist
```

## Cause
The old app with package `com.dn0ne.lotus` is still installed on your device/emulator, or Android Studio has cached the old applicationId.

## Solution

### Step 1: Uninstall Old App
1. On your device/emulator, go to **Settings → Apps**
2. Find and **uninstall** the old "Lotus" app
3. Or use ADB:
   ```bash
   adb uninstall com.dn0ne.lotus
   ```

### Step 2: Clean Build in Android Studio
1. In Android Studio, go to **Build → Clean Project**
2. Wait for it to complete
3. Then go to **Build → Rebuild Project**

### Step 3: Invalidate Caches
1. Go to **File → Invalidate Caches...**
2. Select **Invalidate and Restart**
3. Wait for Android Studio to restart

### Step 4: Check Run Configuration
1. Go to **Run → Edit Configurations...**
2. Select your app configuration
3. Make sure **Package** field shows: `com.dn0ne.lumena`
4. If it shows `com.dn0ne.lotus`, change it to `com.dn0ne.lumena`

### Step 5: Run Again
1. Click **Run** or press `Shift + F10`
2. The app should now launch correctly

## Current Configuration
- **ApplicationId**: `com.dn0ne.lumena` ✅
- **Package Name**: `com.dn0ne.player` ✅ (this is correct - they can be different)
- **MainActivity**: `com.dn0ne.player.MainActivity` ✅

## Alternative: Quick Fix via ADB
```bash
# Uninstall old app
adb uninstall com.dn0ne.lotus

# Uninstall new app if it exists
adb uninstall com.dn0ne.lumena

# Then rebuild and run
```

## Note
The applicationId (`com.dn0ne.lumena`) and package name (`com.dn0ne.player`) can be different - this is normal in Android development. The applicationId is what identifies your app on the Play Store, while the package name is the Java/Kotlin package structure.

