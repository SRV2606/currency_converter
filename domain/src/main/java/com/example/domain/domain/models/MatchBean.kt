package com.example.domain.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MatchBean(
    val match: Int,
    val player1: MatchPlayerBean,
    val player2: MatchPlayerBean,
    var cardColor: Int? = 0
) : Parcelable {
    @Parcelize
    data class MatchPlayerBean(
        val id: Int,
        val score: Int,
        val name: String = ""
    ) : Parcelable
}
