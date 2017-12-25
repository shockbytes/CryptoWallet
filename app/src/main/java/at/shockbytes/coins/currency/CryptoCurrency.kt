package at.shockbytes.coins.currency

import at.shockbytes.coins.R

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

enum class CryptoCurrency {
    BTC {
        override val fullName = "bitcoin"
        override val icon = R.drawable.ic_crypto_currency_btc
    },
    ETH {
        override val fullName = "ethereum"
        override val icon = R.drawable.ic_crypto_currency_eth
    },
    LTC {
        override val fullName = "litecoin"
        override val icon = R.drawable.ic_crypto_currency_ltc
    },
    BCH {
        override val fullName = "bitcoin-cash"
        override val icon = R.drawable.ic_crypto_currency_bch
    },
    MIOTA {
        override val fullName = "iota"
        override val icon = R.drawable.ic_crypto_currency_iota
    },
    DASH {
        override val fullName = "dash"
        override val icon = R.drawable.ic_crypto_currency_dash
    },
    XRP {
        override val fullName = "ripple"
        override val icon = R.drawable.ic_crypto_currency_xrp
    };

    abstract val fullName: String
    abstract val icon: Int

}
