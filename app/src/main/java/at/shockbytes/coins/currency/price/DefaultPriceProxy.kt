package at.shockbytes.coins.currency.price

import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.PriceConversion
import at.shockbytes.coins.currency.conversion.PriceConversionWrapper
import io.reactivex.Observable
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 24.06.2017.
 */

class DefaultPriceProxy(private val priceProviders: List<PriceProvider>) : PriceProxy {

    override fun getPriceConversions(from: List<CryptoCurrency>,
                                     to: RealCurrency): Observable<List<PriceConversion>> {

        val wrapper = getConversionObservables(from, to)
        return Observable.zip(wrapper.conversions) { t ->
            t.mapIndexed { idx, m ->
                val pc = m as PriceConversion
                pc.cryptoCurrency = wrapper.from[idx]
                pc
            }
        }
    }

    private fun getConversionObservables(from: List<CryptoCurrency>,
                                         to: RealCurrency): PriceConversionWrapper {

        val conversionObservables = ArrayList<Observable<PriceConversion>>()
        val conversionCurrencies = ArrayList<CryptoCurrency>()
        for (currency in from) {
            // TODO Only support 1 conversion of each currency at a time and add PriceSource to conversion
            for (manager in priceProviders) {
                if (manager.supportsCurrencyConversion(currency)) {
                    conversionObservables.add(manager.getSpotPrice(currency, to))
                    conversionCurrencies.add(currency)
                    break
                }
            }
        }
        return PriceConversionWrapper(conversionObservables, conversionCurrencies)
    }

}
