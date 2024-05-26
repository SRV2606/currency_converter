package com.example.data.service

import ServerLatestCurrencyData
import com.example.data.serverModels.ServerCurrencyAndValue
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    //single api for list of product sku,with different query params
    @GET("/api/latest.json")
    suspend fun getLatestCurrencyData(
        @Query("app_id") appId: String,
    ): Response<ServerLatestCurrencyData>

    //could have used this for getting the available coins , but latest.json providing similar
    //so manipulated the data likewise to save more on api bandwith
    @GET("api/currencies.json")
    suspend fun getCurrencies(
        @Query("app_id") appId: String
    ): Response<ServerCurrencyAndValue>

}