package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dn0ne.player.R
import com.dn0ne.player.app.data.online.OpenAIApiKeyManager
import com.dn0ne.player.app.presentation.components.topbar.ColumnWithCollapsibleTopBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OpenAISettings(
    apiKeyManager: OpenAIApiKeyManager,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var collapseFraction by remember {
        mutableFloatStateOf(0f)
    }
    
    var apiKey by remember {
        mutableStateOf(apiKeyManager.getApiKey() ?: "")
    }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    
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
                text = "OpenAI API Key",
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter your OpenAI API key to enable AI-powered lyrics translation. Your key is stored securely using Android's EncryptedSharedPreferences.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("OpenAI API Key") },
                placeholder = { Text("sk-...") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        scope.launch {
                            if (apiKey.isNotBlank()) {
                                apiKeyManager.saveApiKey(apiKey)
                            }
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            if (apiKey.isNotBlank()) {
                                apiKeyManager.saveApiKey(apiKey)
                            }
                        }
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                
                TextButton(
                    onClick = {
                        apiKey = ""
                        scope.launch {
                            apiKeyManager.clearApiKey()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }
            
            if (apiKeyManager.hasApiKey()) {
                Text(
                    text = "✓ API key is saved",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

