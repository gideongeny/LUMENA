package com.dn0ne.player.setup.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import kotlinx.coroutines.launch

data class Language(
    val code: String,
    val name: String,
    val nativeName: String
)

val supportedLanguages = listOf(
    Language("", "System Default", "System default"),
    Language("en", "English", "English"),
    Language("zh", "Chinese", "中文"),
    Language("hi", "Hindi", "हिन्दी"),
    Language("fr", "French", "Français"),
    Language("pt", "Portuguese", "Português"),
    Language("ru", "Russian", "Русский"),
    Language("bn", "Bengali", "বাংলা"),
    Language("jv", "Javanese", "Basa Jawa"),
    Language("es", "Spanish", "Español"),
    Language("ha", "Hausa", "Hausa"),
    Language("am", "Amharic", "አማርኛ"),
    Language("yo", "Yoruba", "Yorùbá"),
    Language("zu", "Zulu", "isiZulu"),
    Language("ki", "Kikuyu", "Gĩkũyũ"),
    Language("luo", "Luo", "Dholuo"),
    Language("kam", "Kamba", "Kamba"),
    Language("luy", "Luhya", "Luhya"),
    Language("kln", "Kalenjin", "Kalenjin"),
    Language("mer", "Meru", "Kimeru"),
    Language("ar", "Arabic", "العربية"),
    Language("sw", "Swahili", "Kiswahili"),
    Language("uk", "Ukrainian", "Українська")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionPage(
    onBackClick: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf("") }
    var showLanguagePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.select_language)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.language_supporting_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showLanguagePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val currentLang = supportedLanguages.find { it.code == selectedLanguage }
                Text(
                    text = currentLang?.nativeName ?: currentLang?.name ?: stringResource(R.string.system_default)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onLanguageSelected(selectedLanguage) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.next))
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
