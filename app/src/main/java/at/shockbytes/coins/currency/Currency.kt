package at.shockbytes.coins.currency

import at.shockbytes.coins.currency.price.PriceSource
import at.shockbytes.coins.util.ResourceManager
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * @author Martin Macheiner
 * Date: 24.12.2017.
 */

open class Currency(@PrimaryKey var id: Long = -1,
                    @Ignore private var _cryptoCurrency: CryptoCurrency = CryptoCurrency.BTC,
                    private var cryptoCurrencyOrdinal: Int = _cryptoCurrency.ordinal,
                    var cryptoAmount: Double = 0.0,
                    @Ignore private var _realCurrency: RealCurrency = RealCurrency.EUR,
                    private var realCurrencyOrdinal: Int = _realCurrency.ordinal,
                    var realAmount: Double = 0.0,
                    var instanceCashedOut: Boolean = false,
                    var conversionRate: Double = 0.0,
                    var priceSource: PriceSource? = null,
                    var boughtDate: Long = 0,
                    var cashoutDate: Long = 0) : RealmObject() {

    val currentPrice: Double
        get() {
            return if (conversionRate != 0.0) {
                ResourceManager.roundDouble(cryptoAmount * conversionRate, 2)
            } else {
                -1.0
            }
        }

    fun getPricePercentageDiff(boughtPrice: Double): Double {
        val diff = currentPrice / (boughtPrice / 100) - 100
        return ResourceManager.roundDouble(diff, 2)
    }

    fun getCryptoCurrency(): CryptoCurrency {
        return CryptoCurrency.values()[cryptoCurrencyOrdinal]
    }

    fun getRealCurrency(): RealCurrency {
        return RealCurrency.values()[realCurrencyOrdinal]
    }

    override fun equals(other: Any?): Boolean {

        return if (other is Currency) {
            (id == other.id)
        } else {
            false
        }
    }

}