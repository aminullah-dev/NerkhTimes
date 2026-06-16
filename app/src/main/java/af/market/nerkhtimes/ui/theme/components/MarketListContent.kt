package af.market.nerkhtimes.ui.theme.components

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.data.model.MarketItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MarketListContent(
    padding: PaddingValues,
    vm: MarketViewModel,
    group: String
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val nf = remember { NumberFormat.getNumberInstance(Locale.US) }

    val items = remember(state.data, state.selectedCityId, group) {
        state.currentCity()?.items?.filter { it.group == group }.orEmpty()
    }

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
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                state.loading -> CircularProgressIndicator()
                state.error != null -> MarketErrorState(
                    message = state.error ?: "",
                    onRetry = { vm.refresh(silent = false) }
                )
                items.isEmpty() -> Text(stringResource(R.string.no_data))
                else -> MarketItemList(items = items, nf = nf)
            }
        }
    }
}

@Composable
private fun MarketItemList(items: List<MarketItem>, nf: NumberFormat) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = items, key = { it.key }) { item ->
            ItemCard(item = item, nf = nf)
        }
    }
}

@Composable
private fun MarketErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedButton(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.error_retry))
        }
    }
}
