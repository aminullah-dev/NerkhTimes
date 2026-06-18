package af.market.nerkhtimes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import af.market.nerkhtimes.data.model.Candle
import af.market.nerkhtimes.data.model.CityMarket
import af.market.nerkhtimes.data.model.MarketCatalog
import af.market.nerkhtimes.data.model.MarketItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class MarketUiState(
    val loading: Boolean = false,
    val data: List<CityMarket> = emptyList(),
    val error: String? = null,
    val selectedCityId: String = "kabul",
    val isFromCache: Boolean = false
) {
    fun currentCity(): CityMarket? = data.firstOrNull { it.city_id == selectedCityId }
}

data class CandleUiState(
    val loading: Boolean = false,
    val candles: List<Candle> = emptyList(),
    val error: String? = null,
    val key: String = "usd",
    val tf: Int = 5
)

class MarketViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MarketRepository(app.applicationContext)

    private val _state = MutableStateFlow(MarketUiState())
    val state: StateFlow<MarketUiState> = _state

    private val _candleState = MutableStateFlow(CandleUiState())
    val candleState: StateFlow<CandleUiState> = _candleState

    private var autoJob: Job? = null
    private var marketsJob: Job? = null
    private var candlesJob: Job? = null
    private var autoStarted = false

    init {
        load()
        startAutoRefresh(60)
    }

    fun load() = refresh(silent = false)

    fun refresh(silent: Boolean = true) {
        marketsJob?.cancel()
        marketsJob = viewModelScope.launch {
            if (!silent) _state.value = _state.value.copy(loading = true, error = null)

            val res = repo.fetchMarkets()

            if (!res.ok) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = res.error ?: "server_error"
                )
                return@launch
            }

            val useFarsi = Locale.getDefault().language == "fa"

            val fixed = res.data.map { city ->
                val itemsFixed = city.items
                    .map { it.mergeCatalog(useFarsi) }
                    .filter { it.price > 0.0 }
                city.copy(items = itemsFixed)
            }

            val cacheError = if (res.error == MarketRepository.CACHE_SENTINEL) {
                if (useFarsi) "آفلاین — داده‌های قبلی نشان داده می‌شود"
                else "آفلاین — زوړ معلومات ښودل کیږي"
            } else null

            _state.value = _state.value.copy(
                loading = false,
                data = fixed,
                error = cacheError,
                isFromCache = res.error == MarketRepository.CACHE_SENTINEL
            )
        }
    }

    fun startAutoRefresh(seconds: Int = 60) {
        if (autoStarted) return
        autoStarted = true

        autoJob?.cancel()
        autoJob = viewModelScope.launch {
            while (isActive) {
                delay(seconds * 1000L)
                refresh(silent = true)
            }
        }
    }

    fun selectCity(cityId: String) {
        _state.value = _state.value.copy(selectedCityId = cityId)
    }

    fun loadCandles(
        key: String = _candleState.value.key,
        tf: Int = _candleState.value.tf,
        limit: Int = 180
    ) {
        val cityId = _state.value.selectedCityId

        candlesJob?.cancel()
        candlesJob = viewModelScope.launch {
            _candleState.value = _candleState.value.copy(
                loading = true,
                error = null,
                key = key,
                tf = tf
            )

            val res = repo.fetchCandles(cityId = cityId, key = key, tf = tf, limit = limit)

            _candleState.value = _candleState.value.copy(
                loading = false,
                candles = res.candles,
                error = when {
                    !res.ok -> res.error ?: "API error"
                    res.candles.isEmpty() -> "History کې ډاټا نشته"
                    else -> null
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoJob?.cancel()
        marketsJob?.cancel()
        candlesJob?.cancel()
    }
}

private fun MarketItem.mergeCatalog(useFarsi: Boolean = false): MarketItem {
    val meta = MarketCatalog.metaByKey[key.lowercase()] ?: return this

    val catalogName = if (useFarsi && meta.name_fa.isNotBlank()) meta.name_fa else meta.name_ps
    val catalogUnit = if (useFarsi && meta.unit_fa.isNotBlank()) meta.unit_fa else meta.unit_ps

    val resolvedName = when {
        useFarsi          -> catalogName
        name_ps.isBlank() -> catalogName
        else              -> name_ps
    }
    val resolvedUnit = when {
        useFarsi          -> catalogUnit
        unit_ps.isBlank() -> catalogUnit
        else              -> unit_ps
    }

    return this.copy(
        name_ps = resolvedName,
        unit_ps = resolvedUnit,
        group   = meta.group
    )
}
