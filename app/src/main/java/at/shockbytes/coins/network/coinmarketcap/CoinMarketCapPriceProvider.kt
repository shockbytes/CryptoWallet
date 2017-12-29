package at.shockbytes.coins.network.coinmarketcap

import android.content.SharedPreferences
import at.shockbytes.coins.R
import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.PriceConversion
import at.shockbytes.coins.currency.price.PriceProvider
import at.shockbytes.coins.currency.price.PriceSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 25.12.2017.
 */
class CoinMarketCapPriceProvider(private val api: CoinMarketCapPriceApi,
                                 private val prefs: SharedPreferences) : PriceProvider {

    override val providerInfo = PriceSource("CoinMarketCap",
            R.drawable.ic_price_provider_coinmarketcap)

    override var isEnabled: Boolean
        get() = prefs.getBoolean(argEnabled, true)
        set(value) = prefs.edit().putBoolean(argEnabled, value).apply()

    override fun getSpotPrice(from: CryptoCurrency, to: RealCurrency): Observable<PriceConversion> {
        return api.getSpotPrice(from.fullName, to.name)
                // Convert to PriceConversion object and set priceSource here
                .map{ val pc = it[0].asPriceConversion(to); pc.priceSource = providerInfo; pc }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun supportedCurrencies(): List<CryptoCurrency> {
        return Arrays.asList(CryptoCurrency.MIOTA, CryptoCurrency.DASH, CryptoCurrency.XRP)
    }

    companion object {

        val argEnabled = "arg_coinmarketcap_price_provider_is_enabled"

    }

}