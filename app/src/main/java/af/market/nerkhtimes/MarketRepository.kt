package af.market.nerkhtimes

import af.market.nerkhtimes.data.model.CandleResponse
import af.market.nerkhtimes.data.model.MarketsResponse
import retrofit2.HttpException
import java.io.IOException

class MarketRepository {

    private val api = ApiClient.api

    suspend fun fetchMarkets(): MarketsResponse {
        return try {
            api.getMarkets()
        } catch (e: Exception) {
            MarketsResponse(
                ok = false,
                data = emptyList(),
                error = humanError(e, fallback = "markets_error")
            )
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
                error = humanError(e, fallback = "candles_error")
            )
        }
    }

    private fun humanError(e: Exception, fallback: String): String {
        if (e is IOException) return "انټرنېټ ستونزه"
        if (e is HttpException) return "Server error (${e.code()})"

        val msg = (e.message ?: fallback).trim()

        if (msg.contains("JsonReader", ignoreCase = true) ||
            msg.contains("malformed", ignoreCase = true) ||
            msg.contains("Expected BEGIN_OBJECT", ignoreCase = true) ||
            msg.contains("Expected BEGIN_ARRAY", ignoreCase = true)
        ) return "Server JSON problem"

        if (msg.contains("non-json", ignoreCase = true) ||
            msg.contains("text/html", ignoreCase = true) ||
            msg.contains("html", ignoreCase = true)
        ) return "Server returned HTML (check WebApp access)"

        return msg.ifBlank { fallback }
    }
}
