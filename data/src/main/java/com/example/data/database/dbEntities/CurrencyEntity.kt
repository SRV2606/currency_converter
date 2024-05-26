package com.example.data.database.dbEntities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey val code: String,
    val rate: Double
)