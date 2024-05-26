package com.example.domain.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LatestExchangeDetailsBean(
    val base: String,
    val rates: Map<String, Double>,
    val timeStamp: Int
) : Parcelable
