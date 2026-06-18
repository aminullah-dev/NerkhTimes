package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.ui.theme.components.MarketListContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun FoodScreen(padding: PaddingValues, vm: MarketViewModel) {
    MarketListContent(
        padding = padding,
        vm      = vm,
        group   = "food",
        title   = stringResource(R.string.btn_food),
        icon    = Icons.Default.Restaurant
    )
}
