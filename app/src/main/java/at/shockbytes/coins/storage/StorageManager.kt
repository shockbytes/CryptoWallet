package at.shockbytes.coins.storage

import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.conversion.PriceConversion
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 16.06.2017.
 */

interface StorageManager {

    fun storeCurrency(currency: Currency)

    fun removeCurrency(currency: Currency)

    fun cashoutCurrency(currency: Currency)

    fun loadCurrencies(isCashedOut: Boolean): Observable<List<Currency>>

    fun updateConversionRate(c: Currency, conversions: List<PriceConversion>)

    fun updateBoughtDateCurrency(currency: Currency, boughtDate: Long)

    fun getOwnedCurrencyById(id: Long): Currency

    fun partialCashout(currency: Currency, amountToPayout: Double)
}
