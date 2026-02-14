package af.market.nerkhtimes.data.model

import com.google.gson.annotations.SerializedName

data class HistoryRow(
    @SerializedName("ts") val ts: Long = 0L,
    @SerializedName("city_id") val cityId: String = "",
    @SerializedName("group") val group: String = "other",
    @SerializedName("key") val key: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("by") val by: String = ""
)
