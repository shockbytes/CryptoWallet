package at.shockbytes.coins.network;

import at.shockbytes.coins.currency.CryptoCurrency;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.network.model.PriceConversion;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */

public interface PriceManager {

    Observable<PriceConversion> getSpotPrice(CryptoCurrency from, Currency to);

    Observable<PriceConversion> getBuyPrice(CryptoCurrency from, Currency to);

    Observable<PriceConversion> getSellPrice(CryptoCurrency from, Currency to);

    boolean supportsCurrencyConversion(CryptoCurrency currency);

}
