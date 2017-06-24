package at.shockbytes.coins.storage;

import java.util.List;

import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.network.model.PriceConversion;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 16.06.2017.
 */

public interface StorageManager {

    void storeOwnedCurrency(OwnedCurrency currency);

    void removeOwnedCurrency(OwnedCurrency currency);

    Observable<List<OwnedCurrency>> loadOwnedCurrencies();

    void updateConversionRates(OwnedCurrency c, List<PriceConversion> conversions);

}
