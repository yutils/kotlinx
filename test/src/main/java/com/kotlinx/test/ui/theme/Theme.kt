package com.kotlinx.test.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColors(
    primary = Accent,
    primaryVariant = AccentDark,
    secondary = AccentDark,
    background = Paper,
    surface = PaperCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Ink,
    onSurface = Ink,
)

@Composable
fun KotlinxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Light,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
