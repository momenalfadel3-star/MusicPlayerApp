package com.alkhufash.music.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// === الخفاش - ألوان التطبيق الرئيسية ===
val BatPurple = Color(0xFF7C3AED)        // بنفسجي نيون
val BatPurpleLight = Color(0xFFA855F7)   // بنفسجي فاتح
val BatPink = Color(0xFFEC4899)          // وردي نيون
val BatPinkLight = Color(0xFFF472B6)     // وردي فاتح
val BatOrange = Color(0xFFF97316)        // برتقالي نيون
val BatCyan = Color(0xFF06B6D4)          // سماوي نيون

// === ألوان الخلفية الداكنة ===
val DarkBackground = Color(0xFF0A0A14)   // أسود عميق
val DarkSurface = Color(0xFF12121E)      // سطح داكن
val DarkCard = Color(0xFF1C1C2E)         // بطاقة داكنة
val DarkElevated = Color(0xFF252538)     // مرتفع داكن

// === ألوان قديمة للتوافق ===
val MusicPurple = BatPurple
val MusicPink = BatPink
val MusicOrange = BatOrange

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

private val DarkColorScheme = darkColorScheme(
    primary = BatPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B0764),
    onPrimaryContainer = Color(0xFFE9D5FF),
    secondary = BatPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF831843),
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = BatOrange,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color(0xFFF1F0FF),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F0FF),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFBBB8D0),
    outline = Color(0xFF4A4860),
    error = Color(0xFFFF6B6B),
    onError = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = BatPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = BatPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = BatOrange,
    onTertiary = Color.White,
    background = Color(0xFFF8F4FF),
    onBackground = Color(0xFF1A0533),
    surface = Color.White,
    onSurface = Color(0xFF1A0533),
    surfaceVariant = Color(0xFFF3E8FF),
    onSurfaceVariant = Color(0xFF4A3F6B),
    outline = Color(0xFF9C8FC0),
)

@Composable
fun MusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
