package af.market.nerkhtimes.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light scheme ──────────────────────────────────────────────────────────────
val TealPrimary        = Color(0xFF00897B)  // teal-600
val TealLight          = Color(0xFF4DB6AC)  // teal-300
val OffWhiteBg         = Color(0xFFF4F6F8)
val MutedGray          = Color(0xFFB0BEC5)
val InkBlack           = Color(0xFF1A1A2E)
val MintSoft           = Color(0xFFB2DFDB)

// primaryContainer / onPrimaryContainer for hero card
val TealContainer      = Color(0xFFE0F2F1)  // teal-50
val OnTealContainer    = Color(0xFF004D40)  // teal-900

// ── Dark scheme ───────────────────────────────────────────────────────────────
val DarkBg             = Color(0xFF0F1117)
val DarkSurface        = Color(0xFF1A1D26)
val DarkOnSurface      = Color(0xFFE8EAED)
val DarkOutline        = Color(0xFF2D3142)
val DarkTealContainer  = Color(0xFF00695C)  // teal-700
val OnDarkTealContainer= Color(0xFFE0F2F1)

// ── Category accent colours ───────────────────────────────────────────────────
val GoldAmber          = Color(0xFFFB8C00)  // orange-600  → gold / metals
val StonePurple        = Color(0xFF7B1FA2)  // purple-800  → gems / stones
val FoodGreen          = Color(0xFF388E3C)  // green-700   → food
val FuelOrange         = Color(0xFFE64A19)  // deep-orange-700 → fuel
val CurrencyTeal       = Color(0xFF00897B)  // teal-600    → currency
val ChartBlue          = Color(0xFF1565C0)  // blue-800    → chart

/** Returns a deterministic accent colour for a market group. */
fun categoryAccentColor(group: String): Color = when (group) {
    "gold"     -> GoldAmber
    "stones"   -> StonePurple
    "food"     -> FoodGreen
    "fuel"     -> FuelOrange
    "currency" -> CurrencyTeal
    else       -> Color(0xFF607D8B)
}
