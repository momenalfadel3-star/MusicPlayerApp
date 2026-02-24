package com.alkhufash.music.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * تعريف ألوان الثيمات المختلفة للتطبيق
 */
object AppColors {
    // الثيم الأساسي (الافتراضي - بنفسجي)
    val DefaultPalette = lightColorScheme(
        primary = Color(0xFF7C3AED),
        secondary = Color(0xFFEC4899),
        tertiary = Color(0xFFF97316),
        background = Color(0xFFF8F4FF),
        surface = Color.White,
        error = Color(0xFFFF4E4E)
    )
    
    // ثيم داكن (أسود/رمادي)
    val DarkPalette = darkColorScheme(
        primary = Color(0xFFA855F7),
        secondary = Color(0xFFF472B6),
        tertiary = Color(0xFFFB923C),
        background = Color(0xFF0A0A14),
        surface = Color(0xFF12121E),
        error = Color(0xFFFF6B6B)
    )
    
    // ثيم الطبيعة (أخضر)
    val NaturePalette = lightColorScheme(
        primary = Color(0xFF2E7D32),
        secondary = Color(0xFF81C784),
        tertiary = Color(0xFFFFA000),
        background = Color(0xFFF1F8E9),
        surface = Color.White
    )
    
    // ثيم الليل (أزرق داكن)
    val NightPalette = darkColorScheme(
        primary = Color(0xFF1A237E),
        secondary = Color(0xFF3F51B5),
        tertiary = Color(0xFF7986CB),
        background = Color(0xFF000022),
        surface = Color(0xFF000033)
    )
    
    // ثيم غروب الشمس (برتقالي/وردي)
    val SunsetPalette = lightColorScheme(
        primary = Color(0xFFFF7043),
        secondary = Color(0xFFF06292),
        tertiary = Color(0xFFFFD54F),
        background = Color(0xFFFFF3E0),
        surface = Color.White
    )
}
