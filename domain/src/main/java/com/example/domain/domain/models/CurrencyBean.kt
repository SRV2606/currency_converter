package com.example.domain.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CurrencyBean(
    val baseCurrency: String,
    val rates: List<CurrencyRateBean>
) : Parcelable {
    @Parcelize
    data class CurrencyRateBean(
        val currencyCode: String,
        val currencyRate: Double
    ) : Parcelable
}
