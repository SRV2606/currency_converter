package com.example.currency.di

import com.example.data.repositoryImpl.CurrencyExchangesRepositoryImpl
import com.example.domain.domain.repository.CurrencyExchangesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    abstract fun bindCurrencyExchangeRepo(decathlonRepoImpl: CurrencyExchangesRepositoryImpl): CurrencyExchangesRepository

}