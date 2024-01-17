package com.example.domain.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerBean(
    val id: Int,
    val name: String,
    val icon: String
) : Parcelable

