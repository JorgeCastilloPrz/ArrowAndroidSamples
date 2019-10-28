package com.github.jorgecastillo.kotlinandroid.io.algebras.business.model

data class NewsItem(
    val source: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String
)
