package at.shockbytes.coins.currency.price;

import java.util.List;

import at.shockbytes.coins.currency.CryptoCurrency;
import io.reactivex.Observable;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */

public class PriceConversionWrapper {

    public List<CryptoCurrency> from;
    public List<Observable<PriceConversion>> conversions;

    public PriceConversionWrapper(List<Observable<PriceConversion>> conversions,
                                  List<CryptoCurrency> from) {
        this.conversions = conversions;
        this.from = from;
    }
}