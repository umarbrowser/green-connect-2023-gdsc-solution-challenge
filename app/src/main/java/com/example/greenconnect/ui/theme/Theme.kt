package com.example.greenconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Blue40,
    onPrimary = Blue80,
    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
    error = Red80,
    onError = Red20,
    background = Grey10,
    surface = Grey10,
   // onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = Blue40,
    onPrimary = Color.White,
    secondary = DarkBlue40,
    onSecondary = Color.White,
    error = Red40,
    onError = Color.White,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun GreenConnectTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
