package com.example.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.models.CurrencyBean
import com.example.domain.domain.usecases.GetLatestCurrencyExchangesDataUseCase
import com.example.domain.models.ClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLatestCurrencyExchangesDataUseCase: GetLatestCurrencyExchangesDataUseCase
) : ViewModel() {


    //stateflow for getting the latest exchange data
    private val _latestCurrencyExchangeData: MutableStateFlow<ClientResult<CurrencyBean>> =
        MutableStateFlow(ClientResult.InProgress)
    val latestCurrencyExchangeData = _latestCurrencyExchangeData.asStateFlow()

    //stateflow for storing result of the currency conversions w.r.t a selected base currency
    private val _currencyDataWRTBase: MutableStateFlow<ClientResult<CurrencyBean>> =
        MutableStateFlow(ClientResult.InProgress)
    val currencyDataWRTBase = _currencyDataWRTBase.asStateFlow()


    //SharedFlow for storing the current list of existing currencies for user to select from
    private val _currencyChoicesList: MutableSharedFlow<ClientResult<List<CurrencyBean.CurrencyRateBean>>> =
        MutableSharedFlow()
    val currencyChoicesList = _currencyChoicesList.asSharedFlow()

    //Stateflow for storing the current user entered  desired amount with a base currency
    private val _desiredAmount: MutableStateFlow<Pair<String, Double>> =
        MutableStateFlow(Pair("", 0.0))
    val desiredAmount = _desiredAmount.asStateFlow()

    //Stateflow for storing the converted individual currency according to user needs (extra feature)
    private val _convertedIndividualCurrency: MutableStateFlow<ClientResult<Double>> =
        MutableStateFlow(ClientResult.InProgress)
    val convertedIndividualCurrency = _convertedIndividualCurrency.asStateFlow()

    //StateFlow for storing the current state of the convert button , also used for testing purposes
    private val _isConvertButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isConvertButtonEnabled = _isConvertButtonEnabled.asStateFlow()


    //Get/Update the latest exchange data , from server or from db
    fun fetchLatestCurrencyExchangesData() {
        viewModelScope.launch {
            val apiCall = getLatestCurrencyExchangesDataUseCase.getLatestCurrencyExchanges()
            _latestCurrencyExchangeData.emit(apiCall)
        }
    }

    //Convert all currencies w.r.t a user selected base currency
    private fun convertWithRespectToBaseCurrency() {
        viewModelScope.launch {
            val result =
                getLatestCurrencyExchangesDataUseCase.convertWithRespectToBase(desiredAmount.value)
            _currencyDataWRTBase.emit(result)

        }
    }

    //Fetch the latest currency list  to give choices to a user to select from
    fun fetchCurrencyOptions() {
        viewModelScope.launch {
            val result = getLatestCurrencyExchangesDataUseCase.fetchCurrencyOptions()
            _currencyChoicesList.emit(result)
        }
    }

    //Function to validate all user inputs before enabling the convert button
    fun checkAndUpdateUserInputs(
        selectedCurrencyOption: String,
        userDesiredAmount: Double
    ): Boolean {
        // Check if the selected currency option is empty
        if (selectedCurrencyOption.isEmpty()) {
            return false
        }

        // Check if the user desired amount is less than or equal to 1.0
        if (userDesiredAmount <= 0) {
            return false
        }

        // If both checks pass, emit the value and return true
        viewModelScope.launch {
            _desiredAmount.emit(Pair(selectedCurrencyOption, userDesiredAmount))
            convertWithRespectToBaseCurrency()
        }

        return true
    }

    //Function to validate the user inputs for the Individual Currency Conversion Feature as well as Rest
    fun convertIndividualCurrency(
        fromCurrencyCode: String,
        toCurrencyCode: String,
        amount: Double
    ) {
        if (fromCurrencyCode.isEmpty()) {
            return
        }
        if (toCurrencyCode.isEmpty()) {
            return
        }

        if (amount <= 0) {
            return
        }
        viewModelScope.launch {
            _desiredAmount.emit(Pair(toCurrencyCode, amount))
            val result = getLatestCurrencyExchangesDataUseCase.convertIndividualCurrency(
                fromCurrencyCode,
                desiredAmount.value,
            )
            if (result is ClientResult.Success) {
                _convertedIndividualCurrency.emit(result.data.second)
                _currencyDataWRTBase.emit(result.data.first)
            }

        }
    }

    //Function to validate and change/update the existing state of buttons
    fun updateConvertButtonState(
        amount: String,
        fromCurrency: String,
        toCurrency: String,
        isFromCurrencyEnabled: Boolean
    ) {
        viewModelScope.launch {
            _isConvertButtonEnabled.emit(
                amount.isNotBlank() &&
                        (!isFromCurrencyEnabled || fromCurrency.isNotBlank()) &&
                        toCurrency.isNotBlank()
            )
        }
    }

}