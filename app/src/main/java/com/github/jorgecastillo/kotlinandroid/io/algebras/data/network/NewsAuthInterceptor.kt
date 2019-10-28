package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import com.github.jorgecastillo.kotlinandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response


class NewsAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url()
            .newBuilder()
            .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
            .build()

        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}