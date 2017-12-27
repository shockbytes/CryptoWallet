package at.shockbytes.coins.storage.realm

import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.conversion.PriceConversion
import at.shockbytes.coins.storage.StorageManager
import at.shockbytes.coins.storage.realm.model.ShockConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort

/**
 * @author Martin Macheiner
 * Date: 16.06.2017.
 */

class RealmStorageManager(private val realm: Realm) : StorageManager {

    // This property can only be accessed inside a transaction
    private val lastPrimaryKey: Long
        get() {
            var config = realm.where(ShockConfig::class.java).findFirst()
            if (config == null) {
                config = realm.createObject(ShockConfig::class.java)
            }
            return config?.getLastPrimaryKey()!!
        }

    override fun storeOwnedCurrency(currency: Currency) {

        realm.beginTransaction()
        currency.id = lastPrimaryKey
        realm.copyToRealmOrUpdate(currency)
        realm.commitTransaction()
    }

    override fun removeOwnedCurrency(currency: Currency) {

        realm.beginTransaction()
        currency.deleteFromRealm()
        realm.commitTransaction()
    }

    override fun cashoutOwnedCurrency(currency: Currency) {

        realm.beginTransaction()
        currency.instanceCashedOut = true
        currency.cashoutDate = System.currentTimeMillis()
        realm.commitTransaction()
    }

    override fun loadOwnedCurrencies(isCashedOut: Boolean): Observable<List<Currency>> {
        val stored = realm.where(Currency::class.java)
                .equalTo("instanceCashedOut", isCashedOut)
                .findAllAsync()
                .sort("id", Sort.DESCENDING)
        return Observable.just<List<Currency>>(stored)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun updateConversionRate(c: Currency, conversions: List<PriceConversion>) {

        realm.beginTransaction()

        conversions
                .filter { c.getCryptoCurrency() === it.cryptoCurrency }
                .forEach {
                    c.conversionRate = it.conversionRate
                    c.priceSource = realm.copyToRealmOrUpdate(it.priceSource)
                }
        realm.commitTransaction()
    }

    override fun getOwnedCurrencyById(id: Long): Currency {
        return realm.where(Currency::class.java).equalTo("id", id).findFirst()!!
    }

    override fun partialCashout(currency: Currency, amountToPayout: Double) {

        val cashoutRealAmount = currency.realAmount * (amountToPayout / currency.cryptoAmount)
        val leftRealAmount = currency.realAmount - cashoutRealAmount
        val leftCryptoAmount = currency.cryptoAmount - amountToPayout

        realm.beginTransaction()

        // Update old currency
        currency.cryptoAmount = leftCryptoAmount
        currency.realAmount = leftRealAmount
        realm.copyToRealmOrUpdate(currency)

        // Create and store a cashout instance
        val cashedOutInstance = Currency(lastPrimaryKey, currency.getCryptoCurrency(),
                currency.getCryptoCurrency().ordinal, amountToPayout, currency.getRealCurrency(),
                currency.getRealCurrency().ordinal, cashoutRealAmount, true,
                currency.conversionRate, currency.priceSource,
                System.currentTimeMillis(), System.currentTimeMillis())
        cashedOutInstance.instanceCashedOut = true
        realm.copyToRealm(cashedOutInstance)

        realm.commitTransaction()
    }
}
