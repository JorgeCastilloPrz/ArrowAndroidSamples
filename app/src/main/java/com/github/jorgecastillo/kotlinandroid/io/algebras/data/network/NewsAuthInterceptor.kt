package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import com.github.jorgecastillo.kotlinandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response


class NewsAuthInterceptor(private val apiKey: String = BuildConfig.NEWS_API_KEY) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url()
            .newBuilder()
            .addQueryParameter("apiKey", apiKey)
            .build()

        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}