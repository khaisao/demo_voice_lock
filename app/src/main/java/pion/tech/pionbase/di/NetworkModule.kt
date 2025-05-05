package pion.tech.pionbase.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pion.tech.pionbase.home.data.api.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pion.tech.pionbase.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val DEFAULT_TIMEOUT = 30
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        cache: Cache?,
        @ApplicationContext application: Context
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        return OkHttpClient.Builder()
            .sslSocketFactory(
                provideSSLSocketFactory()!!,
                provideUnTrustManager()[0] as X509TrustManager
            )
            .cache(cache)
            .addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                    val request = chain.request()
                        .newBuilder()
                        .header("Accept", "application/json")
                        .build()

                    chain.proceed(request)
                }
            )
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ChuckerInterceptor(application))
            .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideSSLSocketFactory(): SSLSocketFactory? {
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        try {
            sslContext!!.init(null, provideUnTrustManager(), SecureRandom())
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        return sslContext!!.socketFactory
    }

    @Singleton
    @Provides
    fun provideUnTrustManager(): Array<TrustManager> {
        return arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext application: Context): Cache {
        val cacheSize = 10 * 1024 * 1024.toLong() // 10 MB
        val httpCacheDirectory = File(application.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}