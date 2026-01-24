package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dn0ne.player.R
import com.dn0ne.player.app.presentation.components.topbar.ColumnWithCollapsibleTopBar

@Composable
fun PlayStoreCompliancePage(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var collapseFraction by remember {
        mutableFloatStateOf(0f)
    }

    ColumnWithCollapsibleTopBar(
        topBarContent = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = context.resources.getString(R.string.back)
                )
            }

            Text(
                text = "Play Store Compliance",
                fontSize = lerp(
                    MaterialTheme.typography.titleLarge.fontSize,
                    MaterialTheme.typography.displaySmall.fontSize,
                    collapseFraction
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            )
        },
        collapseFraction = {
            collapseFraction = it
        },
        contentPadding = PaddingValues(horizontal = 24.dp),
        contentHorizontalAlignment = Alignment.CenterHorizontally,
        contentVerticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val scrollState = rememberScrollState()
        val complianceText = """
            Play Store Compliance Information
            
            Last Updated: December 2024
            
            Lumena Music Player complies with Google Play Store policies and guidelines. This document outlines our compliance with key requirements.
            
            1. Content Policy Compliance
            • Lumena is a music player application that plays local audio files stored on the user's device.
            • The app does not host, stream, or distribute copyrighted content.
            • Users are responsible for ensuring they have the rights to play the music files they use with this app.
            
            2. Data Safety & Privacy
            • Local Storage Only: All music files, playlists, and user preferences are stored locally on the device.
            • No Personal Data Collection: We do not collect, transmit, or store personal information.
            • No Account Required: Users can use the app without creating an account.
            • Third-Party Services: Limited use of third-party APIs (YouTube, MusicBrainz, LRCLIB) for optional features like lyrics and metadata lookup.
            
            3. Permissions Declaration
            The app requests the following permissions with clear justification:
            
            • READ_MEDIA_AUDIO / READ_EXTERNAL_STORAGE
              Purpose: Required to scan and play music files stored on the device.
              Data Access: Only accesses audio files in user-designated folders.
              
            • WRITE_EXTERNAL_STORAGE (Android 12 and below)
              Purpose: Allows saving metadata edits to audio files.
              Data Access: Only modifies files that the user explicitly chooses to edit.
              
            • INTERNET
              Purpose: Required for optional online features:
                - Fetching lyrics from LRCLIB
                - Fetching metadata from MusicBrainz
                - YouTube search functionality (if enabled)
              Data Transmission: Only sends track metadata (title, artist) for lyrics/metadata lookup. No personal information is transmitted.
            
            4. Target Audience
            • Age Rating: Suitable for all ages
            • Content: No explicit or inappropriate content
            • The app itself does not contain user-generated content that requires moderation
            
            5. Monetization
            • Lumena is free and open-source software
            • No in-app purchases
            • No advertisements
            • No data collection for advertising purposes
            
            6. Security & Safety
            • All data processing occurs locally on the device
            • No network communication except for optional features (lyrics, metadata)
            • No background data collection
            • No tracking or analytics
            
            7. Intellectual Property
            • Lumena respects intellectual property rights
            • The app does not facilitate copyright infringement
            • Users are responsible for ensuring they have rights to play their music files
            
            8. User Data Rights
            • Users have full control over their data
            • All data can be deleted by uninstalling the app
            • No data is stored on external servers
            • Users can export playlists in standard formats (M3U)
            
            9. Accessibility
            • Lumena follows Material Design guidelines for accessibility
            • Supports system font scaling
            • Uses standard Android accessibility features
            
            10. Technical Compliance
            • Follows Android App Bundle format requirements
            • Complies with target SDK requirements
            • Uses standard Android APIs
            • No prohibited APIs or practices
            
            11. Updates & Maintenance
            • Regular updates to maintain compatibility
            • Security updates as needed
            • Bug fixes and feature improvements
            
            For questions or concerns about Play Store compliance, please contact us through the app's feedback option.
        """.trimIndent()
        
        Text(
            text = complianceText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp)
        )
    }
}



