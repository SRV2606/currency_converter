package com.example.currency.di

import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkerFactory
import com.example.data.mappers.CurrencyMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideCurrencyMapper(): CurrencyMapper {
        return CurrencyMapper()
    }

    @Provides
    @Singleton
    fun provideWorkerFactory(
        hiltWorkerFactory: HiltWorkerFactory
    ): WorkerFactory {
        return hiltWorkerFactory
    }

    @Provides
    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelProvider.NewInstanceFactory()
    }

}

