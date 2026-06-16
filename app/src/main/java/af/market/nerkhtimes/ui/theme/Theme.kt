package af.market.nerkhtimes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    secondary = MintSoft,
    onSecondary = InkBlack,
    tertiary = MintSoft,
    onTertiary = InkBlack,
    background = OffWhiteBg,
    onBackground = InkBlack,
    surface = Color.White,
    onSurface = InkBlack,
    outline = MutedGray,
    error = Color(0xFFC62828),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = TealLight,
    onPrimary = Color.Black,
    secondary = TealPrimary,
    onSecondary = Color.White,
    tertiary = TealLight,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    outline = DarkOutline,
    error = Color(0xFFEF9A9A),
    onError = Color.Black
)

@Composable
fun NerkhTimesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
