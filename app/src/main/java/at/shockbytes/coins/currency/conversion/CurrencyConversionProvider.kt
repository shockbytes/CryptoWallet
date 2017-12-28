package at.shockbytes.coins.currency.conversion

import at.shockbytes.coins.currency.RealCurrency
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 28.12.2017.
 */

interface CurrencyConversionProvider {

    val providerName: String

    fun poke()

    fun getCurrencyConversionRates(): Observable<CurrencyConversionRates>

    fun convert(value: Double, from: RealCurrency, to: RealCurrency): Double

}