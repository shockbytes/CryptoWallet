package at.shockbytes.coins.network.conversion

import android.content.SharedPreferences
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.CurrencyConversionProvider
import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author Martin Macheiner
 * Date: 28.12.2017.
 *
 * Reload currency prices every week. These conversion rates do not change
 * so fast over time and this isn't a #1 feature.
 *
 */

class FixerIoCurrencyConversionProvider(private val api: FixerIoCurrencyConversionApi,
                                        private val prefs: SharedPreferences) : CurrencyConversionProvider {

    override val providerName: String = "fixer.io"

    private val prefsValidityTime = "prefs_arg_validity_date"
    private val prefsLocalCurrency = "prefs_local_currency"
    private val prefsConversionRates = "prefs_conversion_rates"

    private val validityPeriod = 604800000L // 1 week of validity

    private val gson = Gson()
    private var rates: CurrencyConversionRates = CurrencyConversionRates.defaultCurrencyConversionRates

    override fun poke() {
        loadFromApi()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { rates = storeRatesInPreferences(it); updateValidityTime() }
    }

    override fun getCurrencyConversionRates(): Observable<CurrencyConversionRates> {
        return Observable.defer {
            if (isCacheValid()) {
                if (rates.providerName == "DEFAULT") {
                    rates = loadRatesFromPreferences()
                }
            } else {
                rates = loadFromApiAndStore()
            }
            Observable.just(rates)
        }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
    }

    override fun convert(value: Double, from: RealCurrency, to: RealCurrency): Double {
        return rates.convert(value, from, to)
    }

    // -----------------------------------------------------------------------------------------

    private fun loadFromApi(): Observable<CurrencyConversionRates> {
        val lc = getLocalCurrency()
        return api.getCurrencyConversionRates(lc.name, getSymbols(lc))
                .map { it.providerName = providerName; it } // Set provider name each fetch
    }

    private fun loadFromApiAndStore(): CurrencyConversionRates {
        val lc = getLocalCurrency()
        return storeRatesInPreferences(api.getCurrencyConversionRates(lc.name, getSymbols(lc))
                .map { it.providerName = providerName; it } // Set provider name each fetch
                .blockingFirst())
    }

    private fun isCacheValid(): Boolean {

        var validityTime = prefs.getLong(prefsValidityTime, -1L)

        // First access of method
        return if (validityTime == -1L) {
            validityTime = System.currentTimeMillis().plus(validityPeriod)
            prefs.edit().putLong(prefsValidityTime, validityTime).apply()
            false
        } else {
            val now = System.currentTimeMillis()
            val isValid = (validityTime.minus(now) > 0)
            if (!isValid) { // Update validityTime if it's no longer valid
                updateValidityTime()
            }
            isValid
        }
    }

    private fun storeRatesInPreferences(r: CurrencyConversionRates): CurrencyConversionRates {
        prefs.edit().putString(prefsConversionRates, gson.toJson(r)).apply()
        return r
    }

    private fun loadRatesFromPreferences(): CurrencyConversionRates {
        val ratesAsString = prefs.getString(prefsConversionRates, null)
        if (ratesAsString != null) {
            rates = gson.fromJson(ratesAsString, CurrencyConversionRates::class.java)
        } else {
            poke() // Cannot load from preferences, poke to refresh rates
        }
        return rates
    }

    private fun getLocalCurrency(): RealCurrency {
        return RealCurrency.values()[prefs.getInt(prefsLocalCurrency, RealCurrency.USD.ordinal)]
    }

    private fun getSymbols(lc: RealCurrency): String {
        return RealCurrency.values().filter { it != lc }.joinToString(",")
    }

    private fun updateValidityTime() {
        val newValidityTime = System.currentTimeMillis().plus(validityPeriod)
        prefs.edit().putLong(prefsValidityTime, newValidityTime).apply()
    }

}