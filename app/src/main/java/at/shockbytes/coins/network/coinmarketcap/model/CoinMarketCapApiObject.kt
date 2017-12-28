package at.shockbytes.coins.network.coinmarketcap.model

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.PriceConversion
import at.shockbytes.coins.currency.conversion.PriceConversionConvertible

/**
 * @author Martin Macheiner
 * Date: 25.12.2017.
 */

class CoinMarketCapApiObject : PriceConversionConvertible {

    var symbol: String = ""

    var price_usd: Double = 0.0
    var price_eur: Double = 0.0
    var price_gbp: Double = 0.0
    var price_chf: Double = 0.0

    // Enable in later release, when those currencies are supported
    // var price_chf: Double = 0.0
    // var price_gbp: Double = 0.0

    override fun asPriceConversion(to: RealCurrency): PriceConversion {

        val pc = PriceConversion()
        pc.cryptoCurrency = CryptoCurrency.valueOf(symbol)
        pc.fillDataExternal(getAmountForRealCurrency(to), to.name)

        return pc
    }

    private fun getAmountForRealCurrency(to: RealCurrency): Double {
        return when (to) {

            RealCurrency.EUR -> price_eur
            RealCurrency.USD -> price_usd
            RealCurrency.GBP -> price_gbp
            RealCurrency.CHF -> price_chf
        }
    }
}