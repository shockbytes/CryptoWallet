package at.shockbytes.coins.dagger

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.DefaultCurrencyManager
import at.shockbytes.coins.currency.DefaultPriceProxy
import at.shockbytes.coins.currency.PriceProxy
import at.shockbytes.coins.network.PriceManager
import at.shockbytes.coins.network.conversion.CurrencyConversionApi
import at.shockbytes.coins.storage.CoinsRealmMigration
import at.shockbytes.coins.storage.RealmStorageManager
import at.shockbytes.coins.storage.StorageManager
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */
@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideCurrencyManager(priceProxy: PriceProxy,
                               storageManager: StorageManager,
                               currencyConversionApi: CurrencyConversionApi,
                               preferences: SharedPreferences): CurrencyManager {
        return DefaultCurrencyManager(app.applicationContext, priceProxy,
                storageManager, currencyConversionApi, preferences)
    }

    @Provides
    @Singleton
    fun providePriceProxy(@Named("CoinbasePriceManager") priceManager: PriceManager): PriceProxy {
        return DefaultPriceProxy(Arrays.asList(priceManager))
    }

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder()
                .schemaVersion(CoinsRealmMigration.CURRENT_SCHEME.toLong())
                .migration(CoinsRealmMigration())
                //.encryptionKey(key)
                .build()
        return Realm.getInstance(config)
    }

    @Provides
    @Singleton
    fun provideStorageManager(realm: Realm): StorageManager {
        return RealmStorageManager(realm)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    }

}
