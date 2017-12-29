package at.shockbytes.coins.currency

import android.content.SharedPreferences
import at.shockbytes.coins.currency.conversion.CurrencyConversionProvider
import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import at.shockbytes.coins.currency.conversion.PriceConversion
import at.shockbytes.coins.currency.price.PriceProxy
import at.shockbytes.coins.storage.StorageManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * @author Martin Macheiner
 * Date: 15.06.2017.
 */

class DefaultCurrencyManager(private val priceProxy: PriceProxy,
                             private val storageManager: StorageManager,
                             override val currencyConversionProvider: CurrencyConversionProvider,
                             private val prefs: SharedPreferences) : CurrencyManager {

    private val prefsLocalCurrency = "prefs_local_currency"
    private val prefsLatestBalance = "latest_balance"

    override var localCurrency: RealCurrency
        set(value) {
            // Check before setting value, otherwise this would have no effect
            val poking = value.ordinal != prefs.getInt(prefsLocalCurrency, RealCurrency.USD.ordinal)
            prefs.edit().putInt(prefsLocalCurrency, value.ordinal).apply()
            // Automatically reload currency conversion rates if local currency changes
            if (poking) {
                currencyConversionProvider.poke()
            }
        }
        get() = RealCurrency.values()[prefs.getInt(prefsLocalCurrency, RealCurrency.USD.ordinal)]

    override val ownedCurrencies: Observable<List<Currency>>
        get() {
            val localCurrencies = storageManager.loadOwnedCurrencies(false)
            return Observable.zip(localCurrencies,
                    priceProxy.getPriceConversions(Arrays.asList(*CryptoCurrency.values()), localCurrency),
                    currencyConversionProvider.getCurrencyConversionRates(),
                    Function3<List<Currency>, List<PriceConversion>, CurrencyConversionRates, List<Currency>> { c, conversions, rates ->
                        updateOwnedCurrencyConversions(c, conversions, rates)
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
        }

    override val cashedOutCurrencies: Observable<List<Currency>>
        get() = Observable.zip(storageManager.loadOwnedCurrencies(true),
                currencyConversionProvider.getCurrencyConversionRates(),
                BiFunction<List<Currency>, CurrencyConversionRates, List<Currency>> { currencies, rates ->
                    updateOwnedCurrencyConversions(currencies, null, rates)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override var balance: Balance = Balance()

    override val latestBalance: Double
        get() = prefs.getFloat(prefsLatestBalance, 0f).toDouble()

    override fun getCurrencyById(id: Long): Currency {
        return storageManager.getOwnedCurrencyById(id)
    }

    override fun addCurrency(ownedCurrency: Currency) {
        storageManager.storeOwnedCurrency(ownedCurrency)
    }

    override fun storeLatestBalance() {
        prefs.edit()
                .putFloat(prefsLatestBalance, balance.current.toFloat())
                .apply()
    }

    override fun removeCurrency(ownedCurrency: Currency) {
        storageManager.removeOwnedCurrency(ownedCurrency)
    }

    override fun cashoutCurrency(ownedCurrency: Currency) {
        storageManager.cashoutOwnedCurrency(ownedCurrency)
    }

    override fun partialCashout(currency: Currency, amountToPayout: Double) {
        storageManager.partialCashout(currency, amountToPayout)
    }

    private fun updateOwnedCurrencyConversions(c: List<Currency>,
                                               conversions: List<PriceConversion>?,
                                               currencyRates: CurrencyConversionRates): List<Currency> {

        balance.clear()
        // Assign the conversion currencyRates to the corresponding currencies
        for (oc in c) {
            val inv = currencyRates.convert(oc.realAmount, oc.getRealCurrency(), localCurrency)
            balance.addInvested(inv)
            if (conversions != null) {
                storageManager.updateConversionRate(oc, conversions)
            }
            balance.addCurrent(oc.currentPrice)
        }
        return c
    }
}
