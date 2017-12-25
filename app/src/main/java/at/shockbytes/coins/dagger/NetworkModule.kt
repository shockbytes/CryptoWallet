package at.shockbytes.coins.dagger

import android.app.Application
import at.shockbytes.coins.currency.price.PriceProvider
import at.shockbytes.coins.network.coinbase.CoinbasePriceApi
import at.shockbytes.coins.network.coinbase.CoinbasePriceProvider
import at.shockbytes.coins.network.conversion.CurrencyConversionApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */
@Module
class NetworkModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {

        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)
        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideCoinbasePriceApi(client: OkHttpClient): CoinbasePriceApi {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CoinbasePriceApi.SERVICE_ENDPOINT)
                .build()
                .create(CoinbasePriceApi::class.java)
    }

    @Provides
    @Singleton
    @Named("CoinbasePriceManager")
    fun providePriceManager(api: CoinbasePriceApi): PriceProvider {
        return CoinbasePriceProvider(api)
    }

    @Provides
    @Singleton
    fun provideCurrencyConversionApi(client: OkHttpClient): CurrencyConversionApi {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CurrencyConversionApi.SERVICE_ENDPOINT)
                .build()
                .create(CurrencyConversionApi::class.java)
    }

}
