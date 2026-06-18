package af.market.nerkhtimes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary                = TealPrimary,
    onPrimary              = Color.White,
    primaryContainer       = TealContainer,
    onPrimaryContainer     = OnTealContainer,
    secondary              = MintSoft,
    onSecondary            = InkBlack,
    secondaryContainer     = Color(0xFFCCE4E2),
    onSecondaryContainer   = InkBlack,
    tertiary               = MintSoft,
    onTertiary             = InkBlack,
    tertiaryContainer      = Color(0xFFCCE4E2),
    onTertiaryContainer    = InkBlack,
    background             = OffWhiteBg,
    onBackground           = InkBlack,
    surface                = Color.White,
    onSurface              = InkBlack,
    surfaceVariant         = Color(0xFFECEFF1),
    onSurfaceVariant       = Color(0xFF546E7A),
    outline                = MutedGray,
    outlineVariant         = Color(0xFFCFD8DC),
    error                  = Color(0xFFC62828),
    onError                = Color.White,
    errorContainer         = Color(0xFFFFCDD2),
    onErrorContainer       = Color(0xFFB71C1C),
    inverseSurface         = Color(0xFF2D2D2D),
    inverseOnSurface       = Color(0xFFF5F5F5),
)

private val DarkColors = darkColorScheme(
    primary                = TealLight,
    onPrimary              = Color.Black,
    primaryContainer       = DarkTealContainer,
    onPrimaryContainer     = OnDarkTealContainer,
    secondary              = TealPrimary,
    onSecondary            = Color.White,
    secondaryContainer     = Color(0xFF004D40),
    onSecondaryContainer   = MintSoft,
    tertiary               = TealLight,
    onTertiary             = Color.Black,
    tertiaryContainer      = Color(0xFF004D40),
    onTertiaryContainer    = MintSoft,
    background             = DarkBg,
    onBackground           = DarkOnSurface,
    surface                = DarkSurface,
    onSurface              = DarkOnSurface,
    surfaceVariant         = DarkOutline,
    onSurfaceVariant       = Color(0xFFB0BEC5),
    outline                = Color(0xFF455A64),
    outlineVariant         = DarkOutline,
    error                  = Color(0xFFEF9A9A),
    onError                = Color.Black,
    errorContainer         = Color(0xFF93000A),
    onErrorContainer       = Color(0xFFFFDAD6),
    inverseSurface         = Color(0xFFECEFF1),
    inverseOnSurface       = InkBlack,
)

@Composable
fun NerkhTimesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = Typography,
        content     = content
    )
}
