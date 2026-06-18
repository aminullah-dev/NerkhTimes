package af.market.nerkhtimes.ui.theme.components

import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.ui.theme.categoryAccentColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MarketListContent(
    padding: PaddingValues,
    vm: MarketViewModel,
    group: String,
    title: String,
    icon: ImageVector
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val nf = remember { NumberFormat.getNumberInstance(Locale.US) }
    val accentColor = categoryAccentColor(group)

    val items = remember(state.data, state.selectedCityId, group) {
        state.currentCity()?.items?.filter { it.group == group }.orEmpty()
    }

    // Full-screen loading — only on first load (no cached data yet)
    if (state.loading && items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = accentColor)
        }
        return
    }

    // Full-screen error — only when we have no cached data at all
    if (state.error != null && items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            MarketFullErrorState(
                message = state.error!!,
                accentColor = accentColor,
                onRetry = { vm.refresh(silent = false) }
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "city_picker") {
            CityPicker(selectedId = state.selectedCityId, onSelect = vm::selectCity)
        }

        // Inline loading bar during background refresh
        if (state.loading) {
            item(key = "loading_bar") {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.2f)
                )
            }
        }

        // Inline error banner when we have old data but refresh failed
        if (state.error != null && items.isNotEmpty()) {
            item(key = "error_banner") {
                InlineErrorBanner(
                    message = state.error!!,
                    onRetry = { vm.refresh(silent = false) }
                )
            }
        }

        item(key = "category_header") {
            CategoryHeader(title = title, icon = icon, accentColor = accentColor)
        }

        if (items.isEmpty()) {
            item(key = "no_data") {
                Text(
                    text = stringResource(R.string.no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(items = items, key = { it.key }) { item ->
                ItemCard(item = item, nf = nf)
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String, icon: ImageVector, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon first → appears on the start edge (right in RTL, left in LTR)
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(accentColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        // Title fills the remaining width
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InlineErrorBanner(message: String, onRetry: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) {
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun MarketFullErrorState(
    message: String,
    accentColor: Color,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.error_retry))
        }
    }
}
