rootProject.name = "Lumena"
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // Removed maven { url = uri("https://repo.newpipe.net/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://github.com/gideongeny/NewPipeExtractor-Kts/raw/gh-pages/") }
    }
}

// NEW LINE: Include the cloned source project
include(":app")
// includeBuild("newpipe-extractor")