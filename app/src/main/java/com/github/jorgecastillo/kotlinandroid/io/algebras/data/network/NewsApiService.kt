package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.dto.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("everything")
    fun fetchNews(@Query("q") query: String): Call<NewsResponse>
}
