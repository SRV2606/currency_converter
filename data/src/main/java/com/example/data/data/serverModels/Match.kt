package com.example.data.data.serverModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Match(
    @SerializedName("match")
    val match: Int,
    @SerializedName("player1")
    val player1: MatchPlayer,
    @SerializedName("player2")
    val player2: MatchPlayer
) : Parcelable {
    @Parcelize
    data class MatchPlayer(
        @SerializedName("id")
        val id: Int,
        @SerializedName("score")
        val score: Int
    ) : Parcelable
}
