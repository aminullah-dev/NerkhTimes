package af.market.nerkhtimes

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    private fun createClient(): OkHttpClient {

        val builder = OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)

        // ---- JSON enforcement interceptor ----
        builder.addInterceptor { chain ->

            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .build()

            val response = chain.proceed(request)

            val contentType = (response.header("Content-Type") ?: "").lowercase()
            val preview = response.peekBody(32 * 1024).string().trimStart()

            val looksJson = preview.startsWith("{") || preview.startsWith("[")
            val ctJson = contentType.contains("application/json") || contentType.contains("text/json")

            if (BuildConfig.DEBUG && !ctJson && !looksJson) {
                Log.e(
                    "API_RAW",
                    "URL=${request.url}\nCT=$contentType\nBODY=${preview.take(500)}"
                )
            }

            if (!ctJson && !looksJson) {
                response.close()
                throw IOException("Server returned non-JSON (check WebApp access)")
            }

            response
        }

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logger)
        }

        return builder.build()
    }

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(ApiUrls.BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
