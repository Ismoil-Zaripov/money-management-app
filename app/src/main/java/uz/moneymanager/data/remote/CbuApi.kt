package uz.moneymanager.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

/** Central Bank of Uzbekistan public rates API. All rates are in UZS. */
interface CbuApi {
    /** @param date ISO date, e.g. 2026-10-17 */
    @GET("uz/arkhiv-kursov-valyut/json/all/{date}/")
    suspend fun ratesForDate(@Path("date") date: String): List<CbuRateDto>

    companion object {
        const val BASE_URL = "https://cbu.uz/"
    }
}

@Serializable
data class CbuRateDto(
    @SerialName("Ccy") val ccy: String,
    @SerialName("Nominal") val nominal: String = "1",
    @SerialName("Rate") val rate: String,
)
