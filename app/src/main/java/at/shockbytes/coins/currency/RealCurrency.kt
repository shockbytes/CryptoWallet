package at.shockbytes.coins.currency

import at.shockbytes.coins.R

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

enum class RealCurrency {

    EUR {
        override val symbol = "€"
        override val icon = R.drawable.ic_real_currency_eur
    },
    USD {
        override val symbol = "$"
        override val icon = R.drawable.ic_real_currency_usd
    };
    /*,
    GBP,
    CHF*/

    abstract val symbol: String
    abstract val icon: Int


}
