# LUMENA Web App

A modern, Progressive Web App (PWA) version of LUMENA Music Player.

## Features

- 🎵 **Local Music Playback** - Upload and play your music files
- 📱 **Responsive Design** - Works on phones, tablets, and desktops
- 📺 **TV Optimized** - Enhanced UI for large screens
- 🔍 **Search** - Find your tracks quickly
- 📋 **Playlists** - Create and manage playlists
- 💾 **Offline Support** - Works offline with service workers
- 🎨 **Modern UI** - Clean, dark-themed interface
- ⚡ **Fast & Lightweight** - No heavy frameworks

## Installation

### Local Development

1. Clone the repository
2. Navigate to the `webapp` directory
3. Serve the files using any static file server:

```bash
# Using Python
python -m http.server 8000

# Using Node.js http-server
npx http-server

# Using PHP
php -S localhost:8000
```

4. Open your browser and navigate to `http://localhost:8000`

### Deploy to Web

You can deploy this web app to any static hosting service:

- **GitHub Pages**: Push to a `gh-pages` branch
- **Netlify**: Drag and drop the `webapp` folder
- **Vercel**: Connect your repository
- **Firebase Hosting**: Use `firebase deploy`

## Usage

### Upload Music

1. Click the "Upload Music" button in the Library view
2. Select one or more audio files from your device
3. The tracks will be added to your library

### Play Music

1. Click on any track in your library to start playing
2. Use the player controls at the bottom to:
   - Play/Pause
   - Skip to next/previous track
   - Shuffle playback
   - Repeat tracks
   - Adjust volume

### Create Playlists

1. Navigate to the Playlists view
2. Click "Create Playlist"
3. Enter a name for your playlist
4. Add tracks to your playlist

### Search

1. Navigate to the Search view
2. Type in the search box to find tracks by title or artist

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Opera 76+

## PWA Features

- **Install to Home Screen**: Add LUMENA to your device's home screen
- **Offline Access**: Continue using the app without internet
- **Background Playback**: Music continues playing when app is in background
- **Media Session API**: Control playback from system media controls

## Tablet & TV Support

The web app automatically adapts to different screen sizes:

- **Tablets (7"+)**: Larger text and controls
- **Tablets (10"+)**: Enhanced spacing and layout
- **TV/Large Screens**: Optimized for 10-foot UI with larger elements

## Technical Details

### Technologies Used

- **HTML5** - Semantic markup
- **CSS3** - Modern styling with CSS Grid and Flexbox
- **Vanilla JavaScript** - No framework dependencies
- **Service Workers** - Offline functionality
- **Web Audio API** - Audio playback
- **Media Session API** - System media controls
- **LocalStorage** - Data persistence

### File Structure

```
webapp/
├── index.html          # Main HTML file
├── manifest.json       # PWA manifest
├── sw.js              # Service worker
├── css/
│   └── styles.css     # All styles
├── js/
│   ├── app.js         # Main app logic
│   ├── player.js      # Audio player
│   └── storage.js     # Storage utilities
└── icons/             # App icons
```

## Privacy

- All music files are stored locally in your browser
- No data is sent to external servers
- No tracking or analytics

## License

Same as the main LUMENA project

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
