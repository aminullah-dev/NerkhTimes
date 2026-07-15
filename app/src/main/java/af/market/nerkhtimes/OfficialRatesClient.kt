package af.market.nerkhtimes

import af.market.nerkhtimes.data.model.OfficialRatesResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface OfficialRatesService {

    @GET("v6/latest/USD")
    suspend fun latestUsd(): OfficialRatesResponse
}

/**
 * Separate Retrofit stack for the official-rate feed (open.er-api.com).
 * Kept apart from [ApiClient] so its JSON-enforcement interceptor and
 * Apps-Script-specific timeouts don't apply here.
 */
object OfficialRatesClient {

    private const val BASE_URL = "https://open.er-api.com/"

    val api: OfficialRatesService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OfficialRatesService::class.java)
    }
}
