package com.dn0ne.player.app.presentation.components.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R
import com.dn0ne.player.core.data.Settings
import com.dn0ne.player.setup.presentation.components.supportedLanguages
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsPage(
    settings: Settings,
    onBackClick: () -> Unit,
    onLanguageChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf(settings.language) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.language)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.select_language),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                val currentLang = supportedLanguages.find { it.code == selectedLanguage }
                ListItem(
                    headlineContent = {
                        Column {
                            Text(
                                text = currentLang?.nativeName ?: currentLang?.name
                                    ?: stringResource(R.string.system_default)
                            )
                            if (currentLang != null && currentLang.code.isNotEmpty()) {
                                Text(
                                    text = currentLang.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguagePicker = true }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = stringResource(R.string.language_change_requires_restart),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        if (showLanguagePicker) {
            ModalBottomSheet(
                onDismissRequest = { showLanguagePicker = false },
                sheetState = sheetState
            ) {
                LazyColumn {
                    items(supportedLanguages) { language ->
                        ListItem(
                            headlineContent = {
                                Column {
                                    Text(text = language.nativeName)
                                    if (language.code.isNotEmpty()) {
                                        Text(
                                            text = language.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                if (selectedLanguage == language.code) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = language.code
                                    settings.language = language.code
                                    showRestartDialog = true
                                    scope.launch {
                                        sheetState.hide()
                                        showLanguagePicker = false
                                    }
                                }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (showRestartDialog) {
            AlertDialog(
                onDismissRequest = { showRestartDialog = false },
                title = { Text(stringResource(R.string.restart_required)) },
                text = { Text(stringResource(R.string.restart_app_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRestartDialog = false
                            onLanguageChanged()
                            // Restart the app
                            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    ) {
                        Text(stringResource(R.string.restart_now))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartDialog = false }) {
                        Text(stringResource(R.string.later))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettingsPage(
    settings: Settings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val screenReader by settings.screenReader.collectAsState()
    val largeText by settings.largeText.collectAsState()
    val highContrast by settings.highContrast.collectAsState()
    val voiceControl by settings.voiceControl.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.accessibility)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.accessibility_supporting_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.screen_reader),
                    supportingText = stringResource(R.string.screen_reader_explain),
                    checked = screenReader,
                    onCheckedChange = { settings.updateScreenReader(it) }
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.large_text),
                    supportingText = stringResource(R.string.large_text_explain),
                    checked = largeText,
                    onCheckedChange = { settings.updateLargeText(it) }
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.high_contrast),
                    supportingText = stringResource(R.string.high_contrast_explain),
                    checked = highContrast,
                    onCheckedChange = { settings.updateHighContrast(it) }
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.voice_control),
                    supportingText = stringResource(R.string.voice_control_explain),
                    checked = voiceControl,
                    onCheckedChange = { settings.updateVoiceControl(it) }
                )
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    supportingText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Column {
                Text(text = title)
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = modifier.fillMaxWidth()
    )
}
