package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.text.font.FontWeight
import android.util.Log
import androidx.compose.material.icons.rounded.Info
import com.dn0ne.player.R
import com.dn0ne.player.app.presentation.components.topbar.ColumnWithCollapsibleTopBar
import com.dn0ne.player.core.presentation.AppDetails

@Composable
private fun FeatureItem(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AboutPage(
    onBackClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit = {},
    onPlayStoreComplianceClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    android.util.Log.d("AboutPage", "AboutPage composable started")
    val context = LocalContext.current
    android.util.Log.d("AboutPage", "Context obtained: ${context != null}")
    if (context == null) {
        android.util.Log.e("AboutPage", "Context is null, cannot render AboutPage")
        return
    }
    var collapseFraction by remember {
        mutableFloatStateOf(0f)
    }
    var showFeatures by remember {
        mutableStateOf(false)
    }
    var showOpenSourceLicensesDialog by remember {
        mutableStateOf(false)
    }
    var showTermsOfServiceDialog by remember {
        mutableStateOf(false)
    }
    var showContactInfoDialog by remember {
        mutableStateOf(false)
    }
    var showAppPermissionsDialog by remember {
        mutableStateOf(false)
    }
    // Trigger animation after composition
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showFeatures = true
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
                text = context.resources.getString(R.string.about_app),
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
        android.util.Log.d("AboutPage", "Calling AppDetails")
        AppDetails(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Features Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = context.resources.getString(R.string.features),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                FeatureItem(
                    title = context.resources.getString(R.string.feature_lyrics),
                    description = context.resources.getString(R.string.feature_lyrics_desc),
                    icon = Icons.Rounded.Star
                )

                FeatureItem(
                    title = context.resources.getString(R.string.feature_metadata),
                    description = context.resources.getString(R.string.feature_metadata_desc),
                    icon = Icons.Rounded.Info
                )

                FeatureItem(
                    title = context.resources.getString(R.string.feature_playback),
                    description = context.resources.getString(R.string.feature_playback_desc),
                    icon = Icons.Rounded.Favorite
                )

                FeatureItem(
                    title = context.resources.getString(R.string.feature_search),
                    description = context.resources.getString(R.string.feature_search_desc),
                    icon = Icons.Rounded.QuestionAnswer
                )

                FeatureItem(
                    title = context.resources.getString(R.string.feature_playlists),
                    description = context.resources.getString(R.string.feature_playlists_desc),
                    icon = Icons.Rounded.Star
                )
            }
        }
        

        Spacer(modifier = Modifier.height(16.dp))

        // Cultural Heritage Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = context.resources.getString(R.string.cultural_heritage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = context.resources.getString(R.string.cultural_heritage_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val uriHandler = LocalUriHandler.current
        android.util.Log.d("AboutPage", "UriHandler obtained: ${uriHandler != null}")
        if (uriHandler == null) {
            android.util.Log.e("AboutPage", "UriHandler is null, links will not work")
        }
        // Use a default icon - R.drawable.source_control appears to be invalid/missing
        val repoIcon = Icons.Rounded.Info
        SettingsGroup(
            items = listOf(
                SettingsItem(
                    title = context.resources.getString(R.string.repo),
                    supportingText = context.resources.getString(R.string.repo_explain),
                    icon = repoIcon,
                    onClick = {
                        try {
                            uriHandler?.openUri(context.resources.getString(R.string.repo_url))
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to open repository URL", e)
                        }
                    }
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.feedback),
                    supportingText = context.resources.getString(R.string.feedback_explain),
                    icon = Icons.Rounded.QuestionAnswer,
                    onClick = {
                        try {
                            uriHandler?.openUri(context.resources.getString(R.string.feedback_url))
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to open feedback URL", e)
                        }
                    }
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.donate),
                    supportingText = context.resources.getString(R.string.donate_explain),
                    icon = ImageVector.vectorResource(R.drawable.ic_buymeacoffee),
                    onClick = {
                        try {
                            uriHandler?.openUri(context.resources.getString(R.string.donate_url))
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to open donate URL", e)
                        }
                    }
                )
            )
        )
        
        SettingsGroup(
            items = listOf(
                SettingsItem(
                    title = context.resources.getString(R.string.privacy_policy),
                    supportingText = context.resources.getString(R.string.privacy_policy_explain),
                    icon = Icons.Rounded.PrivacyTip,
                    onClick = onPrivacyPolicyClick
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.open_source_licenses),
                    supportingText = context.resources.getString(R.string.open_source_licenses_explain),
                    icon = Icons.Rounded.Info,
                    onClick = {
                        try {
                            showOpenSourceLicensesDialog = true
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to show open source licenses dialog", e)
                        }
                    }
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.terms_of_service),
                    supportingText = context.resources.getString(R.string.terms_of_service_explain),
                    icon = Icons.Rounded.Info,
                    onClick = {
                        try {
                            showTermsOfServiceDialog = true
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to show terms of service dialog", e)
                        }
                    }
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.contact_info),
                    supportingText = context.resources.getString(R.string.contact_info_explain),
                    icon = Icons.Rounded.Info,
                    onClick = {
                        try {
                            showContactInfoDialog = true
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to show contact info dialog", e)
                        }
                    }
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.app_permissions),
                    supportingText = context.resources.getString(R.string.app_permissions_explain),
                    icon = Icons.Rounded.Info,
                    onClick = {
                        try {
                            showAppPermissionsDialog = true
                        } catch (e: Exception) {
                            android.util.Log.e("AboutPage", "Failed to show app permissions dialog", e)
                        }
                    }
                ),
                SettingsItem(
                    title = context.resources.getString(R.string.changelog),
                    supportingText = context.resources.getString(R.string.changelog_explain),
                    icon = Icons.Rounded.Star,
                    onClick = {
                        // TODO: Open changelog screen or dialog
                        android.util.Log.d("AboutPage", "Changelog clicked")
                    }
                )
            )
        )

        // Compliance Dialogs
        if (showOpenSourceLicensesDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showOpenSourceLicensesDialog = false },
                title = { Text(context.resources.getString(R.string.open_source_licenses)) },
                text = {
                    Text(
                        text = context.resources.getString(R.string.open_source_licenses_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showOpenSourceLicensesDialog = false }
                    ) {
                        Text(context.resources.getString(R.string.got_it))
                    }
                }
            )
        }

        if (showTermsOfServiceDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showTermsOfServiceDialog = false },
                title = { Text(context.resources.getString(R.string.terms_of_service)) },
                text = {
                    Text(
                        text = context.resources.getString(R.string.terms_of_service_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showTermsOfServiceDialog = false }
                    ) {
                        Text(context.resources.getString(R.string.got_it))
                    }
                }
            )
        }

        if (showContactInfoDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showContactInfoDialog = false },
                title = { Text(context.resources.getString(R.string.contact_info)) },
                text = {
                    Text(
                        text = context.resources.getString(R.string.contact_info_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showContactInfoDialog = false }
                    ) {
                        Text(context.resources.getString(R.string.got_it))
                    }
                }
            )
        }

        if (showAppPermissionsDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showAppPermissionsDialog = false },
                title = { Text(context.resources.getString(R.string.app_permissions)) },
                text = {
                    Text(
                        text = context.resources.getString(R.string.app_permissions_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showAppPermissionsDialog = false }
                    ) {
                        Text(context.resources.getString(R.string.got_it))
                    }
                }
            )
        }
    }
}