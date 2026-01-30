# Icons Directory

Place your app icons here:

- `icon-192.png` - 192x192 icon for PWA
- `icon-512.png` - 512x512 icon for PWA

You can generate these from your existing Android app icons or create new ones.

## Generating Icons

### From Android Icons
You can export your existing Android launcher icons:
1. Navigate to `app/src/main/res/mipmap-xxxhdpi/`
2. Copy `ic_launcher.png` and resize to 192x192 and 512x512
3. Place them in this directory

### Using Online Tools
- [PWA Builder Image Generator](https://www.pwabuilder.com/imageGenerator)
- [RealFaviconGenerator](https://realfavicongenerator.net/)
- [Favicon.io](https://favicon.io/)

### Using Command Line (ImageMagick)
```bash
# Resize to 192x192
convert ic_launcher.png -resize 192x192 icon-192.png

# Resize to 512x512
convert ic_launcher.png -resize 512x512 icon-512.png
```

## Icon Requirements

- **Format**: PNG
- **Sizes**: 192x192 and 512x512 pixels
- **Background**: Can be transparent or solid
- **Purpose**: Used for PWA installation and app icon
