package com.example.data.database.dbEntities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.mappers.CurrencyMapper


@Entity(tableName = "exchange_rate")
@TypeConverters(CurrencyMapper::class)
data class CurrencyExchangeRateEntity(
    @PrimaryKey val baseCurrency: String,
    val rates: Map<String, Double>
)