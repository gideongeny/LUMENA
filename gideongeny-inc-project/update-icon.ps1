# Script to update all icon files with the new Lumena logo
# Usage: .\update-icon.ps1 -LogoFile "lumena-icon.png"

param(
    [Parameter(Mandatory=$true)]
    [string]$LogoFile
)

if (-not (Test-Path $LogoFile)) {
    Write-Host "Error: Logo file '$LogoFile' not found!" -ForegroundColor Red
    Write-Host "Please place your logo PNG file in the project root directory." -ForegroundColor Yellow
    exit 1
}

Write-Host "Updating all icon files with: $LogoFile" -ForegroundColor Green

# Replace all icon files
$iconFiles = @(
    "app\src\main\res\drawable\ic_launcher_foreground_new.png",
    "app\src\main\res\drawable-hdpi\ic_launcher_foreground_new.png",
    "app\src\main\res\drawable-mdpi\ic_launcher_foreground_new.png",
    "app\src\main\res\drawable-xhdpi\ic_launcher_foreground_new.png",
    "app\src\main\res\drawable-xxhdpi\ic_launcher_foreground_new.png",
    "fastlane\metadata\android\en-US\images\icon.png",
    "fastlane\metadata\android\en-US\images\icon-fit.png"
)

foreach ($file in $iconFiles) {
    if (Test-Path $file) {
        Copy-Item $LogoFile $file -Force
        Write-Host "  Updated: $file" -ForegroundColor Cyan
    } else {
        Write-Host "  Warning: $file not found, skipping..." -ForegroundColor Yellow
    }
}

Write-Host "`nIcon update complete!" -ForegroundColor Green
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Rebuild the app: .\gradlew clean assembleDebug" -ForegroundColor White
Write-Host "2. Test on device to verify the icon looks good" -ForegroundColor White
Write-Host "3. Commit changes: git add app/src/main/res fastlane && git commit -m 'Update app icon'" -ForegroundColor White

