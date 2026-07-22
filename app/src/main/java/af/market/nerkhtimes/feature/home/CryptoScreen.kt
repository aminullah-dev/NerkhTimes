package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.ui.theme.components.MarketListContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun CryptoScreen(padding: PaddingValues, vm: MarketViewModel) {
    MarketListContent(
        padding = padding,
        vm      = vm,
        group   = "crypto",
        title   = stringResource(R.string.btn_crypto),
        icon    = Icons.Default.CurrencyBitcoin
    )
}
