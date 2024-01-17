package com.example.domain.domain.usecases

import com.example.domain.domain.models.DecathlonSKUItemBean
import com.example.domain.domain.models.ListSorters
import com.example.domain.domain.models.MatchBean
import com.example.domain.domain.models.PlayerBean
import com.example.domain.domain.repository.StarwarsRepository
import com.example.domain.models.ClientResult
import javax.inject.Inject

class GetStarWarsUseCase @Inject constructor(private val repository: StarwarsRepository) {

    //fun to get Hero Products Initially , to load on Home
    suspend fun getInitialSKUItems(
        page: Int
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return repository.getTopHeroProductsInitially(page = page)
    }

    //function to get Hero Products by sorting , clicking on chips
    suspend fun sortSKUItems(
        sort: ListSorters,
        page: Int
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return repository.getSortedSKUItems(sort = sort, page = page)
    }

    //function to get Product via search query by user
    suspend fun filterItemsBySearch(
        query: String,
        page: Int
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return repository.getFilteredSKUItems(searchQuery = query, page = page)
    }

    suspend fun getPlayers(): ClientResult<List<PlayerBean>> {
        // Assuming there's an API call to get the list of players
        return repository.getPlayers()
    }

    suspend fun getMatches(): ClientResult<List<MatchBean>> {
        // Assuming there's an API call to get the list of matches
        return repository.getMatches()
    }
}