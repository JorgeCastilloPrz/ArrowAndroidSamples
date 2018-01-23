package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import arrow.HK
import arrow.core.IdHK
import arrow.data.Reader
import arrow.data.Try
import arrow.data.map
import arrow.effects.Async
import arrow.effects.IO
import arrow.effects.monadSuspend
import arrow.syntax.either.right
import arrow.typeclasses.bindingCatch
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/*
 * This is the network data source. Calls are made using Karumi's MarvelApiClient.
 * @see "https://github.com/Karumi/MarvelApiClientAndroid"
 *
 * Both requests return a new Reader enclosing an action to resolve when you provide them with the
 * required execution context.
 */

fun fetchAllHeroes() = Reader.ask<IdHK, GetHeroesContext>().map({ ctx ->
  IO.monadSuspend().bindingCatch {
    runInAsyncContext(
        f = { queryForHeroes(ctx) },
        onError = { IO.raiseError<List<CharacterDto>>(it) },
        onSuccess = { IO.pure(it) },
        AC = ctx.threading
    ).bind()
  }
})

fun fetchHeroDetails(heroId: String) = Reader.ask<IdHK, GetHeroDetailsContext>().map({ ctx ->
  IO.monadSuspend().bindingCatch {
    runInAsyncContext(
        f = { queryForHero(ctx, heroId) },
        onError = { IO.raiseError<List<CharacterDto>>(it) },
        onSuccess = { IO.pure(it) },
        AC = ctx.threading).bind()
  }
})

private fun queryForHeroes(ctx: GetHeroesContext): List<CharacterDto> {
  val query = Builder.create().withOffset(0).withLimit(50).build()
  return ctx.apiClient.getAll(query).response.characters
}

private fun queryForHero(ctx: GetHeroDetailsContext, heroId: String): List<CharacterDto> =
    listOf(ctx.apiClient.getCharacter(heroId).response)

/**
 * Just syntax to improve readability.
 */
private fun <F, A, B> runInAsyncContext(
    f: () -> A,
    onError: (Throwable) -> B,
    onSuccess: (A) -> B, AC: Async<F>): HK<F, B> {
  return AC.async { proc ->
    async(CommonPool) {
      val result = Try { f() }.fold(onError, onSuccess)
      proc(result.right())
    }
  }
}
