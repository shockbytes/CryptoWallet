package at.shockbytes.coins.currency

import at.shockbytes.coins.network.PriceManager
import at.shockbytes.coins.network.model.PriceConversion
import at.shockbytes.coins.network.model.PriceConversionWrapper
import io.reactivex.Observable
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 24.06.2017.
 */

class DefaultPriceProxy(private val priceManagers: List<PriceManager>) : PriceProxy {

    override fun getPriceConversions(from: List<CryptoCurrency>,
                            to: Currency): Observable<List<PriceConversion>> {

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
                                         to: Currency): PriceConversionWrapper {

        val conversionObservables = ArrayList<Observable<PriceConversion>>()
        val conversionCurrencies = ArrayList<CryptoCurrency>()
        for (currency in from) {
            // TODO Only support 1 conversion of each currency at a time
            for (manager in priceManagers) {
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
