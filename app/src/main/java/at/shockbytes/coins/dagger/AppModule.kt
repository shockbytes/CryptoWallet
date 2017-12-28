package at.shockbytes.coins.dagger

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.DefaultCurrencyManager
import at.shockbytes.coins.currency.conversion.CurrencyConversionProvider
import at.shockbytes.coins.currency.price.DefaultPriceProxy
import at.shockbytes.coins.currency.price.PriceProvider
import at.shockbytes.coins.currency.price.PriceProxy
import at.shockbytes.coins.storage.StorageManager
import at.shockbytes.coins.storage.realm.CryptoWatcherRealmMigration
import at.shockbytes.coins.storage.realm.RealmStorageManager
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
                               currencyConversionProvider: CurrencyConversionProvider,
                               preferences: SharedPreferences): CurrencyManager {
        return DefaultCurrencyManager(priceProxy, storageManager,
                currencyConversionProvider, preferences)
    }

    @Provides
    @Singleton
    fun providePriceProxy(@Named("CoinbasePriceProvider") coinbaseProvider: PriceProvider,
                          @Named("CoinMarketCapPriceProvider") coinMarketCapProvider: PriceProvider): PriceProxy {
        return DefaultPriceProxy(Arrays.asList(coinbaseProvider, coinMarketCapProvider))
    }

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder()
                .schemaVersion(CryptoWatcherRealmMigration.v1_0_scheme)
                .migration(CryptoWatcherRealmMigration())
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
