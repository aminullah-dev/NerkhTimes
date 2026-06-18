package af.market.nerkhtimes.feature.home

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.data.model.MarketItem
import af.market.nerkhtimes.navigation.NavRoutes
import af.market.nerkhtimes.ui.theme.*
import af.market.nerkhtimes.ui.theme.components.CityPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

private data class CategoryItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val accentColor: Color
)

@Composable
fun HomeScreen(
    padding: PaddingValues,
    nav: NavController,
    vm: MarketViewModel
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val nf = remember { NumberFormat.getNumberInstance(Locale.US) }
    val scroll = rememberScrollState()

    val rawUpdate = state.currentCity()?.updated_at ?: ""
    val lastUpdate = remember(rawUpdate) { rawUpdate.cleanUpdateLabel() }

    val currencyItems = remember(state.data, state.selectedCityId) {
        state.currentCity()?.items?.filter { it.group == "currency" }.orEmpty()
    }

    val categories = listOf(
        CategoryItem(stringResource(R.string.btn_currency),  Icons.Default.CurrencyExchange, NavRoutes.CURRENCY, CurrencyTeal),
        CategoryItem(stringResource(R.string.btn_metals),    Icons.Default.Paid,             NavRoutes.METALS,   GoldAmber),
        CategoryItem(stringResource(R.string.btn_gems),      Icons.Default.Diamond,          NavRoutes.GEMS,     StonePurple),
        CategoryItem(stringResource(R.string.btn_food),      Icons.Default.Restaurant,       NavRoutes.FOOD,     FoodGreen),
        CategoryItem(stringResource(R.string.btn_son),       Icons.Default.LocalGasStation,  NavRoutes.SON,      FuelOrange),
        CategoryItem(stringResource(R.string.btn_chart),     Icons.Default.ShowChart,        NavRoutes.CHART,    ChartBlue)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Hero card ─────────────────────────────────────────────────────────
        HeroCard(
            lastUpdate  = lastUpdate,
            loading     = state.loading,
            error       = state.error,
            selectedCityId = state.selectedCityId,
            onSelectCity = vm::selectCity,
            onRefresh   = { vm.refresh(silent = false) }
        )

        // ── Live exchange rates ───────────────────────────────────────────────
        if (currencyItems.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel(stringResource(R.string.section_live_rates))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(currencyItems, key = { it.key }) { item ->
                        RateChip(item = item, nf = nf)
                    }
                }
            }
        }

        // ── Category grid (2-column) ──────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel(stringResource(R.string.section_markets))
            categories.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { cat ->
                        CategoryCard(
                            title       = cat.title,
                            icon        = cat.icon,
                            accentColor = cat.accentColor,
                            modifier    = Modifier.weight(1f),
                            onClick     = { nav.navigate(cat.route) }
                        )
                    }
                    // If the last row has only 1 item, fill the empty cell
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ── Hero card ─────────────────────────────────────────────────────────────────

@Composable
private fun HeroCard(
    lastUpdate: String,
    loading: Boolean,
    error: String?,
    selectedCityId: String,
    onSelectCity: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = stringResource(R.string.app_name),
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text  = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = onRefresh) {
                    if (loading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier    = Modifier.size(22.dp),
                            color       = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // City picker
            CityPicker(selectedId = selectedCityId, onSelect = onSelectCity)

            // Last update + optional error
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (lastUpdate.isNotBlank() && lastUpdate != "—") {
                    Text(
                        text  = "${stringResource(R.string.last_update)} $lastUpdate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }

                if (error != null) {
                    Text(
                        text  = error,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// ── Live rate chip ─────────────────────────────────────────────────────────────

@Composable
private fun RateChip(item: MarketItem, nf: NumberFormat) {
    Surface(
        shape  = MaterialTheme.shapes.medium,
        color  = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.widthIn(min = 90.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text       = nf.format(item.price),
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = CurrencyTeal
            )
            Text(
                text  = item.name_ps.ifBlank { item.key.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text  = "؋ AFN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// ── Category grid card ────────────────────────────────────────────────────────

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick   = onClick,
        modifier  = modifier.height(100.dp),
        shape     = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(accentColor.copy(alpha = 0.14f), CircleShape)
                )
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = accentColor,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text      = title,
                style     = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines  = 2,
                color     = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text   = text,
        style  = MaterialTheme.typography.labelLarge,
        color  = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

private fun String.cleanUpdateLabel(): String {
    val s = trim()
    if (s.isBlank()) return "—"
    val lower = s.lowercase()
    if (lower.contains("1899") || lower.contains("sun dec 31 1899")) return "—"
    return s
}
