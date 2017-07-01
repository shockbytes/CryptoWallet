package at.shockbytes.coins.network.conversion;

import at.shockbytes.coins.currency.CurrencyConversionRates;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 01.07.2017.
 */

public interface CurrencyConversionApi {

    String SERVICE_ENDPOINT = "http://api.fixer.io/";

    @GET("latest")
    Observable<CurrencyConversionRates> getCurrencyConversionRates(@Query("base") String from,
                                                                   @Query("symbols") String to);


}
