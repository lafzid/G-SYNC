package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = BlueOnPrimaryDark,
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = BlueOnPrimaryContainerDark,
    secondary = LavenderSecondaryDark,
    onSecondary = LavenderOnSecondaryDark,
    secondaryContainer = LavenderSecondaryContainerDark,
    onSecondaryContainer = LavenderOnSecondaryContainerDark,
    tertiary = WarmAmberTertiaryDark,
    onTertiary = WarmAmberOnTertiaryDark,
    tertiaryContainer = WarmAmberTertiaryContainerDark,
    onTertiaryContainer = WarmAmberOnTertiaryContainerDark,
    background = PolishBackgroundDark,
    onBackground = PolishOnBackgroundDark,
    surface = PolishSurfaceDark,
    onSurface = PolishOnSurfaceDark,
    surfaceVariant = PolishSurfaceVariantDark,
    onSurfaceVariant = PolishOnSurfaceVariantDark,
    outline = PolishOutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BluePrimary,
    onPrimary = BlueOnPrimary,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BlueOnPrimaryContainer,
    secondary = LavenderSecondary,
    onSecondary = LavenderOnSecondary,
    secondaryContainer = LavenderSecondaryContainer,
    onSecondaryContainer = LavenderOnSecondaryContainer,
    tertiary = WarmAmberTertiary,
    onTertiary = WarmAmberOnTertiary,
    tertiaryContainer = WarmAmberTertiaryContainer,
    onTertiaryContainer = WarmAmberOnTertiaryContainer,
    background = PolishBackgroundLight,
    onBackground = PolishOnBackgroundLight,
    surface = PolishSurfaceLight,
    onSurface = PolishOnSurfaceLight,
    surfaceVariant = PolishSurfaceVariantLight,
    onSurfaceVariant = PolishOnSurfaceVariantLight,
    outline = PolishOutlineLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our civic green/amber RW theme for distinct identity
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
