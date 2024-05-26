package com.example.domain.domain.usecases

import com.example.domain.domain.models.CurrencyBean
import com.example.domain.domain.repository.CurrencyExchangesRepository
import com.example.domain.models.ClientResult
import javax.inject.Inject

class GetLatestCurrencyExchangesDataUseCase @Inject constructor(private val repository: CurrencyExchangesRepository) {

    suspend fun getLatestCurrencyExchanges(): ClientResult<CurrencyBean> {
        return repository.getLatestExchangeDetails()
    }

    suspend fun convertWithRespectToBase(base: Pair<String, Double>): ClientResult<CurrencyBean> {
        return repository.convertWithRespectToBase(base)
    }

    suspend fun fetchCurrencyOptions(): ClientResult<List<CurrencyBean.CurrencyRateBean>> {
        return repository.fetchCurrencyOptions()
    }

    suspend fun convertIndividualCurrency(
        fromCurrencyCode: String,
        desiredAmountCurrencyPair: Pair<String, Double>
    ): ClientResult<Pair<ClientResult<CurrencyBean>, ClientResult<Double>>> {
        return repository.convertIndividualCurrency(fromCurrencyCode, desiredAmountCurrencyPair)
    }


}
