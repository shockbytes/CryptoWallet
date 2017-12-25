package at.shockbytes.coins.currency.conversion

import at.shockbytes.coins.currency.CryptoCurrency

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

    var cryptoCurrency: CryptoCurrency? = null
    var data: Data = Data()

    val to: String?
        get() = data.currency

    val conversionRate: Double
        get() = data.amount

    inner class Data {

        var amount: Double = 0.toDouble()
        var currency: String? = null

    }

}
