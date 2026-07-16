package com.betslip.analyzer.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object RetrofitClient {
    private const val BASE_URL = "https://sportsrc.com/"
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    
    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    val apiService: SportSrcApiService by lazy {
        retrofitInstance.create(SportSrcApiService::class.java)
    }
}
