package af.market.nerkhtimes

import android.content.Context
import af.market.nerkhtimes.data.model.OfficialRates
import com.google.gson.Gson

/**
 * Fetches the official interbank AFN rate (USD/EUR/PKR) and caches it on disk.
 * The upstream feed updates once a day, so a 12-hour cache is more than enough.
 * All failures are silent — callers get the last cached value or null, and the
 * UI simply hides the official-rate card when nothing is available.
 */
class OfficialRatesRepository(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("official_rates", Context.MODE_PRIVATE)
    private val gson  = Gson()

    suspend fun getRates(): OfficialRates? {
        val cached = loadCache()
        if (cached != null && isFresh()) return cached

        return try {
            val res = OfficialRatesClient.api.latestUsd()
            val afn = res.rates["AFN"]
            if (res.result != "success" || afn == null || afn <= 0.0) return cached

            val eur = res.rates["EUR"] ?: 0.0
            val pkr = res.rates["PKR"] ?: 0.0
            val rates = OfficialRates(
                usdToAfn = afn,
                eurToAfn = if (eur > 0.0) afn / eur else 0.0,
                pkrToAfn = if (pkr > 0.0) afn / pkr else 0.0,
                updatedUnix = res.timeLastUpdateUnix
            )
            saveCache(rates)
            rates
        } catch (_: Exception) {
            cached
        }
    }

    private fun isFresh(): Boolean {
        val fetchedAt = prefs.getLong(KEY_FETCHED_AT, 0L)
        return System.currentTimeMillis() - fetchedAt < CACHE_TTL_MS
    }

    private fun saveCache(rates: OfficialRates) {
        prefs.edit()
            .putString(KEY_JSON, gson.toJson(rates))
            .putLong(KEY_FETCHED_AT, System.currentTimeMillis())
            .apply()
    }

    private fun loadCache(): OfficialRates? {
        val json = prefs.getString(KEY_JSON, null) ?: return null
        return try {
            gson.fromJson(json, OfficialRates::class.java)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val KEY_JSON       = "rates_json"
        private const val KEY_FETCHED_AT = "fetched_at"
        private const val CACHE_TTL_MS   = 12 * 60 * 60 * 1000L
    }
}
