package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.Kind
import arrow.core.Try
import arrow.core.right
import arrow.effects.IO
import arrow.effects.async
import arrow.effects.fix
import arrow.effects.monadError
import arrow.effects.typeclasses.Async
import arrow.typeclasses.binding
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. This is equivalent, but already fixing the type F to IO, for simplicity. Sometimes you're
 * okay fixing the type to some concrete type you know will fulfill your needs for all the cases.
 * But remember: you're losing polymorphism on your program when doing this.
 */
object DataSource {

  private val apiClient
    get() = CharacterApiClient(MarvelApiConfig.Builder(
        BuildConfig.MARVEL_PUBLIC_KEY,
        BuildConfig.MARVEL_PRIVATE_KEY).debug().build())

  private fun buildFetchHeroesQuery(): CharactersQuery =
      CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()

  private fun fetchHero(heroId: String) =
      apiClient.getCharacter(heroId).response

  private fun fetchHeroes(query: CharactersQuery): List<CharacterDto> =
      apiClient.getAll(query).response.characters

  private fun <F, A, B> runInAsyncContext(
      f: () -> A,
      onError: (Throwable) -> B,
      onSuccess: (A) -> B, AC: Async<F>): Kind<F, B> {
    return AC.async { proc ->
      async(CommonPool) {
        val result = Try { f() }.fold(onError, onSuccess)
        proc(result.right())
      }
    }
  }

  fun fetchAllHeroes(): IO<List<CharacterDto>> {
    val monadError = IO.monadError()
    return monadError.binding {
      val query = buildFetchHeroesQuery()
      val result = runInAsyncContext(
          f = { fetchHeroes(query) },
          onError = { monadError.raiseError<List<CharacterDto>>(it) },
          onSuccess = { monadError.just(it) },
          AC = IO.async()
      ).bind()
      result.bind()
    }.fix()
  }

  fun fetchHeroDetails(heroId: String): IO<CharacterDto> {
    val monadError = IO.monadError()
    return monadError.binding {
      val result = runInAsyncContext(
          f = { fetchHero(heroId) },
          onError = { monadError.raiseError<CharacterDto>(it) },
          onSuccess = { monadError.just(it) },
          AC = IO.async()
      ).bind()
      result.bind()
    }.fix()
  }
}
