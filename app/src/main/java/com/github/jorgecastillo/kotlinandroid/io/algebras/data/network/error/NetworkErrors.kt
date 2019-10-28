package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.error

sealed class NetworkError : Throwable() {
    object Unauthorized : NetworkError()
    object NotFound : NetworkError()
    object ServerError : NetworkError()
}
