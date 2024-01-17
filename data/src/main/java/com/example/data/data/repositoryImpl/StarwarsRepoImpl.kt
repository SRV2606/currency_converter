package com.example.data.data.repositoryImpl

import com.example.data.data.mappers.StarWarsMapper
import com.example.data.data.service.ApiService
import com.example.data.utlils.safeApiCall
import com.example.domain.domain.models.DecathlonSKUItemBean
import com.example.domain.domain.models.ListSorters
import com.example.domain.domain.models.MatchBean
import com.example.domain.domain.models.PlayerBean
import com.example.domain.domain.repository.StarwarsRepository
import com.example.domain.models.ClientResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StarwarsRepoImpl @Inject constructor(
    private val service: ApiService,
    private val mapper: StarWarsMapper
) : StarwarsRepository {


    //fun to get Hero Products Initially , to load on Home
    override suspend fun getTopHeroProductsInitially(page: Int): ClientResult<List<DecathlonSKUItemBean>> {
        return withContext(Dispatchers.IO) {
            return@withContext mapper.toSkuItem(true, safeApiCall {
                service.getHeroProducts(page = page)
            })

        }
    }

    //function to get Hero Products by sorting , clicking on chips
    override suspend fun getSortedSKUItems(
        page: Int,
        sort: ListSorters
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return withContext(Dispatchers.IO) {
            return@withContext mapper.toSortedSkuItems(true, sort, safeApiCall {
                service.getHeroProducts(page = page, sortBy = sort.sortByValue)
            })

        }
    }


    //function to get Product via search query by user
    override suspend fun getFilteredSKUItems(
        page: Int,
        searchQuery: String
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return withContext(Dispatchers.IO) {
            return@withContext mapper.toFilteredSkuItem(true, searchQuery, safeApiCall {
                service.getHeroProducts(page = page, query = searchQuery)
            })
        }
    }

    override suspend fun getPlayers(): ClientResult<List<PlayerBean>> {
        return withContext(Dispatchers.IO) {
            return@withContext mapper.toPlayer(safeApiCall {
                service.getPlayers()
            })
        }
    }

    override suspend fun getMatches(): ClientResult<List<MatchBean>> {
        return withContext(Dispatchers.IO) {
            return@withContext mapper.toMatch(safeApiCall {
                service.getMatches()
            })
        }
    }


}