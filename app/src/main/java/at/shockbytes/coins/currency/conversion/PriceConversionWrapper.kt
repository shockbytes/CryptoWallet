package at.shockbytes.coins.currency.conversion

import at.shockbytes.coins.currency.CryptoCurrency
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

class PriceConversionWrapper(var conversions: List<Observable<PriceConversion>>,
                             var from: List<CryptoCurrency>)
