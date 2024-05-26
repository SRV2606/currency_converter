package com.example.currency.di

import com.example.data.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    private val BASE_URL = "https://openexchangerates.org/"

    @Provides
    fun provideService(@com.example.data.utlils.Retrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @com.example.data.utlils.Retrofit
    fun retrofit(
        @com.example.data.utlils.OkHttpClient okHttpClient: OkHttpClient,
        @com.example.data.utlils.Gson gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @com.example.data.utlils.Gson
    fun gson(): Gson {
        return GsonBuilder().create()
    }

}