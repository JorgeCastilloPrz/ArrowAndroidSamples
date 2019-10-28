package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.dto

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NetworkNewsItem>
)