package at.shockbytes.coins.network.coinbase

import at.shockbytes.coins.R
import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.network.PriceManager
import at.shockbytes.coins.network.model.PriceConversion
import at.shockbytes.coins.network.model.PriceManagerInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

class CoinbasePriceManager(private val api: CoinbasePriceApi) : PriceManager {

    override val info = PriceManagerInfo("Coinbase", R.drawable.ic_price_provider_coinbase)

    override var isEnabled: Boolean = true


    override fun getSpotPrice(from: CryptoCurrency, to: Currency): Observable<PriceConversion> {
        return api.getSpotPrice(buildConversionPath(from, to), buildTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun getBuyPrice(from: CryptoCurrency, to: Currency): Observable<PriceConversion> {
        return api.getBuyPrice(buildConversionPath(from, to), buildTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun getSellPrice(from: CryptoCurrency, to: Currency): Observable<PriceConversion> {
        return api.getSellPrice(buildConversionPath(from, to), buildTimestamp())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun supportsCurrencyConversion(currency: CryptoCurrency): Boolean {
        return Arrays.asList(CryptoCurrency.BTC, CryptoCurrency.ETH,
                CryptoCurrency.LTC, CryptoCurrency.BCH)
                .contains(currency)
    }

    private fun buildConversionPath(from: CryptoCurrency, to: Currency): String {
        return from.name + "-" + to.name
    }

    private fun buildTimestamp(): String? {
        return null
    }

}
