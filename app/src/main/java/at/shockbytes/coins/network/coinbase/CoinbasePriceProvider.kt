package at.shockbytes.coins.network.coinbase

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
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

class CoinbasePriceProvider(private val api: CoinbasePriceApi,
                            private val prefs: SharedPreferences) : PriceProvider {

    override val providerInfo = PriceSource("Coinbase", R.drawable.ic_price_provider_coinbase)

    override var isEnabled: Boolean
        get() = prefs.getBoolean(argEnabled, true)
        set(value) = prefs.edit().putBoolean(argEnabled, value).apply()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    override fun getSpotPrice(from: CryptoCurrency, to: RealCurrency): Observable<PriceConversion> {
        return api.getSpotPrice(buildConversionPath(from, to), buildTimestamp())
                .map { it.priceSource = providerInfo; it } // <-- Set the PriceSource here
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun supportedCurrencies(): List<CryptoCurrency> {
        return Arrays.asList(CryptoCurrency.BTC, CryptoCurrency.ETH, CryptoCurrency.LTC,
                CryptoCurrency.BCH)
    }

    private fun buildConversionPath(from: CryptoCurrency, to: RealCurrency): String {
        return from.name + "-" + to.name
    }

    private fun buildTimestamp(): String {
        return sdf.format(Date(System.currentTimeMillis()))
    }


    companion object {

        val argEnabled = "arg_coinbase_price_provider_is_enabled"

    }

}
