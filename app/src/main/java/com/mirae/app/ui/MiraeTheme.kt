package com.mirae.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MiraePurple = Color(0xFF6E42F5)
val MiraeViolet = Color(0xFF9B4DFF)
val MiraeCyan = Color(0xFF2FE5D0)
val MiraePink = Color(0xFFFF5BBE)
val MiraeLavender = Color(0xFFF1ECFF)
val MiraeInk = Color(0xFF171429)

private val LightColors = lightColorScheme(
    primary = MiraePurple,
    onPrimary = Color.White,
    secondary = MiraeCyan,
    onSecondary = MiraeInk,
    tertiary = MiraePink,
    background = Color(0xFFF8F6FC),
    surface = Color.White,
    surfaceVariant = MiraeLavender,
    onSurface = MiraeInk,
    onBackground = MiraeInk
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB59CFF),
    secondary = Color(0xFF62F0DF),
    tertiary = Color(0xFFFF88CE),
    background = Color(0xFF0E0B18),
    surface = Color(0xFF171326),
    surfaceVariant = Color(0xFF28203B)
)

@Composable
fun MiraeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        shapes = Shapes(
            extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10),
            small = androidx.compose.foundation.shape.RoundedCornerShape(14),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(20),
            large = androidx.compose.foundation.shape.RoundedCornerShape(28),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36)
        ),
        content = content
    )
}
