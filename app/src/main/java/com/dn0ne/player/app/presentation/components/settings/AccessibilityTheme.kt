package com.dn0ne.player.app.presentation.components.settings

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dn0ne.player.core.data.Settings
import com.kmpalette.DominantColorState

// Large text scale factor
const val LARGE_TEXT_SCALE = 1.25f
const val EXTRA_LARGE_TEXT_SCALE = 1.5f

// Custom LocalAccessibilitySettings for text scaling
val LocalTextScale = compositionLocalOf { 1.0f }

// High contrast colors
private val HighContrastLightColors = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = Color.Black,
    onSecondary = Color.White,
    tertiary = Color.Black,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color.Black,
    outline = Color.Black
)

private val HighContrastDarkColors = darkColorScheme(
    primary = Color.Yellow,
    onPrimary = Color.Black,
    secondary = Color.Yellow,
    onSecondary = Color.Black,
    tertiary = Color.Yellow,
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = Color.Yellow,
    surface = Color.Black,
    onSurface = Color.Yellow,
    surfaceVariant = Color(0xFF333333),
    onSurfaceVariant = Color.Yellow,
    outline = Color.Yellow
)

@Composable
fun AccessibilityTheme(
    settings: Settings,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dominantColorState: DominantColorState<Color>? = null,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    
    // Collect accessibility settings
    val largeText by settings.largeText.collectAsState()
    val highContrast by settings.highContrast.collectAsState()
    
    // Calculate text scale
    val textScale = if (largeText) LARGE_TEXT_SCALE else 1.0f
    
    // Determine if we should use high contrast
    val useHighContrast = highContrast
    
    // Create typography with scaling
    val scaledTypography = remember(textScale) {
        createScaledTypography(textScale)
    }
    
    // Get color scheme
    val colorScheme = when {
        useHighContrast && darkTheme -> HighContrastDarkColors
        useHighContrast -> HighContrastLightColors
        dominantColorState != null -> {
            val dominantColor = dominantColorState.color
            if (darkTheme) {
                darkColorScheme(
                    primary = dominantColor,
                    onPrimary = Color.Black,
                    secondary = dominantColor.copy(alpha = 0.7f),
                    onSecondary = Color.Black,
                    tertiary = dominantColor.copy(alpha = 0.5f),
                    onTertiary = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = dominantColor,
                    onPrimary = Color.White,
                    secondary = dominantColor.copy(alpha = 0.7f),
                    onSecondary = Color.White,
                    tertiary = dominantColor.copy(alpha = 0.5f),
                    onTertiary = Color.White
                )
            }
        }
        else -> if (darkTheme) darkColorScheme() else lightColorScheme()
    }
    
    if (useHighContrast) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Black.toArgb()
            window.navigationBarColor = Color.Black.toArgb()
        }
    }
    
    CompositionLocalProvider(LocalTextScale provides textScale) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            content = content
        )
    }
}

private fun createScaledTypography(scale: Float): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (57 * scale).sp,
            lineHeight = (64 * scale).sp,
            letterSpacing = (-0.25 * scale).sp
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (45 * scale).sp,
            lineHeight = (52 * scale).sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (36 * scale).sp,
            lineHeight = (44 * scale).sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (32 * scale).sp,
            lineHeight = (40 * scale).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (28 * scale).sp,
            lineHeight = (36 * scale).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (24 * scale).sp,
            lineHeight = (32 * scale).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (22 * scale).sp,
            lineHeight = (28 * scale).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale).sp,
            letterSpacing = (0.15 * scale).sp
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp,
            letterSpacing = (0.1 * scale).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale).sp,
            letterSpacing = (0.5 * scale).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp,
            letterSpacing = (0.25 * scale).sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * scale).sp,
            lineHeight = (16 * scale).sp,
            letterSpacing = (0.4 * scale).sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp,
            letterSpacing = (0.1 * scale).sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * scale).sp,
            lineHeight = (16 * scale).sp,
            letterSpacing = (0.5 * scale).sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * scale).sp,
            lineHeight = (16 * scale).sp,
            letterSpacing = (0.5 * scale).sp
        )
    )
}

// Helper function to get scaled typography in composables
@Composable
fun rememberScaledTypography(): Typography {
    val textScale = LocalTextScale.current
    return remember(textScale) {
        createScaledTypography(textScale)
    }
}
