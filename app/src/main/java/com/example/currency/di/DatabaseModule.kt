package com.example.currency.di

import android.content.Context
import androidx.room.Room
import com.example.currency.BuildConfig
import com.example.data.database.CurrencyExchangeDao
import com.example.data.database.CurrencyExchangeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CurrencyExchangeDatabase {
        val debugBuild = BuildConfig.DEBUG

        return if (debugBuild) {
            // Create or recreate database with altered schema for debug build (for testing purposes , else migration needed)
            Room.databaseBuilder(
                appContext,
                CurrencyExchangeDatabase::class.java,
                "currency_exchange_db_debug"
            ).fallbackToDestructiveMigration()
                .build()
        } else {
            // Creating database without altering schema for release build
            Room.databaseBuilder(
                appContext,
                CurrencyExchangeDatabase::class.java,
                "currency_exchange_db"
            ).build()
        }
    }


    @Provides
    fun provideCurrencyExchangeDao(database: CurrencyExchangeDatabase): CurrencyExchangeDao {
        return database.currencyExchangeDao()
    }

}