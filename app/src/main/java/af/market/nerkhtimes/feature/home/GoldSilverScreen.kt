package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.ui.theme.components.CityPicker
import af.market.nerkhtimes.ui.theme.components.ItemCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoldSilverScreen(padding: PaddingValues, vm: MarketViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val nf = NumberFormat.getNumberInstance(Locale.US)

    val list = state.currentCity()?.items
        ?.filter { it.group == "gold" }
        .orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CityPicker(selectedId = state.selectedCityId, onSelect = vm::selectCity)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            when {
                state.loading -> CircularProgressIndicator()
                state.error != null -> Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                list.isEmpty() -> Text(stringResource(R.string.no_data))
                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(list) { ItemCard(item = it, nf = nf) }
                }
            }
        }
    }
}
