package com.github.jorgecastillo.kotlinandroid.io.runtime

import android.app.Application
import android.content.Context
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.NewsApiService
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.NewsAuthInterceptor
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.RuntimeContext
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

open class NewsApplication : Application() {

    open val runtimeContext by lazy {
        RuntimeContext(
            bgDispatcher = Dispatchers.IO,
            mainDispatcher = Dispatchers.Main,
            newsService = newsService
        )
    }

    protected open val newsService: NewsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()

        retrofit.create<NewsApiService>(NewsApiService::class.java)
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addNetworkInterceptor(NewsAuthInterceptor())
            .build()
    }
}

fun Context.application(): NewsApplication = applicationContext as NewsApplication
