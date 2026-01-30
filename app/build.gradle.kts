import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.realm)
}

val splitApks = !project.hasProperty("noSplits")
val abiFilterList = (properties["ABI_FILTERS"] as? String)?.split(';').orEmpty()
val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.dn0ne.player"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dn0ne.lumena"
        minSdk = 24
        targetSdk = 36
        versionCode = 130
        versionName = "1.3.0"

        if (splitApks) {
            splits {
                abi {
                    isEnable = true
                    reset()
                    include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    isUniversalApk = true
                }
            }
        } else {
            ndk {
                abiFilters.addAll(abiFilterList)
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "YOUTUBE_API_KEY", "\"AIzaSyDbTHAbBxPWdvKWjbWG_xcd8-09t3w-CCI\"")
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"019e0b38fdbf4846a6af3860d1aa03fa\"")
        buildConfigField("String", "SPOTIFY_CLIENT_SECRET", "\"9ff2c719cb124a54b3ac353e71676554\"")
    }

    androidComponents {
        onVariants { variant ->
            variant.outputs.forEach { output ->
                val name =
                    if (splitApks) {
                        output.filters
                            .find {
                                it.filterType ==
                                        com.android.build.api.variant.FilterConfiguration.FilterType.ABI
                            }
                            ?.identifier
                    } else {
                        abiFilterList.firstOrNull()
                    }

                val baseAbiCode = abiCodes[name]

                if (baseAbiCode != null) {
                    output.versionCode.set(baseAbiCode + (output.versionCode.getOrElse(0)))
                }
            }
        }
    }

signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storeFile = if (keystoreProperties.containsKey("storeFile")) {
                file(keystoreProperties["storeFile"] as String)
            } else {
                null
            }
            storePassword = keystoreProperties["storePassword"] as String?
        }
    }

    buildTypes {
        debug {
            if (keystorePropertiesFile.exists() && keystoreProperties.containsKey("storeFile")) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            // Disable splits for bundle builds
            splits {
                abi {
                    isEnable = false
                }
            }
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "lumena-${defaultConfig.versionName}-${name}.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
        }
        jniLibs {
            excludes += "**/libpython.zip.so"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

tasks.withType<com.android.build.gradle.internal.tasks.CompileArtProfileTask> {
    enabled = false
}

configurations.all {
    resolutionStrategy {
        force("androidx.activity:activity:1.11.0")
        force("androidx.activity:activity-ktx:1.11.0")
        force("androidx.activity:activity-compose:1.11.0")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)
    implementation("androidx.leanback:leanback:1.0.0")
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil)
    implementation("androidx.palette:palette:1.0.0")
    implementation(libs.kmpalette.core)
    implementation(libs.materialkolor)
    implementation(libs.jaudiotagger)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.realm.library.base)
    implementation(libs.reorderable)
    implementation(libs.scrollbars)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.25.0")
    implementation("com.google.api-client:google-api-client-android:2.6.0")
    implementation("com.google.http-client:google-http-client-jackson2:1.43.3")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20231011-2.0.0")
    implementation("com.spotify.android:auth:1.2.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
}