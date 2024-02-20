package github.leavesczy.matisse.internal.theme

import android.os.Parcelable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize


private val Typography = Typography()

private val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

val LightColors = lightColorScheme(
    primary = Color(0xFF03A9F4),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    secondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    surface = Color(0xFF000000),
    primaryContainer = Color(0xFF03A9F4),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF000000),
    errorContainer = Color(0xFF000000),
    onErrorContainer = Color(0xFFFFFFFF),
    inverseSurface = Color(0xFF0F0F0F),
    inverseOnSurface = Color(0xFFFFFFFF)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF03A9F4),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onSecondary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0F0F0F),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    surface = Color(0xFF000000),
    primaryContainer = Color(0xFF03A9F4),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF2B2A34),
    onSecondaryContainer = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF000000),
    inverseOnSurface = Color(0xFFFFFFFF),
    inverseSurface = Color(0xFF0F0F0F)
)

interface ThemeColorProvider : Parcelable {
    fun create(isDark: Boolean, colorScheme: ColorScheme): ColorScheme
}


@Composable
internal fun MatisseTheme(
    colorProvider: ThemeColorProvider? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            colorProvider?.create(true, DarkColors) ?: DarkColors
        } else {
            colorProvider?.create(true, LightColors) ?: LightColors
        },
        typography = Typography,
        shapes = Shapes, content = content
    )
}