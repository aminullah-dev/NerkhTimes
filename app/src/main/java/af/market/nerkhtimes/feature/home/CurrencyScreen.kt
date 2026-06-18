package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.ui.theme.components.MarketListContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun CurrencyScreen(padding: PaddingValues, vm: MarketViewModel) {
    MarketListContent(
        padding = padding,
        vm      = vm,
        group   = "currency",
        title   = stringResource(R.string.btn_currency),
        icon    = Icons.Default.CurrencyExchange
    )
}
