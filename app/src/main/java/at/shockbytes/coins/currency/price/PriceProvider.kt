package at.shockbytes.coins.currency.price

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.PriceConversion
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

interface PriceProvider {

    val providerInfo: PriceSource

    var isEnabled: Boolean

    fun getSpotPrice(from: CryptoCurrency, to: RealCurrency): Observable<PriceConversion>

    fun getBuyPrice(from: CryptoCurrency, to: RealCurrency): Observable<PriceConversion>?

    fun getSellPrice(from: CryptoCurrency, to: RealCurrency): Observable<PriceConversion>?

    fun supportedCurrencies(): List<CryptoCurrency>

}
