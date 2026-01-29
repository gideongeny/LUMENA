package com.dn0ne.player.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R
import com.dn0ne.player.core.util.getAppVersionName
import android.util.Log

@Composable
fun AppDetails(modifier: Modifier = Modifier) {
    android.util.Log.d("AppDetails", "AppDetails composable started")
    val context = LocalContext.current
    android.util.Log.d("AppDetails", "Context obtained: ${context != null}")
    val shouldShowIcon = remember {
        try {
            val iconId = context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
            android.util.Log.d("AppDetails", "Icon ID: $iconId")
            iconId != 0
        } catch (e: Exception) {
            Log.e("AppDetails", "Failed to load launcher icon: ${e.message}", e)
            false
        }
    }
    android.util.Log.d("AppDetails", "Should show icon: $shouldShowIcon")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (shouldShowIcon) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground_new),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
        }

        Text(
            text = context.resources.getString(R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        val versionName = try {
            context.getAppVersionName()
        } catch (e: Exception) {
            Log.e("AppDetails", "Failed to get app version name: ${e.message}", e)
            "Unknown"
        }
        android.util.Log.d("AppDetails", "Version name: $versionName")
        Text(
            text = versionName,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = context.resources.getString(R.string.developer),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = context.resources.getString(R.string.developer_name),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = context.resources.getString(R.string.app_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}