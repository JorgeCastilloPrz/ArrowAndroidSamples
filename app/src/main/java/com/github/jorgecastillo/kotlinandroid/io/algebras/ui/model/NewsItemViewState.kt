package com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model

import java.util.*

data class NewsItemViewState(
    val title: String,
    val author: String?,
    val photoUrl: String?,
    val publishedAt: String,
    val description: String?,
    val content: String)
