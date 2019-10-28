package com.github.jorgecastillo.kotlinandroid.io.algebras.business.model

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import java.net.HttpURLConnection

sealed class CharacterError {

  companion object {
    fun fromThrowable(e: Throwable): CharacterError =
        when (e) {
          is MarvelAuthApiException -> AuthenticationError
          is MarvelApiException ->
            if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
            else UnknownServerError(
                Some(e))
          else -> UnknownServerError(
              (Some(e)))
        }
  }

  object AuthenticationError : CharacterError()
  object NotFoundError : CharacterError()
  data class UnknownServerError(val e: Option<Throwable> = None) : CharacterError()
}
