package af.market.nerkhtimes.feature.home

import android.graphics.Paint
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import af.market.nerkhtimes.MarketViewModel
import af.market.nerkhtimes.R
import af.market.nerkhtimes.data.model.Candle
import af.market.nerkhtimes.data.model.MarketCatalog
import af.market.nerkhtimes.ui.theme.components.CityPicker
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChartScreen(
    padding: PaddingValues,
    vm: MarketViewModel
) {
    val state  = vm.state.collectAsStateWithLifecycle().value
    val cState = vm.candleState.collectAsStateWithLifecycle().value

    // key and tf live in the ViewModel so they survive configuration changes
    val key = cState.key
    val tf  = cState.tf

    // Load candles when city changes (initial load + city picker changes)
    LaunchedEffect(state.selectedCityId) {
        vm.loadCandles(key = key, tf = tf)
    }

    val cityName  = state.currentCity()?.city_name?.takeIf { it.isNotBlank() } ?: state.selectedCityId
    val tfLabel   = if (tf >= 60) "${tf / 60}h" else "${tf}m"
    val lastClose = cState.candles.lastOrNull()?.c

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.chart_header_title),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.chart_header_sub),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CityPicker(selectedId = state.selectedCityId, onSelect = vm::selectCity)

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(Modifier.weight(1f)) {
                        KeyPicker(
                            selected = key,
                            onSelect = { vm.loadCandles(key = it) }
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        TfPicker(
                            selected = tf,
                            onSelect = { vm.loadCandles(tf = it) }
                        )
                    }
                }
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${key.uppercase()} — $cityName — $tfLabel",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (lastClose != null) "${stringResource(R.string.chart_last)} $lastClose" else " ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { vm.loadCandles(key = key, tf = tf) }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        cState.loading    -> LoadingState()
                        cState.error != null -> ErrorState(
                            message = cState.error,
                            onRetry = { vm.loadCandles(key = key, tf = tf) }
                        )
                        cState.candles.isEmpty() -> EmptyState(
                            onRetry = { vm.loadCandles(key = key, tf = tf) },
                            hint = stringResource(R.string.chart_empty_hint)
                        )
                        else -> CandleChartView(candles = cState.candles)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LoadingState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.chart_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit, hint: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.chart_empty), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${stringResource(R.string.error_prefix)} $message",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun CandleChartView(candles: List<Candle>) {
    val sorted = remember(candles) { candles.sortedBy { it.t } }

    val entries = remember(sorted) {
        sorted.mapIndexed { index, c ->
            CandleEntry(index.toFloat(), c.h.toFloat(), c.l.toFloat(), c.o.toFloat(), c.c.toFloat())
        }
    }

    val scheme       = MaterialTheme.colorScheme
    val inc          = scheme.primary.toArgb()
    val dec          = scheme.error.toArgb()
    val neu          = scheme.tertiary.toArgb()
    val textColor    = scheme.onSurfaceVariant.toArgb()
    val gridColorInt = scheme.outlineVariant.toArgb()

    val xFormatter = remember(sorted) {
        val fmt = SimpleDateFormat("HH:mm", Locale.US)
        object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val i = value.toInt()
                if (i < 0 || i >= sorted.size) return ""
                val ts = sorted[i].t
                val ms = if (ts < 2_000_000_000L) ts * 1000L else ts
                return fmt.format(Date(ms))
            }
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        factory = { ctx ->
            CandleStickChart(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled      = false
                setTouchEnabled(true)
                setPinchZoom(true)
                isDoubleTapToZoomEnabled = true
                setNoDataText(" ")
                setNoDataTextColor(textColor)
                axisRight.isEnabled = false
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = gridColorInt
                    this.textColor = textColor
                    setLabelCount(6, true)
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    this.textColor = textColor
                    granularity   = 1f
                    setLabelCount(4, true)
                    valueFormatter = xFormatter
                }
                setVisibleXRangeMaximum(40f)
            }
        },
        update = { chart ->
            val dataSet = CandleDataSet(entries, "Price").apply {
                setDrawValues(false)
                setShadowWidth(1.2f)
                setIncreasingColor(inc)
                setIncreasingPaintStyle(Paint.Style.FILL)
                setDecreasingColor(dec)
                setDecreasingPaintStyle(Paint.Style.FILL)
                setNeutralColor(neu)
            }
            chart.xAxis.valueFormatter = xFormatter
            chart.data = CandleData(dataSet)
            chart.notifyDataSetChanged()
            chart.invalidate()
            if (entries.isNotEmpty()) chart.moveViewToX((entries.size - 1).toFloat())
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KeyPicker(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = remember { MarketCatalog.allKeysForChart() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.uppercase(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_asset)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.uppercase()) },
                    onClick = { expanded = false; onSelect(option) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TfPicker(selected: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(5, 15, 30, 60, 240)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selected >= 60) "${selected / 60}h" else "${selected}m",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_tf)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { t ->
                DropdownMenuItem(
                    text = { Text(if (t >= 60) "${t / 60}h" else "${t}m") },
                    onClick = { expanded = false; onSelect(t) }
                )
            }
        }
    }
}
