package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.dto

data class NetworkNewsItem(
    val source: NetworkNewsSource,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String
)

data class NetworkNewsSource(
    val id: String?,
    val name: String
)