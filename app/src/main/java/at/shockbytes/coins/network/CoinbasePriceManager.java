package at.shockbytes.coins.network;

import java.util.Arrays;

import javax.inject.Inject;

import at.shockbytes.coins.currency.CryptoCurrency;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.network.model.PriceConversion;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */

public class CoinbasePriceManager implements PriceManager {

    private CoinbasePriceApi api;

    @Inject
    public CoinbasePriceManager(CoinbasePriceApi api) {
        this.api = api;
    }

    @Override
    public Observable<PriceConversion> getSpotPrice(CryptoCurrency from, Currency to) {
        return api.getSpotPrice(buildConversionPath(from, to), buildTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<PriceConversion> getBuyPrice(CryptoCurrency from, Currency to) {
        return api.getBuyPrice(buildConversionPath(from, to), buildTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<PriceConversion> getSellPrice(CryptoCurrency from, Currency to) {
        return api.getSellPrice(buildConversionPath(from, to), buildTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public boolean supportsCurrencyConversion(CryptoCurrency currency) {
        return Arrays.asList(CryptoCurrency.BTC, CryptoCurrency.ETH, CryptoCurrency.LTC)
                .contains(currency);
    }

    private String buildConversionPath(CryptoCurrency from, Currency to) {
        return from.name() + "-" + to.name();
    }

    private String buildTimestamp() {
        return null;
    }

}
