package at.shockbytes.coins.currency.conversion

import at.shockbytes.coins.currency.RealCurrency

/**
 * @author Martin Macheiner
 * Date: 01.07.2017.
 */

class CurrencyConversionRates {

    private var base: String = ""
    private var date: String = ""

    private var rates: Rates = Rates().defaultInitialize()

    fun convert(value: Double, from: RealCurrency, to: RealCurrency): Double {

        return when {
            from === to -> value // Do nothing in this case
            from.name == base -> value * getRateForCurrency(to) // Everything is straightforward
            else -> value / getRateForCurrency(from) // A conversion from the requested to the base currency
        }

    }

    private fun getRateForCurrency(currency: RealCurrency): Double {

        return when (currency) {

            RealCurrency.EUR -> rates.EUR
            RealCurrency.USD -> rates.USD
        }
    }

    override fun toString(): String {
        return "Base: " + base + "\nDate: " + date + "\n" + rates.toString()
    }

    internal class Rates {

        var CHF: Double = 0.0
        var GBP: Double = 0.0
        var EUR: Double = 0.0
        var USD: Double = 0.0

        override fun toString(): String {
            return "To CHF: $CHF\nTo GBP: $GBP\nTo EUR: $EUR\nTo USD: $USD"
        }

        fun defaultInitialize(): Rates {
            USD = 1.0
            EUR = USD
            GBP = EUR
            CHF = GBP
            return this
        }
    }

    companion object {

        val defaultCurrencyConversionRates: CurrencyConversionRates
            get() {

                val def = CurrencyConversionRates()
                def.base = "EUR"
                def.date = ""
                def.rates = Rates().defaultInitialize()

                return def
            }
    }

}
