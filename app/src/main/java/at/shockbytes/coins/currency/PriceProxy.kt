package at.shockbytes.coins.currency

import at.shockbytes.coins.network.model.PriceConversion
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 24.06.2017.
 */

interface PriceProxy {

    fun getPriceConversions(from: List<CryptoCurrency>,
                            to: Currency): Observable<List<PriceConversion>>

}
