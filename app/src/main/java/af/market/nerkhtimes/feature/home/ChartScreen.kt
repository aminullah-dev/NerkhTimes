package af.market.nerkhtimes.feature.home

import android.graphics.Paint
import android.view.ViewGroup
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Industry-standard candle colours (TradingView convention) — same in both themes
private val BullGreen = Color(0xFF26A69A)
private val BearRed   = Color(0xFFEF5350)

private val TF_OPTIONS = listOf(5, 15, 30, 60, 240)

private fun tfLabel(tf: Int): String = if (tf >= 60) "${tf / 60}h" else "${tf}m"

private fun candleTimeMs(t: Long): Long = if (t < 2_000_000_000L) t * 1000L else t

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

    val cityName = state.currentCity()?.city_name?.takeIf { it.isNotBlank() } ?: state.selectedCityId

    val sorted = remember(cState.candles) { cState.candles.sortedBy { it.t } }

    // Candle under the crosshair; null → show the latest candle in the header
    var selectedCandle by remember(sorted) { mutableStateOf<Candle?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Instrument controls ───────────────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CityPicker(selectedId = state.selectedCityId, onSelect = vm::selectCity)

                KeyPicker(
                    selected = key,
                    onSelect = { vm.loadCandles(key = it) }
                )

                TimeframeChips(
                    selected = tf,
                    onSelect = { vm.loadCandles(tf = it) }
                )
            }
        }

        // ── Price header + chart ─────────────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                PriceHeader(
                    key = key,
                    cityName = cityName,
                    tf = tf,
                    candles = sorted,
                    displayCandle = selectedCandle,
                    onRefresh = { vm.loadCandles(key = key, tf = tf) }
                )

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
                        sorted.isEmpty() -> EmptyState(
                            onRetry = { vm.loadCandles(key = key, tf = tf) },
                            hint = stringResource(R.string.chart_empty_hint)
                        )
                        else -> CandleChartView(
                            candles = sorted,
                            tf = tf,
                            onCandleSelected = { selectedCandle = it }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── TradingView-style price header ────────────────────────────────────────────

@Composable
private fun PriceHeader(
    key: String,
    cityName: String,
    tf: Int,
    candles: List<Candle>,
    displayCandle: Candle?,
    onRefresh: () -> Unit
) {
    val priceFmt = remember { DecimalFormat("#,##0.##") }
    val timeFmt  = remember(tf) {
        SimpleDateFormat(if (tf >= 60) "MM-dd HH:mm" else "HH:mm", Locale.US)
    }

    val last  = candles.lastOrNull()
    val shown = displayCandle ?: last

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${key.uppercase()} · $cityName · ${tfLabel(tf)}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }

        if (last != null) {
            // Change over the loaded period: last close vs first open
            val base   = candles.first().o
            val change = last.c - base
            val pct    = if (base != 0.0) change / base * 100 else 0.0
            val up     = change >= 0
            val trendColor = if (up) BullGreen else BearRed
            val arrow      = if (up) "▲" else "▼"
            val sign       = if (up) "+" else ""

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = priceFmt.format(last.c),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$arrow $sign${priceFmt.format(change)} ($sign${String.format(Locale.US, "%.2f", pct)}%)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = trendColor,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }

        if (shown != null) {
            // OHLC legend — universal forex notation, updates with the crosshair
            val ohlcColor = if (shown.c >= shown.o) BullGreen else BearRed
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OhlcValue("O", shown.o, ohlcColor, priceFmt)
                OhlcValue("H", shown.h, ohlcColor, priceFmt)
                OhlcValue("L", shown.l, ohlcColor, priceFmt)
                OhlcValue("C", shown.c, ohlcColor, priceFmt)
                Spacer(Modifier.weight(1f))
                Text(
                    text = timeFmt.format(Date(candleTimeMs(shown.t))),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OhlcValue(label: String, value: Double, color: Color, fmt: DecimalFormat) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = fmt.format(value),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ── States ────────────────────────────────────────────────────────────────────

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

// ── Chart ─────────────────────────────────────────────────────────────────────

@Composable
private fun CandleChartView(
    candles: List<Candle>,
    tf: Int,
    onCandleSelected: (Candle?) -> Unit
) {
    val entries = remember(candles) {
        candles.mapIndexed { index, c ->
            CandleEntry(index.toFloat(), c.h.toFloat(), c.l.toFloat(), c.o.toFloat(), c.c.toFloat())
        }
    }

    val scheme        = MaterialTheme.colorScheme
    val bullColor     = BullGreen.toArgb()
    val bearColor     = BearRed.toArgb()
    val neutralArgb   = scheme.outline.toArgb()
    val textColor     = scheme.onSurfaceVariant.toArgb()
    val gridColorInt  = scheme.outlineVariant.copy(alpha = 0.5f).toArgb()
    val crosshairColor = scheme.onSurfaceVariant.copy(alpha = 0.6f).toArgb()

    val priceFmt = remember { DecimalFormat("#,##0.##") }

    val xFormatter = remember(candles, tf) {
        val fmt = SimpleDateFormat(if (tf >= 60) "MM-dd HH:mm" else "HH:mm", Locale.US)
        object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val i = value.toInt()
                if (i < 0 || i >= candles.size) return ""
                return fmt.format(Date(candleTimeMs(candles[i].t)))
            }
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
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
                isDragEnabled = true
                setScaleEnabled(true)
                // Rescale Y to the visible candles while panning — forex-chart behaviour
                isAutoScaleMinMaxEnabled = true
                isHighlightPerDragEnabled = true
                isHighlightPerTapEnabled  = true
                setNoDataText(" ")
                setNoDataTextColor(textColor)

                // Price axis on the RIGHT — forex convention
                axisLeft.isEnabled = false
                axisRight.apply {
                    isEnabled = true
                    setDrawGridLines(true)
                    gridColor = gridColorInt
                    this.textColor = textColor
                    textSize = 11f
                    setDrawAxisLine(false)
                    setLabelCount(6, false)
                    minWidth = 48f
                    setDrawLimitLinesBehindData(true)
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                    gridColor = gridColorInt
                    setDrawAxisLine(false)
                    this.textColor = textColor
                    textSize = 10f
                    granularity = 1f
                    setLabelCount(4, false)
                    setAvoidFirstLastClipping(true)
                }
            }
        },
        update = { chart ->
            val dataSet = CandleDataSet(entries, "Price").apply {
                setDrawValues(false)
                shadowWidth = 1.2f
                setShadowColorSameAsCandle(true)
                increasingColor = bullColor
                increasingPaintStyle = Paint.Style.FILL
                decreasingColor = bearColor
                decreasingPaintStyle = Paint.Style.FILL
                neutralColor = neutralArgb
                barSpace = 0.15f
                // Crosshair
                isHighlightEnabled = true
                highLightColor = crosshairColor
                highlightLineWidth = 0.8f
                setDrawHighlightIndicators(true)
            }

            chart.xAxis.valueFormatter = xFormatter

            // Dashed last-price line on the price axis
            chart.axisRight.removeAllLimitLines()
            val lastCandle = candles.lastOrNull()
            if (lastCandle != null) {
                val lineColor = if (lastCandle.c >= lastCandle.o) bullColor else bearColor
                chart.axisRight.addLimitLine(
                    LimitLine(lastCandle.c.toFloat(), priceFmt.format(lastCandle.c)).apply {
                        this.lineColor = lineColor
                        lineWidth = 1f
                        enableDashedLine(12f, 8f, 0f)
                        this.textColor = lineColor
                        textSize = 10f
                        labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                    }
                )
            }

            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val i = e?.x?.toInt() ?: return
                    onCandleSelected(candles.getOrNull(i))
                }
                override fun onNothingSelected() {
                    onCandleSelected(null)
                }
            })

            chart.data = CandleData(dataSet)
            chart.notifyDataSetChanged()
            chart.setVisibleXRangeMaximum(50f)
            if (entries.isNotEmpty()) chart.moveViewToX((entries.size - 1).toFloat())
            chart.invalidate()
        }
    )
}

// ── Pickers ───────────────────────────────────────────────────────────────────

@Composable
private fun TimeframeChips(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TF_OPTIONS.forEach { t ->
            FilterChip(
                selected = t == selected,
                onClick = { onSelect(t) },
                label = { Text(tfLabel(t)) }
            )
        }
    }
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
