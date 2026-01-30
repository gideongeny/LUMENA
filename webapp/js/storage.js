// Storage utility for managing app data
class StorageManager {
    constructor() {
        this.STORAGE_KEYS = {
            TRACKS: 'lumena_tracks',
            PLAYLISTS: 'lumena_playlists',
            SETTINGS: 'lumena_settings',
            QUEUE: 'lumena_queue',
            CURRENT_TRACK: 'lumena_current_track'
        };
    }

    // Save data to localStorage
    save(key, data) {
        try {
            const jsonData = JSON.stringify(data);
            localStorage.setItem(key, jsonData);
            return true;
        } catch (e) {
            console.error('Failed to save to storage:', e);
            return false;
        }
    }

    // Load data from localStorage
    load(key) {
        try {
            const jsonData = localStorage.getItem(key);
            return jsonData ? JSON.parse(jsonData) : null;
        } catch (e) {
            console.error('Failed to load from storage:', e);
            return null;
        }
    }

    // Remove data from localStorage
    remove(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (e) {
            console.error('Failed to remove from storage:', e);
            return false;
        }
    }

    // Clear all app data
    clearAll() {
        try {
            Object.values(this.STORAGE_KEYS).forEach(key => {
                localStorage.removeItem(key);
            });
            return true;
        } catch (e) {
            console.error('Failed to clear storage:', e);
            return false;
        }
    }

    // Get storage usage
    getStorageUsage() {
        let total = 0;
        for (let key in localStorage) {
            if (localStorage.hasOwnProperty(key)) {
                total += localStorage[key].length + key.length;
            }
        }
        return {
            used: total,
            usedKB: (total / 1024).toFixed(2),
            usedMB: (total / 1024 / 1024).toFixed(2)
        };
    }

    // Check if storage is available
    isAvailable() {
        try {
            const test = '__storage_test__';
            localStorage.setItem(test, test);
            localStorage.removeItem(test);
            return true;
        } catch (e) {
            return false;
        }
    }

    // Export data as JSON
    exportData() {
        const data = {};
        Object.entries(this.STORAGE_KEYS).forEach(([name, key]) => {
            data[name] = this.load(key);
        });
        return data;
    }

    // Import data from JSON
    importData(data) {
        try {
            Object.entries(data).forEach(([name, value]) => {
                const key = this.STORAGE_KEYS[name];
                if (key && value) {
                    this.save(key, value);
                }
            });
            return true;
        } catch (e) {
            console.error('Failed to import data:', e);
            return false;
        }
    }
}

// Initialize storage manager
window.storage = new StorageManager();
