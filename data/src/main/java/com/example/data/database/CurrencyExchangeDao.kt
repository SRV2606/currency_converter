package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.dbEntities.CurrencyEntity
import com.example.data.database.dbEntities.CurrencyExchangeRateEntity

@Dao
interface CurrencyExchangeDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<CurrencyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(exchangeRate: CurrencyExchangeRateEntity)

    @Query("SELECT * FROM currency")
    suspend fun getAllCurrencies(): List<CurrencyEntity>

    @Query("SELECT * FROM exchange_rate WHERE baseCurrency = :baseCurrency")
    suspend fun getRates(baseCurrency: String): CurrencyExchangeRateEntity

    @Query("SELECT * FROM exchange_rate")
    suspend fun getAllRates(): List<CurrencyExchangeRateEntity>
}