package at.shockbytes.coins.core;

import android.app.Application;

import at.shockbytes.coins.dagger.AppComponent;
import at.shockbytes.coins.dagger.AppModule;
import at.shockbytes.coins.dagger.DaggerAppComponent;
import at.shockbytes.coins.dagger.NetworkModule;
import io.realm.Realm;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */

public class CoinsApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        appComponent = DaggerAppComponent.builder()
                .networkModule(new NetworkModule(this))
                .appModule(new AppModule(this))
                .build();

    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
