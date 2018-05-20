package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import arrow.Kind
import arrow.core.Try
import arrow.core.right
import arrow.data.Reader
import arrow.data.ReaderApi
import arrow.data.map
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.monadDefer
import arrow.effects.typeclasses.Async
import arrow.typeclasses.binding
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

fun fetchAllHeroes(): Reader<GetHeroesContext, IO<List<CharacterDto>>> =
    ReaderApi.ask<GetHeroesContext>().map({ ctx ->
      IO.monadDefer().binding {
        val result = runInAsyncContext(
            f = { queryForHeroes(ctx) },
            onError = { IO.raiseError<List<CharacterDto>>(it) },
            onSuccess = { IO.just(it) },
            AC = ctx.threading
        ).bind()
        result.bind()
      }.fix()
    })

fun fetchHeroDetails(heroId: String): Reader<GetHeroDetailsContext, IO<List<CharacterDto>>> =
    ReaderApi.ask<GetHeroDetailsContext>().map({ ctx ->
      IO.monadDefer().bindingCatch {
        val result = runInAsyncContext(
            f = { queryForHero(ctx, heroId) },
            onError = { IO.raiseError<List<CharacterDto>>(it) },
            onSuccess = { IO.just(it) },
            AC = ctx.threading).bind()
        result.bind()
      }.fix()
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
    onSuccess: (A) -> B, AC: Async<F>
): Kind<F, B> {
  return AC.async { proc ->
    async(CommonPool) {
      val result = Try { f() }.fold(onError, onSuccess)
      proc(result.right())
    }
  }
}
