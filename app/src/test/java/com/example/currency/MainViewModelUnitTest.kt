package com.example.currency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currency.viewmodel.MainViewModel
import com.example.domain.domain.models.ApiError
import com.example.domain.domain.models.CurrencyBean
import com.example.domain.domain.usecases.GetLatestCurrencyExchangesDataUseCase
import com.example.domain.models.ClientResult
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    lateinit var getLatestCurrencyExchangesDataUseCase: GetLatestCurrencyExchangesDataUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getLatestCurrencyExchangesDataUseCase = mockk()
        mainViewModel = MainViewModel(getLatestCurrencyExchangesDataUseCase)
    }


    /**Showcasing handling of livedata/flow states for api call **/
    @Test
    fun `fetchLatestCurrencyExchangesData should emit correct data`() = runTest(testDispatcher) {
        val mockData = CurrencyBean("USD", listOf())
        coEvery { getLatestCurrencyExchangesDataUseCase.getLatestCurrencyExchanges() } returns ClientResult.Success(
            mockData
        )

        mainViewModel.fetchLatestCurrencyExchangesData()
        val result = mainViewModel.latestCurrencyExchangeData.first()
        assertTrue(result is ClientResult.Success)
        assertEquals(mockData, (result as ClientResult.Success).data)
    }

    /**Showcasing handling of livedata/flow states for api call **/
    @Test
    fun `fetchLatestCurrencyExchangesData should emit error state`() = runTest(testDispatcher) {
        val errorMessage = "An error occurred"
        coEvery { getLatestCurrencyExchangesDataUseCase.getLatestCurrencyExchanges() } returns ClientResult.Error(
            ApiError(errorMessage)
        )

        mainViewModel.fetchLatestCurrencyExchangesData()
        val result = mainViewModel.latestCurrencyExchangeData.first()
        assertTrue(result is ClientResult.Error)
        assertEquals(errorMessage, (result as ClientResult.Error).error.message)
    }

    /**Showcasing handling of livedata/flow states for api call **/
    @Test
    fun `fetchLatestCurrencyExchangesData should emit loading state`() = runTest(testDispatcher) {
        val initialState = mainViewModel.latestCurrencyExchangeData.first()
        assertTrue(initialState is ClientResult.InProgress)
    }


    /**Showcasing handling of user input and function validations **/

    @Test
    fun `checkAndUpdateUserInputs should return false for invalid inputs`() =
        runTest(testDispatcher) {
            assertFalse(mainViewModel.checkAndUpdateUserInputs("", 1.0))
            assertFalse(mainViewModel.checkAndUpdateUserInputs("USD", 0.0))
            assertFalse(mainViewModel.checkAndUpdateUserInputs("USD", -1.0))
        }

    /**Showcasing handling of user input and function validations **/
    @Test
    fun `checkAndUpdateUserInputs should return true and update state for valid inputs`() =
        runTest(testDispatcher) {
            val validCurrency = "USD"
            val validAmount = 100.0

            assertTrue(mainViewModel.checkAndUpdateUserInputs(validCurrency, validAmount))

            val result = mainViewModel.desiredAmount.first()
            assertEquals(validCurrency, result.first)
            assertEquals(validAmount, result.second, 0.0)
        }

    /**Showcasing handling of user input and function validations **/
    @Test
    fun `convertIndividualCurrency should not proceed for invalid inputs`() =
        runTest(testDispatcher) {
            mainViewModel.convertIndividualCurrency("", "USD", 100.0)
            assertEquals(ClientResult.InProgress, mainViewModel.convertedIndividualCurrency.first())

            mainViewModel.convertIndividualCurrency("USD", "", 100.0)
            assertEquals(ClientResult.InProgress, mainViewModel.convertedIndividualCurrency.first())

            mainViewModel.convertIndividualCurrency("USD", "EUR", 0.0)
            assertEquals(ClientResult.InProgress, mainViewModel.convertedIndividualCurrency.first())

            mainViewModel.convertIndividualCurrency("USD", "EUR", -1.0)
            assertEquals(ClientResult.InProgress, mainViewModel.convertedIndividualCurrency.first())
        }

    /**Showcasing handling of user input and function validations **/
    @Test
    fun `updateConvertButtonState should enable button for valid inputs`() =
        runTest(testDispatcher) {
            mainViewModel.updateConvertButtonState("100", "USD", "EUR", true)
            val result = mainViewModel.isConvertButtonEnabled.first()
            assertTrue(result)
        }


    /**Showcasing handling of user input and function validations **/
    @Test
    fun `updateConvertButtonState should disable button for invalid inputs`() =
        runTest(testDispatcher) {
            mainViewModel.updateConvertButtonState("", "USD", "EUR", true)
            var result = mainViewModel.isConvertButtonEnabled.first()
            assertFalse(result)

            mainViewModel.updateConvertButtonState("100", "", "EUR", true)
            result = mainViewModel.isConvertButtonEnabled.first()
            assertFalse(result)

            mainViewModel.updateConvertButtonState("100", "USD", "", true)
            result = mainViewModel.isConvertButtonEnabled.first()
            assertFalse(result)

            mainViewModel.updateConvertButtonState("100", "", "EUR", false)
            result = mainViewModel.isConvertButtonEnabled.first()
            assertTrue(result)
        }
}
