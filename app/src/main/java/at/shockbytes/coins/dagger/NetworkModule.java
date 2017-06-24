package at.shockbytes.coins.dagger;

import android.app.Application;

import javax.inject.Named;
import javax.inject.Singleton;

import at.shockbytes.coins.network.CoinbasePriceApi;
import at.shockbytes.coins.network.PriceManager;
import at.shockbytes.coins.network.CoinbasePriceManager;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
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
    public CoinbasePriceApi providePiceApiService(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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



}
