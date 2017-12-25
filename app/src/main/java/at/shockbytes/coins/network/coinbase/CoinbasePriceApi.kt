package at.shockbytes.coins.network.coinbase

import at.shockbytes.coins.currency.conversion.PriceConversion
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

interface CoinbasePriceApi {

    @GET("{conversion}/spot")
    fun getSpotPrice(@Path("conversion") conversion: String,
                     @Header("CB-VERSION") version: String): Observable<PriceConversion>

    @GET("{conversion}/buy")
    fun getBuyPrice(@Path("conversion") conversion: String,
                    @Header("CB-VERSION") version: String): Observable<PriceConversion>


    @GET("{conversion}/sell")
    fun getSellPrice(@Path("conversion") conversion: String,
                     @Header("CB-VERSION") version: String): Observable<PriceConversion>

    companion object {

        val SERVICE_ENDPOINT = "https://api.coinbase.com/v2/prices/"
    }

}
