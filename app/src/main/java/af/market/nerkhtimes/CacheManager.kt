package af.market.nerkhtimes

import android.content.Context
import af.market.nerkhtimes.data.model.CityMarket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheManager(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("market_cache", Context.MODE_PRIVATE)
    private val gson  = Gson()

    fun saveMarkets(data: List<CityMarket>) {
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
}
