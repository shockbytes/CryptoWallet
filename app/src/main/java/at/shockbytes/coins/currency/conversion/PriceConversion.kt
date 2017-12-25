package at.shockbytes.coins.currency.conversion

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.price.PriceSource

/**
 *
 * @author Martin Macheiner
 * Date: 14.06.2017
 *
 * This is the base class for all PriceConversions between RealCurrency and CryptoCurrency
 * This is also the class for the Coinbase Api, other ApiProvider data classes have to
 * implement PriceConversionConvertible in order to be compatible with the application
 *
 */

class PriceConversion {

    // This data will be filled later on
    var cryptoCurrency: CryptoCurrency? = null
    // Default initialize variable, will be replaced in Api calls
    var priceSource: PriceSource = PriceSource("Default", 0)

    // This data will be filled by Retrofit
    var data: Data = Data()

    val to: String?
        get() = data.currency

    val conversionRate: Double
        get() = data.amount

    fun fillDataExternal(amount: Double, currency: String) {
        data.amount = amount
        data.currency = currency
    }

    inner class Data {

        var amount: Double = 0.toDouble()
        var currency: String? = null

    }

}
