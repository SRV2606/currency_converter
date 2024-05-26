package com.example.currency.di

import android.content.Context
import com.example.data.utlils.CacheInterceptor
import com.example.data.utlils.HeaderInterceptor
import com.example.data.utlils.LoggingInterceptor
import com.example.data.utlils.NetworkInterceptor
import com.example.data.utlils.NetworkUtil
import com.example.data.utlils.SharedPreferenceUtil
import com.example.data.utlils.StaleIfErrorInterceptor
import com.example.domain.domain.models.NoConnectivityException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {

        private const val CONNECTION_TIMEOUT: Long = 60
        private const val WRITE_TIMEOUT: Long = 60
        private const val READ_TIMEOUT: Long = 60
        private const val MAX_STALE: Int = 7
        private const val MAX_AGE: Int = 0
        private const val CACHE_SIZE: Long = 10 * 1000 * 1000 //10 MB CACHE
        private const val CACHE_CONTROL = "Cache-Control"
        private const val EXTRA_VERSION = "app-version"
        const val EXTRA_HEADER = "x-auth-token"
    }

    @Provides
    @com.example.data.utlils.OkHttpClient
    fun okHttpClient(
        @HeaderInterceptor customInterceptor: Interceptor,
        @NetworkInterceptor networkInterceptor: Interceptor,
        @StaleIfErrorInterceptor staleIfErrorInterceptor: Interceptor,
        @CacheInterceptor cacheInterceptor: Interceptor,
        @LoggingInterceptor loggingInterceptor: HttpLoggingInterceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(customInterceptor)
            .addInterceptor(networkInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(staleIfErrorInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
//            .cache(cache) // can add this if we want to cache , but here disabled it so its easy to check the error screen handling
            .build()
    }

    @Provides
    @LoggingInterceptor
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor =
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
//          Timber.i(message)
                }
            })
        //loggingInterceptor.redactHeader("x-auth-token")
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    fun cache(cacheFile: File): Cache {
        return Cache(cacheFile, CACHE_SIZE)
    }

    @Provides
    fun file(@ApplicationContext context: Context): File {
        return File(context.cacheDir, "okhttp-cache")
    }

    @Provides
    @HeaderInterceptor
    fun customInterceptor(sharedPreferenceUtil: SharedPreferenceUtil): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            requestBuilder.header(
                EXTRA_HEADER, sharedPreferenceUtil.getString(EXTRA_HEADER)
            )
            requestBuilder.header(EXTRA_VERSION, "")
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @NetworkInterceptor
    fun networkInterceptor(
        networkUtil: NetworkUtil,
        cacheControl: CacheControl
    ): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!networkUtil.hasNetwork()) {
                request = request.newBuilder().cacheControl(cacheControl).build()
                val response = chain.proceed(request)
                if (response.cacheResponse == null)
                    throw NoConnectivityException("No internet connection.")
            }
            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @StaleIfErrorInterceptor
    fun staleIfErrorInterceptor(cacheControl: CacheControl): Interceptor {
        return Interceptor { chain ->
            var response: Response? = null
            val request = chain.request()
            try {
                response?.close()
                response = chain.proceed(request)
                if (response.isSuccessful) response
            } catch (e: Exception) {

            }

            if (response == null || !response.isSuccessful) {
                val newRequest =
                    request.newBuilder().cacheControl(cacheControl).build();
                try {
                    response?.close()
                    response = chain.proceed(newRequest)
                } catch (e: Exception) {
                    throw e
                }
            }
            response
        }
    }

    @Provides
    fun cacheControl(): CacheControl {
        return CacheControl.Builder()
            .maxStale(MAX_STALE, TimeUnit.DAYS)
            .maxAge(MAX_AGE, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @CacheInterceptor
    fun cacheInterceptor(cacheControl: CacheControl): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = request.newBuilder()
                .header(CACHE_CONTROL, cacheControl.toString())
                .build()
            return@Interceptor chain.proceed(request)
        }
    }
}