package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.NewsApiService
import com.github.jorgecastillo.kotlinandroid.io.runtime.NewsApplication
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.RuntimeContext

class TestApplication : NewsApplication() {

    override val runtimeContext: RuntimeContext by lazy {
        mockContext
    }

    override val newsService: NewsApiService by lazy {
        stubService
    }

    lateinit var mockContext: RuntimeContext
    lateinit var stubService: NewsApiService
}
