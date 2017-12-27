package at.shockbytes.coins.currency.price

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.PriceConversion
import at.shockbytes.coins.currency.conversion.PriceConversionWrapper
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 24.06.2017.
 */

class DefaultPriceProxy(override val priceProvider: List<PriceProvider>) : PriceProxy {

    override fun getPriceConversions(from: List<CryptoCurrency>,
                                     to: RealCurrency): Observable<List<PriceConversion>> {

        return if (priceProvider.none { it.isEnabled }) {
            Observable.error(NoPriceProviderSelectedException())
        } else {
            val wrapper = getConversionObservables(from, to)
            Observable.zip(wrapper.conversions) { t ->
                t.mapIndexed { idx, m ->
                    val pc = m as PriceConversion; pc.cryptoCurrency = wrapper.from[idx]; pc
                }
            }
        }
    }

    override fun getSupportedCryptoCurrencies(): List<CryptoCurrency> {

        return CryptoCurrency.values().filter {
            priceProvider
                    .filter { p -> p.supportedCurrencies().contains(it) and p.isEnabled }
                    .forEach { return@filter true }
            false
        }
    }

    private fun getConversionObservables(from: List<CryptoCurrency>,
                                         to: RealCurrency): PriceConversionWrapper {

        val conversionObservables = ArrayList<Observable<PriceConversion>>()
        val conversionCurrencies = ArrayList<CryptoCurrency>()
        for (currency in from) {

            priceProvider
                    .filter { it.isEnabled }
                    .forEach {
                        if (it.supportedCurrencies().contains(currency)) {
                            conversionObservables.add(it.getSpotPrice(currency, to))
                            conversionCurrencies.add(currency)
                            return@forEach
                        }
                    }
        }
        return PriceConversionWrapper(conversionObservables, conversionCurrencies)
    }

}
