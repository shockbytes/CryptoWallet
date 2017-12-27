package at.shockbytes.coins.currency

import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 15.06.2017.
 */

interface CurrencyManager {

    var localCurrency: RealCurrency

    var currencyConversionRates: CurrencyConversionRates?

    val ownedCurrencies: Observable<List<Currency>>

    val cashedOutCurrencies: Observable<List<Currency>>

    var balance: Balance?

    val latestBalance: Double

    fun getCurrencyById(id: Long): Currency

    fun addCurrency(ownedCurrency: Currency)

    fun storeLatestBalance()

    fun removeCurrency(ownedCurrency: Currency)

    fun cashoutCurrency(ownedCurrency: Currency)

    fun partialCashout(currency: Currency, amountToPayout: Double)
}
