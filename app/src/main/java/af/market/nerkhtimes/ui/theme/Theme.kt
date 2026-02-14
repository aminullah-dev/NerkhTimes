package af.market.nerkhtimes.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
fun NerkhTimesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
