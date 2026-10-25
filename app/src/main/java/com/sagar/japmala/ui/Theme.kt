@file:OptIn(ExperimentalTextApi::class)

package com.sagar.japmala.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.sagar.japmala.R

object JapColors {
    val Saffron = Color(0xFFFF9933)
    val DeepSaffron = Color(0xFFE8731A)
    val Kumkum = Color(0xFF9C2A12)
    val Maroon = Color(0xFF4A1208)
    val Gold = Color(0xFFD4A017)
    val LightGold = Color(0xFFF0C050)
    val Cream = Color(0xFFFFF8EA)
}

/** Noto Serif Devanagari (SIL OFL 1.1) – a calligraphic serif that suits mantras. */
private val Devanagari = FontFamily(
    Font(R.font.noto_serif_devanagari, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.noto_serif_devanagari, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.noto_serif_devanagari, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.noto_serif_devanagari, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

private fun Typography.withFamily(f: FontFamily) = copy(
    displayLarge = displayLarge.copy(fontFamily = f),
    displayMedium = displayMedium.copy(fontFamily = f),
    displaySmall = displaySmall.copy(fontFamily = f),
    headlineLarge = headlineLarge.copy(fontFamily = f),
    headlineMedium = headlineMedium.copy(fontFamily = f),
    headlineSmall = headlineSmall.copy(fontFamily = f),
    titleLarge = titleLarge.copy(fontFamily = f),
    titleMedium = titleMedium.copy(fontFamily = f),
    titleSmall = titleSmall.copy(fontFamily = f),
    bodyLarge = bodyLarge.copy(fontFamily = f),
    bodyMedium = bodyMedium.copy(fontFamily = f),
    bodySmall = bodySmall.copy(fontFamily = f),
    labelLarge = labelLarge.copy(fontFamily = f),
    labelMedium = labelMedium.copy(fontFamily = f),
    labelSmall = labelSmall.copy(fontFamily = f),
)

private val LightScheme = lightColorScheme(
    primary = JapColors.Kumkum,
    onPrimary = JapColors.Cream,
    primaryContainer = Color(0xFFFFDDB8),
    onPrimaryContainer = JapColors.Maroon,
    secondary = Color(0xFF9A6B00),
    onSecondary = Color.White,
    background = JapColors.Cream,
    onBackground = Color(0xFF3B1A0B),
    surface = Color(0xFFFFFCF4),
    onSurface = Color(0xFF3B1A0B),
    surfaceVariant = Color(0xFFF8E4C6),
    onSurfaceVariant = Color(0xFF6B4630),
    outline = Color(0xFFC99A3C),
    surfaceContainerLow = Color(0xFFFFF6E3),
    surfaceContainer = Color(0xFFFFF1D9),
    surfaceContainerHigh = Color(0xFFFCEAD0),
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFFFB45A),
    onPrimary = Color(0xFF3B1200),
    primaryContainer = Color(0xFF6B2410),
    onPrimaryContainer = Color(0xFFFFDDB8),
    secondary = JapColors.LightGold,
    onSecondary = Color(0xFF3B2A00),
    background = Color(0xFF1B0A05),
    onBackground = Color(0xFFFBE8D0),
    surface = Color(0xFF2A110A),
    onSurface = Color(0xFFFBE8D0),
    surfaceVariant = Color(0xFF3F1D12),
    onSurfaceVariant = Color(0xFFE2C4A6),
    outline = Color(0xFF9A6B2E),
    surfaceContainerLow = Color(0xFF24100A),
    surfaceContainer = Color(0xFF2E140C),
    surfaceContainerHigh = Color(0xFF3A1A10),
)

fun japBackground(dark: Boolean): Brush =
    if (dark) {
        Brush.verticalGradient(listOf(Color(0xFF1B0A05), Color(0xFF3B1409)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFFFFAEE), Color(0xFFFFE3BC)))
    }

@Composable
fun JapTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        typography = Typography().withFamily(Devanagari),
        content = content,
    )
}
