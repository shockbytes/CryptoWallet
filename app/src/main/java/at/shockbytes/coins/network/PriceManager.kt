package at.shockbytes.coins.network

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.network.model.PriceConversion
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

interface PriceManager {

    val name: String

    val icon: Int

    var isEnabled: Boolean

    fun getSpotPrice(from: CryptoCurrency, to: Currency): Observable<PriceConversion>

    fun getBuyPrice(from: CryptoCurrency, to: Currency): Observable<PriceConversion>

    fun getSellPrice(from: CryptoCurrency, to: Currency): Observable<PriceConversion>

    fun supportsCurrencyConversion(currency: CryptoCurrency): Boolean

}
