package at.shockbytes.coins.core

import android.app.Application

import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.dagger.AppModule
import at.shockbytes.coins.dagger.DaggerAppComponent
import at.shockbytes.coins.dagger.NetworkModule
import io.realm.Realm

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */

class CryptoWatcherApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        appComponent = DaggerAppComponent.builder()
                .networkModule(NetworkModule(this))
                .appModule(AppModule(this))
                .build()

    }
}
