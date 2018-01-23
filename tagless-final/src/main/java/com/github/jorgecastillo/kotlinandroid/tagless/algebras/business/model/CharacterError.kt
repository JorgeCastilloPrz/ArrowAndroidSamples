package com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.model

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import java.net.HttpURLConnection

/**
 * This sealed class represents all the possible errors that the app is going to model inside its
 * domain. All the exceptions / errors provoked by third party libraries or APIs are mapped to any
 * of the types defined on this class.
 *
 * Mapping exceptions to errors allows the domain use case functions to be referentially
 * transparent, which means that they are completely clear and straightforward about what they
 * return just by reading their public function output types.
 *
 * Other approaches like exceptions + callback propagation (to be able to surpass thread limits)
 * bring not required complexity to the architecture introducing asynchronous semantics.
 */
sealed class CharacterError {

    companion object {
        fun fromThrowable(e: Throwable): CharacterError =
                when (e) {
                    is MarvelAuthApiException -> CharacterError.AuthenticationError
                    is MarvelApiException ->
                        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) CharacterError.NotFoundError
                        else CharacterError.UnknownServerError(Some(e))
                    else -> CharacterError.UnknownServerError((Some(e)))
                }
    }

    object AuthenticationError : CharacterError()
    object NotFoundError : CharacterError()
    data class UnknownServerError(val e: Option<Throwable> = None) : CharacterError()
}
