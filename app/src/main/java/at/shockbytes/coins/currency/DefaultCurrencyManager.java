package at.shockbytes.coins.currency;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.coins.network.conversion.CurrencyConversionApi;
import at.shockbytes.coins.network.model.PriceConversion;
import at.shockbytes.coins.storage.StorageManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class DefaultCurrencyManager implements CurrencyManager {

    private PriceProxy priceProxy;
    private StorageManager storageManager;
    private SharedPreferences prefs;

    private CurrencyConversionApi currencyConversionApi;
    private CurrencyConversionRates conversionRates;

    private String PREFS_LOCAL_CURRENCY = "prefs_local_currency";
    private String PREFS_LATEST_BALANCE = "latest_balance";

    private Balance balance;

    @Inject
    public DefaultCurrencyManager(Context context, PriceProxy priceProxy,
                                  StorageManager storageManager,
                                  CurrencyConversionApi currencyConversionApi,
                                  SharedPreferences prefs) {
        this.priceProxy = priceProxy;
        this.storageManager = storageManager;
        this.currencyConversionApi = currencyConversionApi;
        this.prefs = prefs;
    }

    @Override
    public void setLocalCurrency(Currency currency) {
        prefs.edit().putInt(PREFS_LOCAL_CURRENCY, currency.ordinal()).apply();
    }

    @Override
    public Currency getLocalCurrency() {
        return Currency.values()[prefs.getInt(PREFS_LOCAL_CURRENCY, Currency.EUR.ordinal())];
    }


    private Observable<CurrencyConversionRates> getCurrencyConversionRatesAsObservable() {
        //return currencyConversionApi.getCurrencyConversionRates("USD", "EUR,GBP,CHF");
        return Observable.just(CurrencyConversionRates.getDefaultCurrencyConversionRates());
    }

    @Override
    public CurrencyConversionRates getCurrencyConversionRates() {
        return conversionRates;
    }

    @Override
    public Observable<List<OwnedCurrency>> getOwnedCurrencies() {

        final Observable<List<OwnedCurrency>> localCurrencies = storageManager.loadOwnedCurrencies(false);

        return Observable.zip(localCurrencies,
                priceProxy.getPriceConversions(Arrays.asList(CryptoCurrency.values()), getLocalCurrency()),
                getCurrencyConversionRatesAsObservable(),
                new Func3<List<OwnedCurrency>, List<PriceConversion>, CurrencyConversionRates,
                        List<OwnedCurrency>>() {
                    @Override
                    public List<OwnedCurrency> call(List<OwnedCurrency> c,
                                                    List<PriceConversion> conversions,
                                                    CurrencyConversionRates rates) {
                        return updateOwnedCurrencyConversions(c, conversions, null); // TODO Replace with call later
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public OwnedCurrency getOwnedCurrencyById(long id) {
        return storageManager.getOwnedCurrencyById(id);
    }

    @Override
    public Observable<List<OwnedCurrency>> getCashedoutCurrencies() {
        return Observable.zip(storageManager.loadOwnedCurrencies(true),
                getCurrencyConversionRatesAsObservable(),
                new Func2<List<OwnedCurrency>, CurrencyConversionRates, List<OwnedCurrency>>() {
                    @Override
                    public List<OwnedCurrency> call(List<OwnedCurrency> currencies,
                                                    CurrencyConversionRates rates) {
                        return updateOwnedCurrencyConversions(currencies, null, null); // TODO Replace with call later
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
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
    public void storeLatestBalance() {
        if (balance != null) {
            prefs.edit().putFloat(PREFS_LATEST_BALANCE, (float) balance.getCurrent()).apply();
        }
    }

    @Override
    public double getLatestBalance() {
        return prefs.getFloat(PREFS_LATEST_BALANCE, 0);
    }

    @Override
    public void removeCurrency(OwnedCurrency ownedCurrency) {
        storageManager.removeOwnedCurrency(ownedCurrency);
    }

    @Override
    public void cashoutCurrency(OwnedCurrency ownedCurrency) {
        storageManager.cashoutOwnedCurrency(ownedCurrency);
    }

    @Override
    public void splitCashout(OwnedCurrency currency, double amountToPayout) {
        storageManager.splitCashout(currency, amountToPayout);
    }

    private List<OwnedCurrency> updateOwnedCurrencyConversions (List<OwnedCurrency> c,
                                                                List<PriceConversion> conversions,
                                                                CurrencyConversionRates rates) {

        // Get default conversion rates if rates is not present
        if (rates == null) {
            rates = CurrencyConversionRates.getDefaultCurrencyConversionRates();
        }
        conversionRates = rates;

        balance = new Balance();
        // Assign the conversion rates to the corresponding currencies
        for (OwnedCurrency oc : c) {
            double inv = rates.convert(oc.getBoughtPrice(), oc.getBoughtCurrency(),
                    getLocalCurrency());
            balance.addInvested(inv);
            if (conversions != null) {
                storageManager.updateConversionRates(oc, conversions);
            }
            balance.addCurrent(oc.getCurrentPrice());
        }
        return c;
    }
}
