package com.example.starwars.di

import com.example.data.data.mappers.StarWarsMapper
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
    fun provideSkuItemMapper(): StarWarsMapper {
        return StarWarsMapper()
    }
}