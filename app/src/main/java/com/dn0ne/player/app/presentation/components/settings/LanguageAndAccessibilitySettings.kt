package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Visibility
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R
import com.dn0ne.player.setup.presentation.components.supportedLanguages
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageAndAccessibilitySettings(
    settings: com.dn0ne.player.core.data.Settings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(settings.language) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
                        Text(
                            text = stringResource(R.string.next),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showLanguagePicker = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguagePicker = true }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.accessibility),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.screen_reader),
                    supportingText = stringResource(R.string.screen_reader_explain),
                    checked = settings.screenReader,
                    onCheckedChange = { settings.screenReader = it }
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.large_text),
                    supportingText = stringResource(R.string.large_text_explain),
                    checked = settings.largeText,
                    onCheckedChange = { settings.largeText = it }
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.high_contrast),
                    supportingText = stringResource(R.string.high_contrast_explain),
                    checked = settings.highContrast,
                    onCheckedChange = { settings.highContrast = it }
                )
            }

            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.voice_control),
                    supportingText = stringResource(R.string.voice_control_explain),
                    checked = settings.voiceControl,
                    onCheckedChange = { settings.voiceControl = it }
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
