package at.shockbytes.coins.network.conversion

import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Martin Macheiner
 * Date: 01.07.2017.
 */

interface CurrencyConversionApi {

    @GET("latest")
    fun getCurrencyConversionRates(@Query("base") from: String,
                                   @Query("symbols") to: String): Observable<CurrencyConversionRates>

    companion object {

        val SERVICE_ENDPOINT = "http://api.fixer.io/"
    }


}
