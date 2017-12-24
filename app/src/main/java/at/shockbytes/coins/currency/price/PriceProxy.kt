package at.shockbytes.coins.currency.price

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 24.06.2017.
 */

interface PriceProxy {

    fun getPriceConversions(from: List<CryptoCurrency>,
                            to: RealCurrency): Observable<List<PriceConversion>>

}
