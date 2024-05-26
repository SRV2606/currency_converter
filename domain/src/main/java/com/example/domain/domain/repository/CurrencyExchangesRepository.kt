package com.example.domain.domain.repository

import com.example.domain.domain.models.CurrencyBean
import com.example.domain.models.ClientResult

interface CurrencyExchangesRepository {


    companion object {
        const val API_KEY = "05aac91068444b5da10c9b56fd927689"
        const val LAST_FETCH_TIME = "last_fetch_time"
        const val FETCH_INTERVAL = 30 * 60 * 1000 // 30 minutes in milliseconds
        const val DEFAULT_BASE_CURRENCY = "USD" // due to free api limitations
    }


    suspend fun getLatestExchangeDetails(base: String = DEFAULT_BASE_CURRENCY): ClientResult<CurrencyBean>
    suspend fun convertWithRespectToBase(base: Pair<String, Double>): ClientResult<CurrencyBean>
    suspend fun fetchCurrencyOptions(): ClientResult<List<CurrencyBean.CurrencyRateBean>>
    suspend fun convertIndividualCurrency(
        fromCurrencyCode: String,
        desiredAmountCurrencyPair: Pair<String, Double>
    ): ClientResult<Pair<ClientResult<CurrencyBean>, ClientResult<Double>>>
}