package com.example.currency.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currency.R
import com.example.currency.base.BaseActivity
import com.example.currency.collectEvent
import com.example.currency.databinding.ActivityMainBinding
import com.example.currency.services.CurrencyWorker
import com.example.currency.ui.adapters.CurrencyGridAdapter
import com.example.currency.viewmodel.MainViewModel
import com.example.domain.domain.models.CurrencyBean
import com.example.domain.models.ClientResult
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {


    private var currencyOptions: List<CurrencyBean.CurrencyRateBean> = listOf()
    private val mainViewModel by viewModels<MainViewModel>()

    private val currencyGridAdapter by lazy {
        CurrencyGridAdapter(itemClickListener = { currencyRate ->
        }, context = this@MainActivity)
    }

    private lateinit var fromCurrencyAdapter: ArrayAdapter<String>
    private lateinit var toCurrencyAdapter: ArrayAdapter<String>
    private lateinit var selectedCurrencyOption: String
    private lateinit var userDesiredAmount: String
    private var enableIndividualCurrencyConversionExtraFunctionality: Boolean = false
    override fun readArguments(extras: Intent) {

    }

    override fun setupUi() {
        binding.userInfoTV.text = getString(R.string.initial_user_info)
        setupRecyclerView()
        mainViewModel.fetchLatestCurrencyExchangesData()
        schedulePeriodicWork()
    }


    //scheduling work for Workmanager, refreshing data every 30 mins even if not interacting with app
    private fun schedulePeriodicWork() {
        Log.d("Current_TAG", "schedulePeriodicWork: ")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatInterval =
            1000L // Repeat interval, keeping it low for testing purposes , else we can have 30 mins
        val periodicWorkRequest = PeriodicWorkRequestBuilder<CurrencyWorker>(
            1000, TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            CurrencyWorker::class.java.name,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }


    private fun setupRecyclerView() {
        binding.currencyRecyclerView.layoutManager =
            GridLayoutManager(this, 4)
        binding.currencyRecyclerView.adapter = currencyGridAdapter
    }

    //Setting up the drop down menu of currencyOptions we have for the user to choose from
    private fun setupDropdownMenus(currencyOptions: List<CurrencyBean.CurrencyRateBean>) {
        // Initializing the array adapter
        fromCurrencyAdapter = ArrayAdapter(
            this@MainActivity,
            R.layout.item_currency_dropdown,
            currencyOptions.map { it.currencyCode })
        toCurrencyAdapter = ArrayAdapter(
            this@MainActivity,
            R.layout.item_currency_dropdown,
            currencyOptions.map { it.currencyCode })


        (binding.fromCurrencyEditText.editText as MaterialAutoCompleteTextView).apply {
            setAdapter(fromCurrencyAdapter)
            setOnItemClickListener { _, _, position, _ ->
                val selectedCurrency = currencyOptions[position]
                setText(selectedCurrency.currencyCode, false)
            }
        }

        (binding.toCurrencyEditText.editText as MaterialAutoCompleteTextView).apply {
            setAdapter(toCurrencyAdapter)
            setOnItemClickListener { _, _, position, _ ->
                val selectedCurrency = currencyOptions[position]
                setText(selectedCurrency.currencyCode, false)
                selectedCurrencyOption = selectedCurrency.currencyCode
            }

        }


    }

    override fun observeData() {
        //getting the latest currency options we have , for the users to choose from
        collectEvent(mainViewModel.currencyChoicesList) {
            when (it) {
                is ClientResult.Error -> {
                    renderErrorScreenWithRetry(it)
                }

                ClientResult.InProgress -> {
                    renderLoadingScreen(true)
                }

                is ClientResult.Success -> {
                    renderLoadingScreen(false)
                    currencyOptions = it.data
                    setupDropdownMenus(currencyOptions)
                }
            }
        }
        //Getting the latest currency exchange data, with the latest prices for each currency w.r.t USD
        collectEvent(mainViewModel.latestCurrencyExchangeData) {
            when (it) {
                is ClientResult.Error -> {
                    renderErrorScreenWithRetry(it)
                }

                ClientResult.InProgress -> {
                    renderLoadingScreen(true)
                }

                is ClientResult.Success -> {
                    renderLoadingScreen(false)
                    renderSuccessScreen(it.data)
                    mainViewModel.fetchCurrencyOptions()
                }
            }
        }
        //Getting the latest currency exchange data converted w.r.t the  base currency selected
        collectEvent(mainViewModel.currencyDataWRTBase) {
            when (it) {
                is ClientResult.Error -> {
                    renderErrorScreenWithRetry(it)
                }

                ClientResult.InProgress -> {
                    renderLoadingScreen(true)
                }

                is ClientResult.Success -> {
                    renderLoadingScreen(false)
                    renderSuccessScreen(it.data)

                }
            }
        }

        //Getting the result for conversion of the individual currency conversion feature
        collectEvent(mainViewModel.convertedIndividualCurrency) {
            when (it) {
                is ClientResult.Error -> {

                }

                ClientResult.InProgress -> {

                }

                is ClientResult.Success -> {
                    binding.convertedCurrencyTV.text = it.data.toString()
                }
            }
        }
        //Stateflow to check and change the status of the Convert button, logic in viewmodel , for testing purposes as well
        collectEvent(mainViewModel.isConvertButtonEnabled) { enabled ->
            binding.conversionResultButton.isEnabled = enabled
        }
    }


    //Generic functions to avoid redundant handling
    private fun renderLoadingScreen(isLoading: Boolean) {
        if (isLoading) {
            binding.circularProgressView.visibility = View.VISIBLE
            binding.currencyRecyclerView.visibility = View.GONE
        } else {
            binding.circularProgressView.visibility = View.GONE
            binding.currencyRecyclerView.visibility = View.VISIBLE
        }
    }

    //Generic functions to avoid redundant handling
    private fun renderErrorScreenWithRetry(clientResult: ClientResult.Error) {
        binding.currencyRecyclerView.visibility = View.GONE
        binding.retryLayoutHolder.retryLayoutCL.visibility = View.VISIBLE
        binding.retryLayoutHolder.retryMessageTV.text = clientResult.error.message
        binding.retryLayoutHolder.retryCTA.setOnClickListener {
            mainViewModel.fetchLatestCurrencyExchangesData()
        }
    }

    //Generic functions to avoid redundant handling
    private fun renderSuccessScreen(clientResult: CurrencyBean) {
        if (binding.retryLayoutHolder.retryLayoutCL.visibility == View.VISIBLE) {
            binding.retryLayoutHolder.retryLayoutCL.visibility = View.GONE
        }
        if (binding.noMovieFoundHolder.retryLayoutCL.visibility == View.VISIBLE) {
            binding.noMovieFoundHolder.retryLayoutCL.visibility = View.GONE
        }
        if (binding.currencyRecyclerView.visibility == View.GONE) {
            binding.currencyRecyclerView.visibility = View.VISIBLE
        }

        if (clientResult.rates.isEmpty()) {
            renderDataNotFoundScreen()
        } else {
            currencyGridAdapter.submitList(clientResult.rates)
        }
    }

    override fun setListener() {
        binding.amountEditText.doAfterTextChanged {
            userDesiredAmount = it.toString()
            mainViewModel.updateConvertButtonState(
                amount = userDesiredAmount,
                fromCurrency = binding.fromCurrencyEditText.editText?.text.toString(),
                toCurrency = binding.toCurrencyEditText.editText?.text.toString(),
                isFromCurrencyEnabled = enableIndividualCurrencyConversionExtraFunctionality
            )
        }


        binding.fromCurrencyEditText.editText?.doAfterTextChanged {
            mainViewModel.updateConvertButtonState(
                amount = binding.amountEditText.text.toString(),
                fromCurrency = it.toString(),
                toCurrency = binding.toCurrencyEditText.editText?.text.toString(),
                isFromCurrencyEnabled = binding.enableFromCurrencyCheckbox.isChecked
            )
        }

        binding.toCurrencyEditText.editText?.doAfterTextChanged {
            mainViewModel.updateConvertButtonState(
                amount = binding.amountEditText.text.toString(),
                fromCurrency = binding.fromCurrencyEditText.editText?.text.toString(),
                toCurrency = it.toString(),
                isFromCurrencyEnabled = binding.enableFromCurrencyCheckbox.isChecked
            )
        }


        binding.enableFromCurrencyCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.fromCurrencyEditText.isEnabled = isChecked
            enableIndividualCurrencyConversionExtraFunctionality = isChecked
            if (isChecked) {
                binding.convertedCurrencyTV.visibility = View.VISIBLE
            } else {
                binding.convertedCurrencyTV.visibility = View.GONE
            }
            mainViewModel.updateConvertButtonState(
                amount = binding.amountEditText.text.toString(),
                fromCurrency = binding.fromCurrencyEditText.editText?.text.toString(),
                toCurrency = binding.toCurrencyEditText.editText?.text.toString(),
                isFromCurrencyEnabled = isChecked
            )
        }


        binding.conversionResultButton.setOnClickListener {
            if (enableIndividualCurrencyConversionExtraFunctionality) {
                val fromCurrencyCode = binding.fromCurrencyEditText.editText?.text.toString()
                val toCurrencyCode = selectedCurrencyOption
                val amountText = userDesiredAmount
                //catching conversion exceptions , without crash
                val amount = try {
                    amountText.toDouble()
                } catch (e: NumberFormatException) {
                    binding.amountEditText.error = getString(R.string.invalid_amount)
                    return@setOnClickListener
                }
                mainViewModel.convertIndividualCurrency(fromCurrencyCode, toCurrencyCode, amount)
            } else {
                val selectedBaseCurrencyCode = selectedCurrencyOption
                val amountText = userDesiredAmount
                //catching conversion exceptions , without crash
                val amount = try {
                    amountText.toDouble()
                } catch (e: NumberFormatException) {
                    binding.amountEditText.error = getString(R.string.invalid_amount)
                    return@setOnClickListener
                }

                mainViewModel.checkAndUpdateUserInputs(selectedBaseCurrencyCode, amount)
            }
            val userInformationText = if (userDesiredAmount.isNotEmpty()) {
                getString(R.string.user_information, selectedCurrencyOption, userDesiredAmount)
            } else {
                getString(R.string.user_information, selectedCurrencyOption, null)
            }
            binding.userInfoTV.text = userInformationText

        }
    }


    //Generic functions
    private fun renderDataNotFoundScreen(hasNetwork: Boolean = true) {
        binding.currencyRecyclerView.visibility = View.GONE
        binding.noMovieFoundHolder.retryLayoutCL.visibility = View.VISIBLE
        binding.noMovieFoundHolder.retryMessageTV.text =
            if (hasNetwork) "Nothing Found" else "No Network detected"
    }


}