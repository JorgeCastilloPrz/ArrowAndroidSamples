package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import kategory.Either
import kategory.Either.Left
import kategory.Either.Right
import kategory.HK
import kategory.Reader
import kategory.Try
import kategory.effects.AsyncContext
import kategory.map
import kategory.right
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.net.HttpURLConnection

/*
 * This is the network data source. Calls are made using Karumi's MarvelApiClient.
 * @see "https://github.com/Karumi/MarvelApiClientAndroid"
 *
 * Both requests return a new Reader enclosing an action to resolve when you provide them with the
 * required execution context.
 *
 * The getHeroesFromAvengerComicsUseCase() method maps the fetchAllHeroes() result to filter the list with just the
 * elements with given conditions. It's returning heroes appearing on comics with the  "Avenger"
 * word in the title. Yep, I wanted to retrieve Avengers but the Marvel API is a bit weird
 * sometimes.
 */

fun fetchAllHeroes() = Reader.ask<GetHeroesContext>().map({ ctx ->
  runInAsyncContext(
      { queryForHeroes(ctx) },
      { mapExceptionsToDomainErrors(it) },
      { Right(it) },
      ctx.threading)
})

fun fetchHeroDetails(heroId: String) = Reader.ask<GetHeroDetailsContext>().map({ ctx ->
  runInAsyncContext(
      { queryForHero(ctx, heroId) },
      { mapExceptionsToDomainErrors(it) },
      { Right(it) },
      ctx.threading)
})

private fun queryForHeroes(ctx: GetHeroesContext): List<CharacterDto> {
  val query = Builder.create().withOffset(0).withLimit(50).build()
  return ctx.apiClient.getAll(query).response.characters
}

private fun queryForHero(ctx: GetHeroDetailsContext, heroId: String): List<CharacterDto> =
    listOf(ctx.apiClient.getCharacter(heroId).response)

private fun mapExceptionsToDomainErrors(
    it: Throwable): Either<CharacterError, Nothing> {
  return when (it as MarvelApiException) {
    is MarvelAuthApiException -> Left(AuthenticationError())
    else -> if (it.httpCode == HttpURLConnection.HTTP_NOT_FOUND) {
      Left(NotFoundError())
    } else {
      Left(UnknownServerError())
    }
  }
}

/**
 * Just syntax to improve readability.
 */
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
