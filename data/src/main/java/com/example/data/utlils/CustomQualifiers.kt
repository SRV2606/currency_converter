package com.example.data.utlils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HeaderInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StaleIfErrorInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CacheInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Retrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Gson

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Picasso


