package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kategory.Option
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
fun exceptionAsCharacterError(e: Throwable): CharacterError =
    when (e) {
      is MarvelAuthApiException -> AuthenticationError
      is MarvelApiException ->
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
        else UnknownServerError(Option.Some(e))
      else -> UnknownServerError((Option.Some(e)))
    }


fun fetchAllHeroes(): AsyncResult<List<CharacterDto>> =
    AsyncResult.bind {
      val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
      val ctx = AsyncResult.ask().bind()
      AsyncResult.catch(
          { ctx.apiClient.getAll(query).response.characters },
          { exceptionAsCharacterError(it) }
      ).ev()
    }

fun fetchHeroDetails(heroId: String): AsyncResult<List<CharacterDto>> =
    AsyncResult.bind {
      val ctx = AsyncResult.ask().bind()
      AsyncResult.catch(
          { listOf(ctx.apiClient.getCharacter(heroId).response) },
          { exceptionAsCharacterError(it) }
      ).ev()
    }

fun fetchHeroesFromAvengerComics(): AsyncResult<List<CharacterDto>> =
    fetchAllHeroes().map {
      it.filter {
        it.comics.items.map { it.name }.filter {
          it.contains("Avenger", true)
        }.count() > 0
      }
    }
