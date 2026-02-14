package af.market.nerkhtimes

import af.market.nerkhtimes.data.model.CandleResponse
import af.market.nerkhtimes.data.model.MarketsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("exec")
    suspend fun getMarkets(
        @Query("action") action: String = "markets"
    ): MarketsResponse

    @GET("exec")
    suspend fun getCandles(
        @Query("action") action: String = "candles",
        @Query("city_id") cityId: String,
        @Query("key") key: String,
        @Query("tf") tf: Int,
        @Query("limit") limit: Int = 180
    ): CandleResponse
}
