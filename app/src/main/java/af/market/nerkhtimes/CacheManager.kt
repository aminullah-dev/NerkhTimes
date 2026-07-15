package af.market.nerkhtimes

import android.content.Context
import af.market.nerkhtimes.data.model.CityMarket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheManager(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("market_cache", Context.MODE_PRIVATE)
    private val gson  = Gson()

    fun saveMarkets(data: List<CityMarket>) {
        trackPriceChanges(data)
        prefs.edit().putString("markets_json", gson.toJson(data)).apply()
    }

    fun loadMarkets(): List<CityMarket>? {
        val json = prefs.getString("markets_json", null) ?: return null
        return try {
            val type = object : TypeToken<List<CityMarket>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Previous distinct price per item, keyed "cityId|itemKey".
     * Lets the UI show ▲/▼ change indicators across app restarts.
     */
    fun loadPrevPrices(): Map<String, Double> {
        val json = prefs.getString("prev_prices_json", null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Double>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /** Records the old price for every item whose price differs from the cached snapshot. */
    private fun trackPriceChanges(fresh: List<CityMarket>) {
        val old = loadMarkets() ?: return

        val oldPrices = HashMap<String, Double>()
        old.forEach { city ->
            city.items.forEach { item ->
                oldPrices[priceKey(city.city_id, item.key)] = item.price
            }
        }

        var changed = false
        val prev = loadPrevPrices().toMutableMap()
        fresh.forEach { city ->
            city.items.forEach { item ->
                val k = priceKey(city.city_id, item.key)
                val oldPrice = oldPrices[k]
                if (oldPrice != null && oldPrice > 0.0 && oldPrice != item.price) {
                    prev[k] = oldPrice
                    changed = true
                }
            }
        }
        if (changed) {
            prefs.edit().putString("prev_prices_json", gson.toJson(prev)).apply()
        }
    }

    companion object {
        fun priceKey(cityId: String, itemKey: String) = "$cityId|${itemKey.lowercase()}"
    }
}
