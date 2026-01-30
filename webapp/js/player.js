// Audio Player Controller
class AudioPlayer {
    constructor() {
        this.audio = new Audio();
        this.currentTrack = null;
        this.isPlaying = false;
        this.isShuffle = false;
        this.repeatMode = 'off'; // off, one, all
        this.volume = 0.7;
        this.queue = [];
        this.currentIndex = 0;
        
        this.init();
    }

    init() {
        this.setupControls();
        this.setupAudioEvents();
        this.setupProgressBar();
        this.setupVolumeControl();
        this.audio.volume = this.volume;
    }

    setupControls() {
        const playBtn = document.getElementById('play-btn');
        const prevBtn = document.getElementById('prev-btn');
        const nextBtn = document.getElementById('next-btn');
        const shuffleBtn = document.getElementById('shuffle-btn');
        const repeatBtn = document.getElementById('repeat-btn');

        playBtn.addEventListener('click', () => this.togglePlay());
        prevBtn.addEventListener('click', () => this.playPrevious());
        nextBtn.addEventListener('click', () => this.playNext());
        shuffleBtn.addEventListener('click', () => this.toggleShuffle());
        repeatBtn.addEventListener('click', () => this.toggleRepeat());
    }

    setupAudioEvents() {
        this.audio.addEventListener('play', () => this.onPlay());
        this.audio.addEventListener('pause', () => this.onPause());
        this.audio.addEventListener('ended', () => this.onEnded());
        this.audio.addEventListener('timeupdate', () => this.onTimeUpdate());
        this.audio.addEventListener('loadedmetadata', () => this.onLoadedMetadata());
        this.audio.addEventListener('error', (e) => this.onError(e));
    }

    setupProgressBar() {
        const progressSlider = document.querySelector('.progress-slider');
        const progressBar = document.querySelector('.progress-bar');

        progressSlider.addEventListener('input', (e) => {
            const percent = e.target.value;
            this.audio.currentTime = (percent / 100) * this.audio.duration;
        });

        progressBar.addEventListener('click', (e) => {
            const rect = progressBar.getBoundingClientRect();
            const percent = (e.clientX - rect.left) / rect.width;
            this.audio.currentTime = percent * this.audio.duration;
        });
    }

    setupVolumeControl() {
        const volumeSlider = document.querySelector('.volume-slider');
        const volumeBtn = document.getElementById('volume-btn');

        volumeSlider.addEventListener('input', (e) => {
            this.volume = e.target.value / 100;
            this.audio.volume = this.volume;
            this.updateVolumeIcon();
        });

        volumeBtn.addEventListener('click', () => {
            if (this.audio.volume > 0) {
                this.audio.volume = 0;
                volumeSlider.value = 0;
            } else {
                this.audio.volume = this.volume;
                volumeSlider.value = this.volume * 100;
            }
            this.updateVolumeIcon();
        });
    }

    loadTrack(track) {
        this.currentTrack = track;
        this.audio.src = track.url;
        this.updateTrackInfo();
        this.play();
    }

    play() {
        this.audio.play().catch(e => {
            console.error('Playback failed:', e);
        });
    }

    pause() {
        this.audio.pause();
    }

    togglePlay() {
        if (this.isPlaying) {
            this.pause();
        } else {
            this.play();
        }
    }

    playNext() {
        if (this.queue.length === 0) return;
        
        if (this.isShuffle) {
            this.currentIndex = Math.floor(Math.random() * this.queue.length);
        } else {
            this.currentIndex = (this.currentIndex + 1) % this.queue.length;
        }
        
        this.loadTrack(this.queue[this.currentIndex]);
    }

    playPrevious() {
        if (this.queue.length === 0) return;
        
        if (this.audio.currentTime > 3) {
            this.audio.currentTime = 0;
        } else {
            this.currentIndex = (this.currentIndex - 1 + this.queue.length) % this.queue.length;
            this.loadTrack(this.queue[this.currentIndex]);
        }
    }

    toggleShuffle() {
        this.isShuffle = !this.isShuffle;
        const shuffleBtn = document.getElementById('shuffle-btn');
        shuffleBtn.classList.toggle('active', this.isShuffle);
    }

    toggleRepeat() {
        const modes = ['off', 'all', 'one'];
        const currentIndex = modes.indexOf(this.repeatMode);
        this.repeatMode = modes[(currentIndex + 1) % modes.length];
        
        const repeatBtn = document.getElementById('repeat-btn');
        repeatBtn.classList.toggle('active', this.repeatMode !== 'off');
    }

    onPlay() {
        this.isPlaying = true;
        const playBtn = document.getElementById('play-btn');
        playBtn.innerHTML = `
            <svg viewBox="0 0 24 24" fill="currentColor">
                <rect x="6" y="4" width="4" height="16"/>
                <rect x="14" y="4" width="4" height="16"/>
            </svg>
        `;
        
        // Update media session
        if ('mediaSession' in navigator) {
            navigator.mediaSession.playbackState = 'playing';
        }
    }

    onPause() {
        this.isPlaying = false;
        const playBtn = document.getElementById('play-btn');
        playBtn.innerHTML = `
            <svg viewBox="0 0 24 24" fill="currentColor">
                <polygon points="5 3 19 12 5 21 5 3"/>
            </svg>
        `;
        
        // Update media session
        if ('mediaSession' in navigator) {
            navigator.mediaSession.playbackState = 'paused';
        }
    }

    onEnded() {
        if (this.repeatMode === 'one') {
            this.audio.currentTime = 0;
            this.play();
        } else if (this.repeatMode === 'all' || this.currentIndex < this.queue.length - 1) {
            this.playNext();
        } else {
            this.pause();
        }
    }

    onTimeUpdate() {
        const currentTime = this.audio.currentTime;
        const duration = this.audio.duration;
        
        if (!isNaN(duration)) {
            const percent = (currentTime / duration) * 100;
            document.querySelector('.progress-fill').style.width = `${percent}%`;
            document.querySelector('.progress-slider').value = percent;
            
            document.querySelector('.time-current').textContent = this.formatTime(currentTime);
        }
    }

    onLoadedMetadata() {
        const duration = this.audio.duration;
        document.querySelector('.time-total').textContent = this.formatTime(duration);
        
        // Setup media session
        this.setupMediaSession();
    }

    onError(e) {
        console.error('Audio error:', e);
        alert('Failed to play audio file');
    }

    updateTrackInfo() {
        if (!this.currentTrack) return;
        
        document.querySelector('.track-title').textContent = this.currentTrack.title;
        document.querySelector('.track-artist').textContent = this.currentTrack.artist;
    }

    updateVolumeIcon() {
        const volumeBtn = document.getElementById('volume-btn');
        const volume = this.audio.volume;
        
        let icon;
        if (volume === 0) {
            icon = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/>
                    <line x1="23" y1="9" x2="17" y2="15"/>
                    <line x1="17" y1="9" x2="23" y2="15"/>
                </svg>
            `;
        } else if (volume < 0.5) {
            icon = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/>
                    <path d="M15.54 8.46a5 5 0 0 1 0 7.07"/>
                </svg>
            `;
        } else {
            icon = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/>
                    <path d="M19.07 4.93a10 10 0 0 1 0 14.14"/>
                    <path d="M15.54 8.46a5 5 0 0 1 0 7.07"/>
                </svg>
            `;
        }
        
        volumeBtn.innerHTML = icon;
    }

    setupMediaSession() {
        if (!('mediaSession' in navigator)) return;
        
        navigator.mediaSession.metadata = new MediaMetadata({
            title: this.currentTrack.title,
            artist: this.currentTrack.artist,
            album: 'LUMENA',
            artwork: [
                { src: 'icons/icon-192.png', sizes: '192x192', type: 'image/png' },
                { src: 'icons/icon-512.png', sizes: '512x512', type: 'image/png' }
            ]
        });

        navigator.mediaSession.setActionHandler('play', () => this.play());
        navigator.mediaSession.setActionHandler('pause', () => this.pause());
        navigator.mediaSession.setActionHandler('previoustrack', () => this.playPrevious());
        navigator.mediaSession.setActionHandler('nexttrack', () => this.playNext());
    }

    formatTime(seconds) {
        if (isNaN(seconds)) return '0:00';
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    setQueue(tracks, startIndex = 0) {
        this.queue = tracks;
        this.currentIndex = startIndex;
    }
}

// Initialize player when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.player = new AudioPlayer();
});
