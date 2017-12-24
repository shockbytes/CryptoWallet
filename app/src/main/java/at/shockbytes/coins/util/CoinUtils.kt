package at.shockbytes.coins.util

import at.shockbytes.coins.R
import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */

object CoinUtils {

    fun getResourceForCryptoCurrency(currency: CryptoCurrency): Int {

        return when (currency) {

            CryptoCurrency.BTC -> R.drawable.ic_crypto_currency_btc_white
            CryptoCurrency.ETH -> R.drawable.ic_crypto_currency_eth_white
            CryptoCurrency.LTC -> R.drawable.ic_crypto_currency_ltc_white
            CryptoCurrency.BCH -> R.drawable.ic_crypto_currency_bch_white
        }
    }

    fun getResourceForCurrency(realCurrency: RealCurrency): Int {

        return when (realCurrency) {

            RealCurrency.EUR -> R.drawable.ic_currency_eur
            RealCurrency.USD -> R.drawable.ic_currency_usd
        }
    }

    fun getSymbolForCurrency(realCurrency: RealCurrency): String {

        return when (realCurrency) {

            RealCurrency.EUR -> "€"
            RealCurrency.USD -> "$"
        }
        /*
        case GBP:
        return "GBP";
        case CHF:
        return "CHF";
        */
    }


}