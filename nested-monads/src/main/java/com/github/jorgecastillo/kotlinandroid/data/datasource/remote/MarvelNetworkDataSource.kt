package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.functional.Future
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kategory.Either.Left
import kategory.Either.Right
import kategory.*
import kategory.Reader
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
  Future {
    try {
      val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
      Right<List<CharacterDto>>(ctx.apiClient.getAll(query).response.characters)
    } catch (e: MarvelAuthApiException) {
      Left(AuthenticationError())
    } catch (e: MarvelApiException) {
      if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) {
        Left(NotFoundError())
      } else {
        Left(UnknownServerError())
      }
    }
  }
})

fun fetchHeroDetails(heroId: String) = Reader.ask<GetHeroDetailsContext>().map({ ctx ->
  Future {
    try {
      Right<List<CharacterDto>>(listOf(ctx.apiClient.getCharacter(heroId).response))
    } catch (e: MarvelAuthApiException) {
      Left(AuthenticationError())
    } catch (e: MarvelApiException) {
      if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) {
        Left(NotFoundError())
      } else {
        Left(UnknownServerError())
      }
    }
  }
})

fun fetchHeroesFromAvengerComics() = fetchAllHeroes().map({ future ->
  future.map {
    when (it) {
      is Right -> it.map {
        it.filter {
          it.comics.items.map { it.name }.filter {
            it.contains("Avenger", true)
          }.count() > 0
        }
      }
      is Left -> it
    }
  }
})
