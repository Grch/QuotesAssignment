package grch.assignment.stonks.di

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import grch.assignment.stonks.data.api.StocksClient
import grch.assignment.stonks.data.repository.StocksRepository
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesOkHttpClient() : OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }.build()
    }

    @Singleton
    @Provides
    fun providesRequest() = Request.Builder()
            .url("wss://quotes.eccalls.mobi:18400/")
            .build()

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory = MoshiConverterFactory.create()


    @Provides
    @Singleton
    fun providesMoshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideLifecycle(application: Application): Lifecycle {
        return AndroidLifecycle.ofApplicationForeground(application)
    }

    @Singleton
    @Provides
    fun providesStocksClient(okHttpClient: OkHttpClient,
                             request: Request,
                             moshi: Moshi) = StocksClient(okHttpClient, request, moshi)

    @Singleton
    @Provides
    fun provideStocksRepository(stocksClient: StocksClient) = StocksRepository(stocksClient)

//    @Singleton
//    @Provides
//    fun providesSocketService(okHttpClient: OkHttpClient, lifecycle: Lifecycle): StonksApi {
//        val moshi = Moshi.Builder()
//            .add(MoshiAdapters())
//            .add(KotlinJsonAdapterFactory())
//            .build()
//        val scarlet = Scarlet.Builder()
//            .webSocketFactory(okHttpClient.newWebSocketFactory("wss://quotes.eccalls.mobi:18400"))
//            .lifecycle(lifecycle)
//            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
//            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
//            .build()
//        return scarlet.create()
//    }
}