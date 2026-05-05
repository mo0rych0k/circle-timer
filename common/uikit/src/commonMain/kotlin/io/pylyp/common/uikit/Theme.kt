package com.circle.timer.common.uikit

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Application color tokens (weather / observation UI).
 * Use with [MaterialTheme.colorScheme] for Material roles; use direct accents for sliders and column headers.
 */
public object AppColors {
    /** Brand blue — primary buttons, FAB, active nav. */
    public val primary: Color = Color(0xFF2563EB)

    /** Screen background (light gray). */
    public val background: Color = Color(0xFFF8FAFC)

    /** Cards, sheets, nav bar. */
    public val surface: Color = Color(0xFFFFFFFF)

    /** “My observations” / user-reported values. */
    public val userDataAccent: Color = Color(0xFF3B82F6)

    /** Open-Meteo / API column. */
    public val apiDataAccent: Color = Color(0xFF4ADE80)

    /** Errors, destructive actions, “large difference” alerts. */
    public val error: Color = Color(0xFFEF4444)

    /** Headings and primary text. */
    public val onSurface: Color = Color(0xFF1E293B)

    /** Labels and secondary text. */
    public val onSurfaceVariant: Color = Color(0xFF64748B)

    /** Humidity slider track / thumb. */
    public val sliderHumidity: Color = Color(0xFF3B82F6)

    /** Atmospheric pressure slider. */
    public val sliderPressure: Color = Color(0xFFA855F7)

    /** Temperature gradient (warm end). */
    public val sliderTemperatureWarm: Color = Color(0xFFEF4444)

    /** Temperature gradient (cool / light end). */
    public val sliderTemperatureCool: Color = Color(0xFFFFE4E6)
}

private val AppFontFamily: FontFamily = FontFamily.SansSerif

/**
 * Material 3 typography tuned for headings, large metrics, and muted labels.
 * Prefer [MaterialTheme.typography] in UI; colors come from [MaterialTheme.colorScheme].
 */
public val AppTypography: Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.01.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.01.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.02.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.03.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.03.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.03.sp,
    ),
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = AppColors.userDataAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = AppColors.apiDataAccent,
    onTertiary = Color(0xFF14532D),
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF14532D),
    error = AppColors.error,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    background = AppColors.background,
    onBackground = AppColors.onSurface,
    surface = AppColors.surface,
    onSurface = AppColors.onSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = AppColors.onSurfaceVariant,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = AppColors.userDataAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFBFDBFE),
    tertiary = AppColors.apiDataAccent,
    onTertiary = Color(0xFF052E16),
    tertiaryContainer = Color(0xFF166534),
    onTertiaryContainer = Color(0xFFD1FAE5),
    error = AppColors.error,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF475569),
)

@Composable
public fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
