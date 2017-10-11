package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.functional.MonadControl
import com.github.jorgecastillo.kotlinandroid.functional.monadControl
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import kategory.HK
import kategory.Option
import kategory.Try
import kategory.binding
import kategory.effects.AsyncContext
import kategory.right
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.net.HttpURLConnection

inline fun <reified F> fetchAllHeroes(
    C: MonadControl<F, GetHeroesContext, CharacterError> = monadControl()): HK<F, List<CharacterDto>> =
    C.binding {
      val query = buildFetchHeroesQuery()
      val ctx = C.ask().bind()
      runInAsyncContext(
          f = { fetchHeroes(ctx, query) },
          onError = { liftError(C, it) },
          onSuccess = { liftSuccess(C, it) },
          AC = C
      ).bind()
    }

inline fun <reified F> fetchHeroDetails(heroId: String,
    C: MonadControl<F, GetHeroDetailsContext, CharacterError> = monadControl()): HK<F, List<CharacterDto>> =
    C.binding {
      val ctx = C.ask().bind()
      runInAsyncContext(
          f = { fetchHero(ctx, heroId) },
          onError = { liftError(C, it) },
          onSuccess = { liftSuccess(C, it) },
          AC = C
      ).bind()
    }

fun <D : SuperHeroesContext> fetchHero(ctx: D, heroId: String) = listOf(ctx.apiClient.getCharacter(heroId).response)

fun buildFetchHeroesQuery(): CharactersQuery = Builder.create().withOffset(0).withLimit(50).build()

fun <D : SuperHeroesContext> fetchHeroes(ctx: D, query: CharactersQuery): List<CharacterDto> = ctx.apiClient.getAll(
    query).response.characters

fun <F, D : SuperHeroesContext> liftError(C: MonadControl<F, D, CharacterError>,
    it: Throwable) = C.raiseError<List<CharacterDto>>(exceptionAsCharacterError(it))

fun <F, D : SuperHeroesContext> liftSuccess(C: MonadControl<F, D, CharacterError>, it: List<CharacterDto>) = C.pure(it)

fun exceptionAsCharacterError(e: Throwable): CharacterError =
    when (e) {
      is MarvelAuthApiException -> AuthenticationError
      is MarvelApiException ->
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
        else UnknownServerError(Option.Some(e))
      else -> UnknownServerError((Option.Some(e)))
    }

fun <F, A, B> runInAsyncContext(
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

