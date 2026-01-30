// Main App Controller
class MusicApp {
    constructor() {
        this.currentView = 'library';
        this.tracks = [];
        this.playlists = [];
        this.init();
    }

    init() {
        this.setupNavigation();
        this.setupUpload();
        this.setupSearch();
        this.setupPlaylists();
        this.setupSettings();
        this.loadFromStorage();
    }

    setupNavigation() {
        const navButtons = document.querySelectorAll('.nav-btn');
        navButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const view = btn.dataset.view;
                this.switchView(view);
            });
        });
    }

    switchView(viewName) {
        // Update nav buttons
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.view === viewName);
        });

        // Update views
        document.querySelectorAll('.view').forEach(view => {
            view.classList.remove('active');
        });
        document.getElementById(`${viewName}-view`).classList.add('active');

        this.currentView = viewName;
    }

    setupUpload() {
        const uploadBtn = document.getElementById('upload-btn');
        const fileInput = document.getElementById('file-input');

        uploadBtn.addEventListener('click', () => {
            fileInput.click();
        });

        fileInput.addEventListener('change', (e) => {
            this.handleFileUpload(e.target.files);
        });
    }

    async handleFileUpload(files) {
        const trackList = document.getElementById('track-list');
        
        for (let file of files) {
            if (file.type.startsWith('audio/')) {
                const track = await this.processAudioFile(file);
                this.tracks.push(track);
                this.addTrackToUI(track);
            }
        }

        this.saveToStorage();
        this.updateEmptyState();
    }

    async processAudioFile(file) {
        return new Promise((resolve) => {
            const audio = new Audio();
            const url = URL.createObjectURL(file);
            
            audio.addEventListener('loadedmetadata', () => {
                const track = {
                    id: Date.now() + Math.random(),
                    title: file.name.replace(/\.[^/.]+$/, ''),
                    artist: 'Unknown Artist',
                    duration: audio.duration,
                    url: url,
                    file: file
                };
                resolve(track);
            });

            audio.src = url;
        });
    }

    addTrackToUI(track) {
        const trackList = document.getElementById('track-list');
        const emptyState = trackList.querySelector('.empty-state');
        
        if (emptyState) {
            emptyState.remove();
        }

        const trackItem = document.createElement('div');
        trackItem.className = 'track-item';
        trackItem.dataset.trackId = track.id;
        trackItem.innerHTML = `
            <div class="track-cover-small">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M9 18V5l12-2v13"/>
                    <circle cx="6" cy="18" r="3"/>
                    <circle cx="18" cy="16" r="3"/>
                </svg>
            </div>
            <div class="track-item-info">
                <div class="track-item-title">${track.title}</div>
                <div class="track-item-artist">${track.artist}</div>
            </div>
            <div class="track-item-duration">${this.formatDuration(track.duration)}</div>
        `;

        trackItem.addEventListener('click', () => {
            window.player.loadTrack(track);
        });

        trackList.appendChild(trackItem);
    }

    formatDuration(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    updateEmptyState() {
        const trackList = document.getElementById('track-list');
        if (this.tracks.length === 0 && !trackList.querySelector('.empty-state')) {
            trackList.innerHTML = `
                <div class="empty-state">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M9 18V5l12-2v13"/>
                        <circle cx="6" cy="18" r="3"/>
                        <circle cx="18" cy="16" r="3"/>
                    </svg>
                    <p>No music in your library</p>
                    <p class="empty-subtitle">Upload some tracks to get started</p>
                </div>
            `;
        }
    }

    setupSearch() {
        const searchInput = document.getElementById('search-input');
        let searchTimeout;

        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                this.performSearch(e.target.value);
            }, 300);
        });
    }

    performSearch(query) {
        const searchResults = document.getElementById('search-results');
        
        if (!query.trim()) {
            searchResults.innerHTML = `
                <div class="empty-state">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="11" cy="11" r="8"/>
                        <path d="m21 21-4.35-4.35"/>
                    </svg>
                    <p>Search for music</p>
                </div>
            `;
            return;
        }

        const results = this.tracks.filter(track => 
            track.title.toLowerCase().includes(query.toLowerCase()) ||
            track.artist.toLowerCase().includes(query.toLowerCase())
        );

        if (results.length === 0) {
            searchResults.innerHTML = `
                <div class="empty-state">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="11" cy="11" r="8"/>
                        <path d="m21 21-4.35-4.35"/>
                    </svg>
                    <p>No results found</p>
                    <p class="empty-subtitle">Try a different search term</p>
                </div>
            `;
            return;
        }

        searchResults.innerHTML = '<div class="track-list"></div>';
        const trackList = searchResults.querySelector('.track-list');
        
        results.forEach(track => {
            const trackItem = document.createElement('div');
            trackItem.className = 'track-item';
            trackItem.innerHTML = `
                <div class="track-cover-small">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M9 18V5l12-2v13"/>
                        <circle cx="6" cy="18" r="3"/>
                        <circle cx="18" cy="16" r="3"/>
                    </svg>
                </div>
                <div class="track-item-info">
                    <div class="track-item-title">${track.title}</div>
                    <div class="track-item-artist">${track.artist}</div>
                </div>
                <div class="track-item-duration">${this.formatDuration(track.duration)}</div>
            `;
            trackItem.addEventListener('click', () => {
                window.player.loadTrack(track);
            });
            trackList.appendChild(trackItem);
        });
    }

    setupPlaylists() {
        const createBtn = document.getElementById('create-playlist-btn');
        createBtn.addEventListener('click', () => {
            this.createPlaylist();
        });
    }

    createPlaylist() {
        const name = prompt('Enter playlist name:');
        if (name) {
            const playlist = {
                id: Date.now(),
                name: name,
                tracks: []
            };
            this.playlists.push(playlist);
            this.addPlaylistToUI(playlist);
            this.saveToStorage();
        }
    }

    addPlaylistToUI(playlist) {
        const playlistGrid = document.getElementById('playlist-grid');
        const emptyState = playlistGrid.querySelector('.empty-state');
        
        if (emptyState) {
            emptyState.remove();
        }

        const playlistCard = document.createElement('div');
        playlistCard.className = 'playlist-card';
        playlistCard.innerHTML = `
            <div class="playlist-cover">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M21 15V6"/>
                    <path d="M18.5 18a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z"/>
                    <path d="M12 12H3"/>
                    <path d="M16 6H3"/>
                    <path d="M12 18H3"/>
                </svg>
            </div>
            <div class="playlist-name">${playlist.name}</div>
            <div class="playlist-count">${playlist.tracks.length} tracks</div>
        `;
        playlistGrid.appendChild(playlistCard);
    }

    setupSettings() {
        const themeSelect = document.getElementById('theme-select');
        const qualitySelect = document.getElementById('quality-select');

        themeSelect.addEventListener('change', (e) => {
            this.applyTheme(e.target.value);
        });

        qualitySelect.addEventListener('change', (e) => {
            this.saveSettings();
        });

        this.loadSettings();
    }

    applyTheme(theme) {
        // Theme implementation would go here
        this.saveSettings();
    }

    saveToStorage() {
        try {
            localStorage.setItem('lumena_tracks', JSON.stringify(
                this.tracks.map(t => ({
                    id: t.id,
                    title: t.title,
                    artist: t.artist,
                    duration: t.duration
                }))
            ));
            localStorage.setItem('lumena_playlists', JSON.stringify(this.playlists));
        } catch (e) {
            console.error('Failed to save to storage:', e);
        }
    }

    loadFromStorage() {
        try {
            const tracks = localStorage.getItem('lumena_tracks');
            const playlists = localStorage.getItem('lumena_playlists');
            
            if (tracks) {
                // Note: We can't restore file URLs, user needs to re-upload
                this.updateEmptyState();
            }
            
            if (playlists) {
                this.playlists = JSON.parse(playlists);
                this.playlists.forEach(p => this.addPlaylistToUI(p));
            }
        } catch (e) {
            console.error('Failed to load from storage:', e);
        }
    }

    saveSettings() {
        const settings = {
            theme: document.getElementById('theme-select').value,
            quality: document.getElementById('quality-select').value
        };
        localStorage.setItem('lumena_settings', JSON.stringify(settings));
    }

    loadSettings() {
        try {
            const settings = JSON.parse(localStorage.getItem('lumena_settings'));
            if (settings) {
                document.getElementById('theme-select').value = settings.theme || 'dark';
                document.getElementById('quality-select').value = settings.quality || 'high';
            }
        } catch (e) {
            console.error('Failed to load settings:', e);
        }
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.app = new MusicApp();
});
