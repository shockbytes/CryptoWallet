package at.shockbytes.coins.storage

import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.price.PriceConversion
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 16.06.2017.
 */

interface StorageManager {

    fun storeOwnedCurrency(currency: Currency)

    fun removeOwnedCurrency(currency: Currency)

    fun cashoutOwnedCurrency(currency: Currency)

    fun loadOwnedCurrencies(isCashedOut: Boolean): Observable<List<Currency>>

    fun updateConversionRates(c: Currency, conversions: List<PriceConversion>)

    fun getOwnedCurrencyById(id: Long): Currency

    fun partialCashout(currency: Currency, amountToPayout: Double)
}
