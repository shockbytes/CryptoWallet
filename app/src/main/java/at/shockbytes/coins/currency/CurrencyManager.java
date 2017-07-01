package at.shockbytes.coins.currency;

import java.util.List;

import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public interface CurrencyManager {

    void setLocalCurrency(Currency currency);

    Currency getLocalCurrency();

    CurrencyConversionRates getCurrencyConversionRates();

    Observable<List<OwnedCurrency>> getOwnedCurrencies();

    Observable<List<OwnedCurrency>> getCashedoutCurrencies();

    void addOwnedCurrency(OwnedCurrency ownedCurrency);

    Balance getBalance();

    void removeCurrency(OwnedCurrency ownedCurrency);

    void cashoutCurrency(OwnedCurrency ownedCurrency);
}
