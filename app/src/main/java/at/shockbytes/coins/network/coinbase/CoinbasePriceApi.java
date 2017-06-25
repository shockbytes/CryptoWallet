package at.shockbytes.coins.network.coinbase;

import at.shockbytes.coins.network.model.PriceConversion;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */

public interface CoinbasePriceApi {

    String SERVICE_ENDPOINT = "https://api.coinbase.com/v2/prices/";

    @GET("{conversion}/spot")
    Observable<PriceConversion> getSpotPrice(@Path("conversion") String conversion,
                                @Header("CB-VERSION") String version);

    @GET("{conversion}/buy")
    Observable<PriceConversion> getBuyPrice(@Path("conversion") String conversion,
                                             @Header("CB-VERSION") String version);


    @GET("{conversion}/sell")
    Observable<PriceConversion> getSellPrice(@Path("conversion") String conversion,
                                            @Header("CB-VERSION") String version);

}
