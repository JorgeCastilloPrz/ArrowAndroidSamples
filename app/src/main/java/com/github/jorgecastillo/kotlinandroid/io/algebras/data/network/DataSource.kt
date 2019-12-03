package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import arrow.Kind
import arrow.core.toOption
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.dto.NewsResponse
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.error.NetworkError.NotFound
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.error.NetworkError.ServerError
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.normalizeError
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.toDomain
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.mapper.toNetworkError
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import retrofit2.Response

fun <F> Runtime<F>.loadNews(): Kind<F, List<NewsItem>> = fx.concurrent {
    continueOn(context.bgDispatcher)
    val response = !effect { fetchNews() }
    continueOn(context.mainDispatcher)

    if (response.isSuccessful) {
        response.news().toDomain()
    } else {
        !raiseError<List<NewsItem>>(response.code().toNetworkError())
    }
}.handleErrorWith { error -> raiseError(error.normalizeError()) }

fun <F> Runtime<F>.loadNewsItemDetails(title: String): Kind<F, NewsItem> = fx.concurrent {
    val response = !effect(context.bgDispatcher) { fetchNews() }
    continueOn(context.mainDispatcher)

    if (response.isSuccessful) {
        response.news().find { it.title == title }?.toDomain().toOption().fold(
            ifEmpty = { !raiseError<NewsItem>(NotFound) },
            ifSome = { it }
        )
    } else {
        !raiseError<NewsItem>(response.code().toNetworkError())
    }
}.handleErrorWith { error -> raiseError(error.normalizeError()) }

private fun <F> Runtime<F>.fetchNews() = context.newsService.fetchNews("android").execute()

private fun Response<NewsResponse>.news() = body()!!.articles
