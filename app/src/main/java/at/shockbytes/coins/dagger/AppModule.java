package at.shockbytes.coins.dagger;

import android.app.Application;

import javax.inject.Named;
import javax.inject.Singleton;

import at.shockbytes.coins.currency.CurrencyManager;
import at.shockbytes.coins.currency.DefaultCurrencyManager;
import at.shockbytes.coins.currency.DefaultPriceProxy;
import at.shockbytes.coins.currency.PriceProxy;
import at.shockbytes.coins.network.PriceManager;
import at.shockbytes.coins.network.conversion.CurrencyConversionApi;
import at.shockbytes.coins.storage.CoinsRealmMigration;
import at.shockbytes.coins.storage.RealmStorageManager;
import at.shockbytes.coins.storage.StorageManager;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */
@Module
public class AppModule {

    private Application app;

    public AppModule(Application application) {
        this.app = application;
    }

    @Provides
    @Singleton
    public CurrencyManager provideCurrencyManager(PriceProxy priceProxy,
                                                  StorageManager storageManager,
                                                  CurrencyConversionApi currencyConversionApi) {
        return new DefaultCurrencyManager(app.getApplicationContext(), priceProxy,
                storageManager, currencyConversionApi);
    }

    @Provides
    @Singleton
    public PriceProxy providePriceProxy(@Named("CoinbasePriceManager") PriceManager priceManager) {
        return new DefaultPriceProxy(priceManager);
    }

    @Provides
    @Singleton
    public Realm provideRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(CoinsRealmMigration.CURRENT_SCHEME)
                .migration(new CoinsRealmMigration())
                //.encryptionKey(key)
                .build();
        return Realm.getInstance(config);
    }

    @Provides
    @Singleton
    public StorageManager provideStorageManager(Realm realm) {
        return new RealmStorageManager(realm);
    }

}
