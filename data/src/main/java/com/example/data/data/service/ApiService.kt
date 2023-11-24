package com.example.data.data.service

import com.example.data.data.serverModels.ServerDecathlonSKUItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    //single api for list of product sku,with different query params
    @GET("/api/hero_products")
    suspend fun getHeroProducts(
        @Query("search_query") query: String? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("page") page: Int? = 0
    ): Response<List<ServerDecathlonSKUItem>>


}