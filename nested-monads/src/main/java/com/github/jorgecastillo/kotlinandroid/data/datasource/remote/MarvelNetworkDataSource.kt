package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import kategory.HK
import kategory.Reader
import kategory.Try
import kategory.bindingE
import kategory.effects.AsyncContext
import kategory.effects.IO
import kategory.effects.monadError
import kategory.map
import kategory.right
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

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
  IO.monadError().bindingE {
    runInAsyncContext(
        f = { queryForHeroes(ctx) },
        onError = { IO.raiseError<List<CharacterDto>>(it) },
        onSuccess = { IO.pure(it) },
        AC = ctx.threading
    ).bind()
  }
})

fun fetchHeroDetails(heroId: String) = Reader.ask<GetHeroDetailsContext>().map({ ctx ->
  IO.monadError().bindingE {
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
    onSuccess: (A) -> B, AC: AsyncContext<F>): HK<F, B> {
  return AC.runAsync { proc ->
    async(CommonPool) {
      val result = Try { f() }.fold(onError, onSuccess)
      proc(result.right())
    }
  }
}
