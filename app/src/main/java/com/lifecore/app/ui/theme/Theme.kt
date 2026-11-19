package com.lifecore.app.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = LifeCoreColors.Primary, onPrimary = Color.White,
    secondary = LifeCoreColors.Secondary,
    background = LifeCoreColors.LightBackground, onBackground = LifeCoreColors.LightText,
    surface = LifeCoreColors.LightSurface, onSurface = LifeCoreColors.LightText,
    surfaceVariant = LifeCoreColors.LightSurfaceVariant,
    onSurfaceVariant = LifeCoreColors.LightMutedText,
    outline = LifeCoreColors.OutlineLight, error = LifeCoreColors.Error
)
private val DarkColors = darkColorScheme(
    primary = LifeCoreColors.Primary, onPrimary = Color.White,
    secondary = LifeCoreColors.Secondary,
    background = LifeCoreColors.DarkBackground, onBackground = LifeCoreColors.DarkText,
    surface = LifeCoreColors.DarkSurface, onSurface = LifeCoreColors.DarkText,
    surfaceVariant = LifeCoreColors.DarkSurfaceVariant,
    onSurfaceVariant = LifeCoreColors.DarkMutedText,
    outline = LifeCoreColors.OutlineDark, error = LifeCoreColors.Error
)
private val LifeCoreShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
@Composable
fun LifeCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = LifeCoreShapes,
        content = content
    )
}
