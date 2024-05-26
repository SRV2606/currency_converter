package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.database.dbEntities.CurrencyEntity
import com.example.data.database.dbEntities.CurrencyExchangeRateEntity


@Database(entities = [CurrencyExchangeRateEntity::class, CurrencyEntity::class], version = 2)
abstract class CurrencyExchangeDatabase : RoomDatabase() {
    abstract fun currencyExchangeDao(): CurrencyExchangeDao


}