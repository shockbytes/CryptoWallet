package at.shockbytes.coins.dagger

import android.app.Application
import android.content.SharedPreferences
import at.shockbytes.coins.currency.conversion.CurrencyConversionProvider
import at.shockbytes.coins.currency.price.PriceProvider
import at.shockbytes.coins.network.coinbase.CoinbasePriceApi
import at.shockbytes.coins.network.coinbase.CoinbasePriceProvider
import at.shockbytes.coins.network.coinmarketcap.CoinMarketCapPriceApi
import at.shockbytes.coins.network.coinmarketcap.CoinMarketCapPriceProvider
import at.shockbytes.coins.network.conversion.FixerIoCurrencyConversionApi
import at.shockbytes.coins.network.conversion.FixerIoCurrencyConversionProvider
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
    @Named("CoinbasePriceProvider")
    fun provideCoinbasePriceProvider(api: CoinbasePriceApi,
                                     prefs: SharedPreferences): PriceProvider {
        return CoinbasePriceProvider(api, prefs)
    }


    @Provides
    @Singleton
    fun provideCoinMarketCapPriceApi(client: OkHttpClient): CoinMarketCapPriceApi {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CoinMarketCapPriceApi.SERVICE_ENDPOINT)
                .build()
                .create(CoinMarketCapPriceApi::class.java)
    }

    @Provides
    @Singleton
    @Named("CoinMarketCapPriceProvider")
    fun provideCoinMarketCapPriceProvider(api: CoinMarketCapPriceApi,
                                          prefs: SharedPreferences): PriceProvider {
        return CoinMarketCapPriceProvider(api, prefs)
    }

    @Provides
    @Singleton
    fun provideFixerIoCurrencyConversionApi(client: OkHttpClient): FixerIoCurrencyConversionApi {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(FixerIoCurrencyConversionApi.SERVICE_ENDPOINT)
                .build()
                .create(FixerIoCurrencyConversionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFixerIoCurrencyConversionProvider(api: FixerIoCurrencyConversionApi,
                                                 prefs: SharedPreferences): CurrencyConversionProvider {
        return FixerIoCurrencyConversionProvider(api, prefs)
    }

}
