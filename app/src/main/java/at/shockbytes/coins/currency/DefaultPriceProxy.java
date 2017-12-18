package at.shockbytes.coins.currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.shockbytes.coins.network.PriceManager;
import at.shockbytes.coins.network.model.PriceConversion;
import at.shockbytes.coins.network.model.PriceConversionWrapper;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author Martin Macheiner
 *         Date: 24.06.2017.
 */

public class DefaultPriceProxy implements PriceProxy {

    private List<PriceManager> priceManagers;

    public DefaultPriceProxy(PriceManager... priceManagers) {
        this.priceManagers = Arrays.asList(priceManagers);
    }

    @Override
    public Observable<List<PriceConversion>> getPriceConversions(List<CryptoCurrency> from,
                                                                 Currency to) {

        final PriceConversionWrapper wrapper = getConversionObservables(from, to);
        return Observable.zip(wrapper.conversions, new Function<Object[], List<PriceConversion>>() {

            @Override
            public List<PriceConversion> apply(Object... args) throws Exception {
                List<PriceConversion> conversions = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    PriceConversion pc = (PriceConversion) args[i];
                    pc.cryptoCurrency = wrapper.from.get(i);
                    conversions.add(pc);
                }
                return conversions;
            }
        });
    }

    private PriceConversionWrapper getConversionObservables(List<CryptoCurrency> from,
                                                            Currency to) {

        List<Observable<PriceConversion>> conversionObservables = new ArrayList<>();
        List<CryptoCurrency> conversionCurrencies = new ArrayList<>();
        for (CryptoCurrency currency : from) {

            // TODO Only support 1 conversion of each currency at a time
            for (PriceManager manager : priceManagers) {
                if (manager.supportsCurrencyConversion(currency)) {
                    conversionObservables.add(manager.getSpotPrice(currency, to));
                    conversionCurrencies.add(currency);
                    break;
                }
            }
        }
        return new PriceConversionWrapper(conversionObservables, conversionCurrencies);
    }

}
