package at.shockbytes.coins.dagger;

import android.app.Application;

import javax.inject.Named;
import javax.inject.Singleton;

import at.shockbytes.coins.network.PriceManager;
import at.shockbytes.coins.network.coinbase.CoinbasePriceApi;
import at.shockbytes.coins.network.coinbase.CoinbasePriceManager;
import at.shockbytes.coins.network.conversion.CurrencyConversionApi;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */
@Module
public class NetworkModule {

    private Application app;

    public NetworkModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        return clientBuilder.build();
    }

    @Provides
    @Singleton
    public CoinbasePriceApi provideCoinbasePriceApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CoinbasePriceApi.SERVICE_ENDPOINT)
                .build()
                .create(CoinbasePriceApi.class);
    }

    @Provides
    @Singleton
    @Named("CoinbasePriceManager")
    public PriceManager providePriceManager(CoinbasePriceApi api) {
        return new CoinbasePriceManager(api);
    }

    @Provides
    @Singleton
    public CurrencyConversionApi provideCurrencyConversionApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CurrencyConversionApi.SERVICE_ENDPOINT)
                .build()
                .create(CurrencyConversionApi.class);
    }

}
