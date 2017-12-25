package at.shockbytes.coins.network.coinmarketcap

import at.shockbytes.coins.network.coinmarketcap.model.CoinMarketCapApiObject
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author Martin Macheiner
 * Date: 25.12.2017.
 */
interface CoinMarketCapPriceApi {


    @GET("{currency}/")
    fun getSpotPrice(@Path("currency") currencyId: String,
                     @Query("convert") conversionCurrency: String): Observable<List<CoinMarketCapApiObject>>

    companion object {

        val SERVICE_ENDPOINT = "https://api.coinmarketcap.com/v1/ticker/"
    }

}