package com.example.data.mappers

import androidx.room.TypeConverter
import com.example.data.database.dbEntities.CurrencyEntity
import com.example.data.database.dbEntities.CurrencyExchangeRateEntity
import com.example.domain.domain.models.CurrencyBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class CurrencyMapper @Inject constructor() {

    fun toCurrencyBean(entity: CurrencyExchangeRateEntity): CurrencyBean {
        return CurrencyBean(
            baseCurrency = entity.baseCurrency,
            rates = entity.rates.map { CurrencyBean.CurrencyRateBean(it.key, it.value) }
        )
    }

    fun toCurrencyRateBean(currencies: List<CurrencyEntity>): List<CurrencyBean.CurrencyRateBean> {
        return currencies.let { currencyList ->
            currencyList.map {
                CurrencyBean.CurrencyRateBean(it.code, it.rate)
            }
        }
    }


    @TypeConverter
    fun fromString(value: String): Map<String, Double> {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(value, mapType) ?: emptyMap()
    }

    @TypeConverter
    fun fromMap(map: Map<String, Double>): String {
        return Gson().toJson(map)
    }


}