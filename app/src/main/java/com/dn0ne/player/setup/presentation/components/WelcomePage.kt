package com.dn0ne.player.setup.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.dn0ne.player.R

@Composable
fun WelcomePage(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo - using the animated logo vector
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App name
            Text(
                text = "Lumena",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onGetStartedClick
            ) {
                Text(text = stringResource(R.string.get_started))
            }
        }
    }
}
