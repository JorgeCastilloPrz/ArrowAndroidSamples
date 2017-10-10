package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.ev
import com.github.jorgecastillo.kotlinandroid.functional.monadError
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kategory.HK
import kategory.Option
import kategory.Try
import kategory.binding
import kategory.effects.AsyncContext
import kategory.right
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.net.HttpURLConnection

fun exceptionAsCharacterError(e: Throwable): CharacterError =
    when (e) {
      is MarvelAuthApiException -> AuthenticationError
      is MarvelApiException ->
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
        else UnknownServerError(Option.Some(e))
      else -> UnknownServerError((Option.Some(e)))
    }

fun <D : SuperHeroesContext> fetchAllHeroes(): AsyncResult<D, List<CharacterDto>> {
  val ME = AsyncResult.monadError<D>()
  return ME.binding {
    val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
    val ctx = AsyncResult.ask<D>().bind()
    runInAsyncContext(
        f = { ctx.apiClient.getAll(query).response.characters },
        onError = { ME.raiseError<List<CharacterDto>>(exceptionAsCharacterError(it)) },
        onSuccess = { AsyncResult.pure(it) },
        AC = ctx.threading<D>()
    ).bind()
  }.ev()
}

private fun <F, A, B> runInAsyncContext(
    f: () -> A,
    onError: (Throwable) -> B,
    onSuccess: (A) -> B, AC: AsyncContext<F>): HK<F, B> {
  return AC.runAsync { proc ->
    async(CommonPool) {
      val result = Try { f() }.fold(onError, onSuccess)
      proc(result.right())
    }
  }
}

fun <D : SuperHeroesContext> fetchHeroDetails(heroId: String): AsyncResult<D, List<CharacterDto>> {
  val ME = AsyncResult.monadError<D>()
  return ME.binding {
    val ctx = AsyncResult.ask<D>().bind()
    runInAsyncContext(
        f = { listOf(ctx.apiClient.getCharacter(heroId).response) },
        onError = { ME.raiseError<List<CharacterDto>>(exceptionAsCharacterError(it)) },
        onSuccess = { AsyncResult.pure(it) },
        AC = ctx.threading<D>()
    ).bind()
  }.ev()
}
