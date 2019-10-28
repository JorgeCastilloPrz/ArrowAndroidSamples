package com.github.jorgecastillo.kotlinandroid.io.algebras.data

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.CachePolicy.*
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.loadNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.loadNewsItemDetails
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

sealed class CachePolicy {
    object NetworkOnly : CachePolicy()
    object NetworkFirst : CachePolicy()
    object LocalOnly : CachePolicy()
    object LocalFirst : CachePolicy()
}

fun <F> Runtime<F>.getNewsWithCachePolicy(policy: CachePolicy): Kind<F, List<NewsItem>> =
    when (policy) {
        NetworkOnly -> loadNews()
        NetworkFirst -> loadNews() // TODO change to conditional call
        LocalOnly -> loadNews() // TODO change to local only cache call
        LocalFirst -> loadNews() // TODO change to conditional call
    }

fun <F> Runtime<F>.getNewsItemDetailsWithCachePolicy(policy: CachePolicy, title: String): Kind<F, NewsItem> =
    when (policy) {
        NetworkOnly -> loadNewsItemDetails(title)
        NetworkFirst -> loadNewsItemDetails(title) // TODO change to conditional call
        LocalOnly -> loadNewsItemDetails(title) // TODO change to local only cache call
        LocalFirst -> loadNewsItemDetails(title) // TODO change to conditional call
    }
