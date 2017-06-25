package at.shockbytes.coins.currency;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.coins.network.model.PriceConversion;
import at.shockbytes.coins.storage.StorageManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class DefaultCurrencyManager implements CurrencyManager {

    private PriceProxy priceProxy;
    private StorageManager storageManager;
    private SharedPreferences prefs;

    private String PREFS_LOCAL_CURRENCY = "prefs_local_currency";

    private Balance balance;

    @Inject
    public DefaultCurrencyManager(Context context, PriceProxy priceProxy,
                                  StorageManager storageManager) {
        this.priceProxy = priceProxy;
        this.storageManager = storageManager;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void setLocalCurrency(Currency currency) {
        prefs.edit().putInt(PREFS_LOCAL_CURRENCY, currency.ordinal()).apply();
    }

    @Override
    public Currency getLocalCurrency() {
        return Currency.values()[prefs.getInt(PREFS_LOCAL_CURRENCY, Currency.EUR.ordinal())];
    }

    @Override
    public Observable<List<OwnedCurrency>> getOwnedCurrencies() {

        final Observable<List<OwnedCurrency>> localCurrencies = storageManager.loadOwnedCurrencies(false);

        return Observable.zip(localCurrencies,
                priceProxy.getPriceConversions(Arrays.asList(CryptoCurrency.values()), getLocalCurrency()),
                new Func2<List<OwnedCurrency>, List<PriceConversion>,
                                        List<OwnedCurrency>>() {
                    @Override
                    public List<OwnedCurrency> call(List<OwnedCurrency> c,
                                                    List<PriceConversion> conversions) {

                        balance = new Balance();
                        // Assign the conversion rates to the corresponding currencies
                        for (OwnedCurrency oc : c) {
                            balance.addInvested(oc.getBoughtPrice());
                            storageManager.updateConversionRates(oc, conversions);
                            balance.addCurrent(oc.getCurrentPrice());
                        }
                        return c;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<OwnedCurrency>> getCashedoutCurrencies() {
        return storageManager.loadOwnedCurrencies(true)
                .map(new Func1<List<OwnedCurrency>, List<OwnedCurrency>>() {
            @Override
            public List<OwnedCurrency> call(List<OwnedCurrency> currencies) {

                balance = new Balance();
                for (OwnedCurrency oc : currencies) {
                    balance.addInvested(oc.getBoughtPrice());
                    balance.addCurrent(oc.getCurrentPrice());
                }
                return currencies;
            }
        });
    }

    @Override
    public void addOwnedCurrency(OwnedCurrency ownedCurrency) {
        storageManager.storeOwnedCurrency(ownedCurrency);
    }

    @Override
    public Balance getBalance() {
        return balance;
    }

    @Override
    public void removeCurrency(OwnedCurrency ownedCurrency) {
        storageManager.removeOwnedCurrency(ownedCurrency);
    }

    @Override
    public void cashoutCurrency(OwnedCurrency ownedCurrency) {
        storageManager.cashoutOwnedCurrency(ownedCurrency);
    }
}
