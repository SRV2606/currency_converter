package com.example.domain.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerWithPointsBean(
    val player: PlayerBean,
    val points: Int,
    val totalScore: Int
) : Parcelable