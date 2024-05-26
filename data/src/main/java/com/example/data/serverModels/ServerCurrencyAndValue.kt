package com.example.data.serverModels

import android.os.Parcelable


@kotlinx.parcelize.Parcelize
data class ServerCurrencyAndValue(
    val currency: Map<String, String>
) : Parcelable
