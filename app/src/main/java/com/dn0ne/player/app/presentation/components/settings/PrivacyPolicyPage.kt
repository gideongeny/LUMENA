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
fun PrivacyPolicyPage(
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
                text = context.resources.getString(R.string.privacy_policy),
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
        val privacyPolicyText = """
            Privacy Policy for Lumena Music Player
            
            Last Updated: December 2024
            
            1. Introduction
            Welcome to Lumena Music Player. We respect your privacy and are committed to protecting your personal data.
            
            2. Data Collection
            Lumena Music Player is designed with privacy in mind:
            
            • Local Storage Only: All your music files, playlists, and preferences are stored locally on your device. We do not collect, transmit, or store your personal data on external servers.
            
            • No Account Required: You can use Lumena without creating an account or providing any personal information.
            
            • Media Library Access: The app requires permission to access your device's media library to play your music files. This access is used solely for music playback and playlist management.
            
            3. Third-Party Services
            Lumena may use the following third-party services:
            
            • YouTube Data API: Used for online music search functionality. Your search queries may be sent to YouTube's servers.
            
            • MusicBrainz: Used for metadata lookup (artist, album, genre information). No personal data is transmitted.
            
            • LRCLIB: Used for lyrics lookup. Only track metadata (title, artist) is sent, not personal information.
            
            4. Permissions
            The app requests the following permissions:
            
            • READ_MEDIA_AUDIO / READ_EXTERNAL_STORAGE: Required to access and play your music files.
            
            • INTERNET: Required for online features like YouTube search and lyrics fetching.
            
            5. Data Security
            All data is stored locally using Android's standard storage mechanisms. We do not have access to your data.
            
            6. Children's Privacy
            Our app does not knowingly collect personal information from children under 13.
            
            7. Changes to This Policy
            We may update this privacy policy from time to time. Changes will be reflected in the app.
            
            8. Contact
            For questions about this privacy policy, please contact us through the app's feedback option.
            
            9. Compliance
            This app complies with Google Play Store policies regarding user privacy and data handling.
        """.trimIndent()
        
        Text(
            text = privacyPolicyText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp)
        )
    }
}

