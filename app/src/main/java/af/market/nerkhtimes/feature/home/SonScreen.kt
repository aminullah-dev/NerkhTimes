package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.ui.theme.components.MarketListContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun SonScreen(padding: PaddingValues, vm: MarketViewModel) {
    MarketListContent(padding = padding, vm = vm, group = "fuel")
}
