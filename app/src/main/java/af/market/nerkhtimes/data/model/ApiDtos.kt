package af.market.nerkhtimes.data.model

import com.google.gson.annotations.SerializedName

data class MarketsResponse(
    @SerializedName("ok") val ok: Boolean = false,
    @SerializedName("data") val data: List<CityMarket> = emptyList(),
    @SerializedName("error") val error: String? = null
)

data class CityMarket(
    @SerializedName("city_id") val city_id: String = "",

    @SerializedName(value = "city_name", alternate = ["city_ps"])
    val city_name: String = "",

    @SerializedName(value = "updated_at", alternate = ["updatedAt"])
    val updated_at: String = "",

    @SerializedName("items") val items: List<MarketItem> = emptyList()
)

data class MarketItem(
    @SerializedName("key") val key: String = "",

    @SerializedName(value = "name_ps", alternate = ["name"])
    val name_ps: String = "",

    @SerializedName(value = "unit_ps", alternate = ["unit"])
    val unit_ps: String = "",

    @SerializedName("price") val price: Double = 0.0,

    val group: String = "other"
)

data class CandleResponse(
    @SerializedName("ok") val ok: Boolean = false,
    @SerializedName("city_id") val city_id: String = "",
    @SerializedName("key") val key: String = "",
    @SerializedName("tf") val tf: Int = 5,
    @SerializedName("candles") val candles: List<Candle> = emptyList(),
    @SerializedName("error") val error: String? = null
)

data class Candle(
    @SerializedName("t") val t: Long = 0L,
    @SerializedName("o") val o: Double = 0.0,
    @SerializedName("h") val h: Double = 0.0,
    @SerializedName("l") val l: Double = 0.0,
    @SerializedName("c") val c: Double = 0.0
)
