package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper

import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.dto.NetworkNewsItem

fun List<NetworkNewsItem>.toDomain() = map { it.toDomain() }

fun NetworkNewsItem.toDomain() = NewsItem(
    source = source.name,
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)
