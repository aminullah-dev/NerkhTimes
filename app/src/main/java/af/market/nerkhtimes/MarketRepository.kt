package af.market.nerkhtimes

import android.content.Context
import af.market.nerkhtimes.data.model.CandleResponse
import af.market.nerkhtimes.data.model.MarketsResponse
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

class MarketRepository(ctx: Context) {

    private val api   = ApiClient.api
    private val cache = CacheManager(ctx)

    suspend fun fetchMarkets(): MarketsResponse {
        return try {
            val res = api.getMarkets()
            if (res.ok) cache.saveMarkets(res.data)
            res
        } catch (e: Exception) {
            val cached = cache.loadMarkets()
            if (cached != null) {
                MarketsResponse(ok = true, data = cached, error = CACHE_SENTINEL)
            } else {
                MarketsResponse(ok = false, data = emptyList(), error = humanError(e, "markets_error"))
            }
        }
    }

    suspend fun fetchCandles(
        cityId: String,
        key: String,
        tf: Int,
        limit: Int = 180
    ): CandleResponse {
        return try {
            api.getCandles(cityId = cityId, key = key, tf = tf, limit = limit)
        } catch (e: Exception) {
            CandleResponse(
                ok = false,
                city_id = cityId,
                key = key,
                tf = tf,
                candles = emptyList(),
                error = humanError(e, "candles_error")
            )
        }
    }

    private fun humanError(e: Exception, fallback: String): String {
        val farsi = Locale.getDefault().language == "fa"
        if (e is IOException) return if (farsi) "خطای اتصال به اینترنت" else "انټرنېټ ستونزه"
        if (e is HttpException) return if (farsi) "خطای سرور (${e.code()})" else "Server error (${e.code()})"

        val msg = (e.message ?: fallback).trim()

        if (msg.contains("JsonReader", ignoreCase = true) ||
            msg.contains("malformed", ignoreCase = true) ||
            msg.contains("Expected BEGIN_OBJECT", ignoreCase = true) ||
            msg.contains("Expected BEGIN_ARRAY", ignoreCase = true)
        ) return if (farsi) "مشکل در پاسخ سرور" else "Server JSON problem"

        if (msg.contains("non-json", ignoreCase = true) ||
            msg.contains("text/html", ignoreCase = true) ||
            msg.contains("html", ignoreCase = true)
        ) return if (farsi) "سرور پاسخ نامعتبر داد" else "Server returned HTML (check WebApp access)"

        return msg.ifBlank { fallback }
    }

    companion object {
        const val CACHE_SENTINEL = "__cached__"
    }
}
