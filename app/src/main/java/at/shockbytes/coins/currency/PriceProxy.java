package at.shockbytes.coins.currency;

import java.util.List;

import at.shockbytes.coins.network.model.PriceConversion;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 24.06.2017.
 */

public interface PriceProxy {

    Observable<List<PriceConversion>> getPriceConversions(List<CryptoCurrency> from, Currency to);

}
