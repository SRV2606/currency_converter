package com.example.data.repositoryImpl

import android.util.Log
import com.example.data.data.utlils.safeApiCall
import com.example.data.database.CurrencyExchangeDao
import com.example.data.database.dbEntities.CurrencyEntity
import com.example.data.database.dbEntities.CurrencyExchangeRateEntity
import com.example.data.mappers.CurrencyMapper
import com.example.data.service.ApiService
import com.example.data.utlils.SharedPreferenceUtil
import com.example.domain.domain.models.ApiError
import com.example.domain.domain.models.CurrencyBean
import com.example.domain.domain.repository.CurrencyExchangesRepository
import com.example.domain.domain.repository.CurrencyExchangesRepository.Companion.DEFAULT_BASE_CURRENCY
import com.example.domain.domain.repository.CurrencyExchangesRepository.Companion.FETCH_INTERVAL
import com.example.domain.models.ClientResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyExchangesRepositoryImpl @Inject constructor(
    private val service: ApiService,
    private val mapper: CurrencyMapper,
    private val sharedPreferences: SharedPreferenceUtil,
    private val currencyExchangeDao: CurrencyExchangeDao
) : CurrencyExchangesRepository {


    override suspend fun getLatestExchangeDetails(base: String): ClientResult<CurrencyBean> {
        return withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            val lastFetchTime =
                sharedPreferences.getLong(CurrencyExchangesRepository.LAST_FETCH_TIME, 0)
            //Check for making the api call in 30 mins window
            val shouldFetchFromApi = currentTime - lastFetchTime > FETCH_INTERVAL

            if (shouldFetchFromApi) {
                val result = safeApiCall {
                    service.getLatestCurrencyData(CurrencyExchangesRepository.API_KEY)
                }
                if (result is ClientResult.Success) {
                    result.data.let { exchangeRates ->
                        val currencies =
                            exchangeRates.rates.map { CurrencyEntity(it.key, it.value) }
                        //Inserting the currency list in one table in room db
                        currencyExchangeDao.insertAll(currencies)
                        //Inserting the rates fo currency with respect to a certain base selected
                        currencyExchangeDao.insertRates(
                            CurrencyExchangeRateEntity(
                                exchangeRates.base,
                                exchangeRates.rates
                            )
                        )
                        //Adding relevant data to SharedPrefs, like Last Fetched Time
                        sharedPreferences.apply {
                            putLong(CurrencyExchangesRepository.LAST_FETCH_TIME, currentTime)
                            putString(
                                SharedPreferenceUtil.EXCHANGE_API_KEY,
                                CurrencyExchangesRepository.API_KEY
                            )
                        }
                    }
                }
                val convertedData =
                    mapper.toCurrencyBean(currencyExchangeDao.getRates(base))

                return@withContext convertedData.let {
                    ClientResult.Success(it)
                }
            } else {
                //The block takes care of the time when say the interval is not over, and it fetches the data from the db itself
                return@withContext currencyExchangeDao.getRates(DEFAULT_BASE_CURRENCY).let {
                    ClientResult.Success(mapper.toCurrencyBean(it))
                }
            }
        }
    }

    //This function totally handles the Conversion of all currencies to w.r.t user entered base currency
    /**
     * Checks is USD as base is there for us, due to api limitation
     * Checks if the current user entered base currency is present in our db , and what's its rate
     * Performs the calculation accordingly with user entered value
     * Returns the result to database and updates the tables
     * Maps the data from entity to bean and sends to UI
     */
    override suspend fun convertWithRespectToBase(base: Pair<String, Double>): ClientResult<CurrencyBean> {
        return withContext(Dispatchers.IO) {
            try {

                //Getting the base USD currency for calculation purposes ,due to free api limitations
                val usdRates = currencyExchangeDao.getRates(DEFAULT_BASE_CURRENCY)
                if (currencyExchangeDao.getAllCurrencies().isEmpty()) {
                    return@withContext ClientResult.Error(ApiError("No data available in the database"))
                }

                //user enterd values
                val baseCurrency = base.first
                val userEnteredValue = base.second

                // Checking if the base currency rate is available
                val baseRate =
                    usdRates.rates[baseCurrency] ?: return@withContext ClientResult.Error(
                        ApiError("Base currency rate not found")
                    )

                // Converting rates with respect to the selected base currency and user-entered value
                val convertedRates =
                    usdRates.rates.mapValues { (_, value) -> (value / baseRate) * userEnteredValue }


                // Inserting  the converted data into the database
                val convertedEntity = CurrencyExchangeRateEntity(baseCurrency, convertedRates)
                currencyExchangeDao.insertAll(convertedEntity.rates.map {
                    Log.d("Converted_TAG", "convertWithRespectToBase: " + it.key + it.value)
                    CurrencyEntity(
                        it.key,
                        it.value
                    )
                })
                currencyExchangeDao.insertRates(convertedEntity)

                // Converting the result back to CurrencyBean  from CurrencyExchangeRateEntity
                val currencyBean = mapper.toCurrencyBean(convertedEntity)
                return@withContext ClientResult.Success(currencyBean)
            } catch (e: Exception) {
                return@withContext ClientResult.Error(ApiError("Some error occurred: $e"))
            }
        }
    }

    override suspend fun fetchCurrencyOptions(): ClientResult<List<CurrencyBean.CurrencyRateBean>> {
        return withContext(Dispatchers.IO) {
            try {
                val currencyOptions =
                    mapper.toCurrencyRateBean(currencyExchangeDao.getAllCurrencies())
                return@withContext ClientResult.Success(currencyOptions)
            } catch (e: Exception) {
                return@withContext ClientResult.Error(ApiError("Some error occurred${e}"))
            }
        }
    }

    override suspend fun convertIndividualCurrency(
        fromCurrencyCode: String,
        desiredAmountCurrencyPair: Pair<String, Double>
    ): ClientResult<Pair<ClientResult<CurrencyBean>, ClientResult<Double>>> {
        return withContext(Dispatchers.IO) {
            try {
                val baseCurrency = desiredAmountCurrencyPair.first
                val desiredAmount = desiredAmountCurrencyPair.second

                // Fetching the rates for the  default base currency
                val usdRates = currencyExchangeDao.getRates(DEFAULT_BASE_CURRENCY)

                // Checking if the fromCurrency rate is available
                val fromCurrencyRate = usdRates.rates[fromCurrencyCode]
                    ?: return@withContext ClientResult.Error(ApiError("From currency rate not found"))

                // Checking if the base currency rate is available
                val toCurrencyRate = usdRates.rates[baseCurrency]
                    ?: return@withContext ClientResult.Error(ApiError("Base currency rate not found"))

                // Calculating the converted amount, for individual currency conversion
                val convertedAmount = (desiredAmount / fromCurrencyRate) * toCurrencyRate

                // After converting the specific amount, adjusting all other currency rates w.r.t the toCurrencyRate
                val allCurrenciesConversion =
                    convertWithRespectToBase(Pair(baseCurrency, desiredAmount))

                ClientResult.Success(
                    Pair(
                        allCurrenciesConversion,
                        ClientResult.Success(convertedAmount)
                    )
                )
            } catch (e: Exception) {
                ClientResult.Error(ApiError("Some error occurred: $e"))
            }
        }
    }


}