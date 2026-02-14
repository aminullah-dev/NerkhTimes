package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.navigation.NavRoutes
import af.market.nerkhtimes.ui.theme.components.CityPicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    padding: PaddingValues,
    nav: NavController,
    vm: MarketViewModel
) {
    val state by vm.state.collectAsStateWithLifecycle()

    val rawUpdate = state.currentCity()?.updated_at ?: ""
    val lastUpdate = remember(rawUpdate) { rawUpdate.cleanUpdateLabel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(6.dp))

        if (state.error != null) {
            Text(
                text = state.error ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.90f)
            )
            Spacer(Modifier.height(6.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(0.90f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${stringResource(R.string.last_update)} $lastUpdate",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = { vm.refresh(silent = false) }) {
                if (state.loading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        CityPicker(
            selectedId = state.selectedCityId,
            onSelect = vm::selectCity,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(Modifier.height(18.dp))

        MarketButton(stringResource(R.string.btn_metals), Icons.Default.Paid) { nav.navigate(NavRoutes.METALS) }
        Spacer(Modifier.height(10.dp))
        MarketButton(stringResource(R.string.btn_gems), Icons.Default.Diamond) { nav.navigate(NavRoutes.GEMS) }
        Spacer(Modifier.height(10.dp))
        MarketButton(stringResource(R.string.btn_food), Icons.Default.Restaurant) { nav.navigate(NavRoutes.FOOD) }
        Spacer(Modifier.height(10.dp))
        MarketButton(stringResource(R.string.btn_son), Icons.Default.LocalGasStation) { nav.navigate(NavRoutes.SON) }
        Spacer(Modifier.height(10.dp))
        MarketButton(stringResource(R.string.btn_chart), Icons.Default.ShowChart) { nav.navigate(NavRoutes.CHART) }
    }
}

@Composable
private fun MarketButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

private fun String.cleanUpdateLabel(): String {
    val s = this.trim()
    if (s.isBlank()) return "—"

    val lower = s.lowercase()
    if (lower.contains("1899") || lower.contains("sun dec 31 1899")) return "—"

    return s
}
