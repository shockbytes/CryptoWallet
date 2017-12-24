package at.shockbytes.coins.currency

import android.content.SharedPreferences
import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import at.shockbytes.coins.currency.price.PriceConversion
import at.shockbytes.coins.currency.price.PriceProxy
import at.shockbytes.coins.network.conversion.CurrencyConversionApi
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
                             private val currencyConversionApi: CurrencyConversionApi,
                             private val prefs: SharedPreferences) : CurrencyManager {


    private val PREFS_LOCAL_CURRENCY = "prefs_local_currency"
    private val PREFS_LATEST_BALANCE = "latest_balance"

    private val currencyConversionRatesAsObservable: Observable<CurrencyConversionRates>
        get() = Observable.just(CurrencyConversionRates.getDefaultCurrencyConversionRates())

    override var localCurrency: RealCurrency
        set(value) = prefs.edit().putInt(PREFS_LOCAL_CURRENCY, value.ordinal).apply()
        get() = RealCurrency.values()[prefs.getInt(PREFS_LOCAL_CURRENCY, RealCurrency.EUR.ordinal)]

    override var currencyConversionRates: CurrencyConversionRates? = null

    override val ownedCurrencies: Observable<List<Currency>>
        get() {
            val localCurrencies = storageManager.loadOwnedCurrencies(false)
            return Observable.zip(localCurrencies,
                    priceProxy.getPriceConversions(Arrays.asList(*CryptoCurrency.values()), localCurrency),
                    currencyConversionRatesAsObservable,
                    Function3<List<Currency>, List<PriceConversion>, CurrencyConversionRates, List<Currency>> { c, conversions, rates ->
                        // TODO Replace with rates call later
                        updateOwnedCurrencyConversions(c, conversions, null)
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
        }

    override val cashedOutCurrencies: Observable<List<Currency>>
        get() = Observable.zip(storageManager.loadOwnedCurrencies(true),
                currencyConversionRatesAsObservable,
                BiFunction<List<Currency>, CurrencyConversionRates, List<Currency>> { currencies, rates ->
                    // TODO Replace with rates call later
                    updateOwnedCurrencyConversions(currencies, null, null)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override var balance: Balance? = null

    override val latestBalance: Double
        get() = prefs.getFloat(PREFS_LATEST_BALANCE, 0f).toDouble()

    override fun getOwnedCurrencyById(id: Long): Currency {
        return storageManager.getOwnedCurrencyById(id)
    }

    override fun addCurrency(ownedCurrency: Currency) {
        storageManager.storeOwnedCurrency(ownedCurrency)
    }

    override fun storeLatestBalance() {
        prefs.edit()
                .putFloat(PREFS_LATEST_BALANCE, balance?.current?.toFloat() ?: 0.toFloat())
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
                                               rates: CurrencyConversionRates?): List<Currency> {

        // Get default conversion rates if rates is not present
        currencyConversionRates = rates ?: CurrencyConversionRates.getDefaultCurrencyConversionRates()

        balance = Balance()
        // Assign the conversion rates to the corresponding currencies
        for (oc in c) {
            val inv = currencyConversionRates?.convert(oc.realAmount, oc.getRealCurrency(), localCurrency) ?: 0.0
            balance?.addInvested(inv)
            if (conversions != null) {
                storageManager.updateConversionRates(oc, conversions)
            }
            balance?.addCurrent(oc.currentPrice)
        }
        return c
    }
}