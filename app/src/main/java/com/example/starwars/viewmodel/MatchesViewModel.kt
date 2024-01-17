package com.example.starwars.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.models.MatchBean
import com.example.domain.domain.models.PlayerWithPointsBean
import com.example.domain.domain.usecases.GetStarWarsUseCase
import com.example.domain.models.ClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(private val getStarWarsUseCase: GetStarWarsUseCase) :
    ViewModel() {

    private val _matchesData = mutableListOf<MatchBean>()
    val matchesData: List<MatchBean> get() = _matchesData

    private val _matchesDataFlow: MutableStateFlow<List<MatchBean>> = MutableStateFlow(emptyList())
    val matchesDataFlow = _matchesDataFlow.asStateFlow()


    // Function to get and display matches for a selected player
    fun getMatchesForPlayer(selectedPlayer: PlayerWithPointsBean) {
        viewModelScope.launch {
            val matchesResult = getStarWarsUseCase.getMatches()
            if (matchesResult is ClientResult.Success) {
                val allMatches = matchesResult.data
                val playerMatches = filterPlayerMatches(selectedPlayer, allMatches)
                playerMatches.forEach { matchBean ->
                    matchBean.cardColor =
                        getColorForMatchResult(matchBean, selectedPlayer.player.id)

                }
                _matchesData.clear()
                _matchesData.addAll(sortMatchesByDate(playerMatches))
                _matchesDataFlow.emit(matchesData)
                Log.d("SHAW_TAG", "getMatchesForPlayer: " + matchesData)
            }
        }
    }

    // Function to filter matches for a selected player
    private fun filterPlayerMatches(
        selectedPlayer: PlayerWithPointsBean,
        allMatches: List<MatchBean>
    ): List<MatchBean> {
        return allMatches.filter {
            it.player1.id == selectedPlayer.player.id || it.player2.id == selectedPlayer.player.id
        }
    }

    // Function to sort matches from most recent to oldest
    private fun sortMatchesByDate(matches: List<MatchBean>): List<MatchBean> {
        return matches.sortedByDescending { it.match }
    }

    // Function to get color based on match result
    fun getColorForMatchResult(match: MatchBean, playerId: Int): Int {
        return when {
            (match.player1.id == playerId && match.player1.score > match.player2.score) ||
                    (match.player2.id == playerId && match.player2.score > match.player1.score) -> {
                // Win - Green
                android.graphics.Color.GREEN
            }

            (match.player1.id == playerId && match.player1.score < match.player2.score) ||
                    (match.player2.id == playerId && match.player2.score < match.player1.score) -> {
                // Loss - Red
                android.graphics.Color.RED
            }

            else -> {
                // Draw - White
                android.graphics.Color.WHITE
            }
        }
    }
}
