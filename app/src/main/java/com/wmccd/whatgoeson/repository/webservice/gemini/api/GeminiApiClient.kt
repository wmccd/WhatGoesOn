import com.wmccd.whatgoeson.repository.webservice.gemini.api.GeminiApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private var geminiApiService: GeminiApiService? = null

    fun getGeminiApiService(): GeminiApiService {
        if (geminiApiService == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            geminiApiService = retrofit.create(GeminiApiService::class.java)
        }
        return geminiApiService!!
    }
}