package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.ev
import com.github.jorgecastillo.kotlinandroid.functional.monad
import com.github.jorgecastillo.kotlinandroid.functional.monadError
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kategory.Option
import kategory.binding
import kategory.effects.IO
import java.net.HttpURLConnection

fun exceptionAsCharacterError(e: Throwable): CharacterError =
    when (e) {
      is MarvelAuthApiException -> AuthenticationError
      is MarvelApiException ->
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
        else UnknownServerError(Option.Some(e))
      else -> UnknownServerError((Option.Some(e)))
    }

fun <D : SuperHeroesContext> fetchAllHeroes(): AsyncResult<D, IO<List<CharacterDto>>> =
    AsyncResult.monad<D>().binding {
      val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
      val ctx = AsyncResult.ask<D>().bind()
      AsyncResult.monadError<D>().catch(
          { ctx.threading.runAsync<List<CharacterDto>> { ctx.apiClient.getAll(query).response.characters } },
          { exceptionAsCharacterError(it) }
      )
    }.ev()

fun <D : SuperHeroesContext> fetchHeroDetails(heroId: String): AsyncResult<D, IO<List<CharacterDto>>> =
    AsyncResult.monad<D>().binding {
      val ctx = AsyncResult.ask<D>().bind()
      AsyncResult.monadError<D>().catch(
          { ctx.threading.runAsync<List<CharacterDto>> { listOf(ctx.apiClient.getCharacter(heroId).response) } },
          { exceptionAsCharacterError(it) }
      ).ev()
    }.ev()

