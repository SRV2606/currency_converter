package com.example.starwars.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.models.MatchBean
import com.example.domain.domain.models.PlayerBean
import com.example.domain.domain.models.PlayerWithPointsBean
import com.example.domain.domain.usecases.GetStarWarsUseCase
import com.example.domain.models.ApiError
import com.example.domain.models.ClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStarWarsUseCase: GetStarWarsUseCase
) : ViewModel() {


    private val __playersAndPoints:
            MutableStateFlow<ClientResult<List<PlayerWithPointsBean>>> =
        MutableStateFlow(ClientResult.InProgress)
    val playerAndPoints = __playersAndPoints.asStateFlow()

    private val _players:
            MutableStateFlow<ClientResult<List<PlayerBean>>> =
        MutableStateFlow(ClientResult.InProgress)
    val players = _players.asStateFlow()

    private val _matches:
            MutableStateFlow<ClientResult<List<MatchBean>>> =
        MutableStateFlow(ClientResult.InProgress)
    val matches = _matches.asStateFlow()


    private suspend fun getPlayers() {
        val resp = getStarWarsUseCase.getPlayers()
        _players.emit(resp)

    }

    private suspend fun getMatches() {
        val response = getStarWarsUseCase.getMatches()
        _matches.emit(response)

    }

    fun getPlayersAndMatches() {
        viewModelScope.launch {
            async { getPlayers() }.await()
            async { getMatches() }.await()
            Log.d("SHAW_TAG", "getPlayersAndMatches: ${players.value} ${matches.value}")

            combine(players, matches) { playersResult, matchesResult ->
                Log.d("SHAW_TAG", "getPlayersAndMatches0: ")

                // Check if both results are successful
                if (playersResult is ClientResult.Success && matchesResult is ClientResult.Success) {
                    Log.d("SHAW_TAG", "getPlayersAndMatches: ")
                    // Combine the data from players and matches
                    val playersWithPoints =
                        calculatePlayerPoints(playersResult.data, matchesResult.data)
                    __playersAndPoints.emit(ClientResult.Success(playersWithPoints))
                } else {
                    Log.d("SHAW_TAG", "getPlayersAndMatches1: ")

                    // If any result is an error, propagate the error
                    __playersAndPoints.emit(ClientResult.Error(ApiError("Error combining results")))
                }
            }
                .stateIn(viewModelScope, SharingStarted.Lazily, ClientResult.InProgress)
                .collect { combinedResult -> Log.d("SHAW_TAG", "Combined result: $combinedResult") }
        }
    }


    // Helper function to calculate player points and total scores
    private fun calculatePlayerPoints(
        players: List<PlayerBean>,
        matches: List<MatchBean>
    ): List<PlayerWithPointsBean> {
        val playerPointsList = mutableListOf<PlayerWithPointsBean>()

        for (player in players) {
            val playerMatches =
                matches.filter { it.player1.id == player.id || it.player2.id == player.id }
            val totalScore = playerMatches.sumBy { it.player1.score + it.player2.score }
            val playerPoints = PlayerWithPointsBean(player, totalScore, totalScore)
            playerPointsList.add(playerPoints)
        }

        // Sort players based on points and total scores
        return playerPointsList.sortedWith(compareByDescending<PlayerWithPointsBean> {
            it.points
        }.thenByDescending {
            it.totalScore
        })
    }


}
