package com.github.jorgecastillo.architecturecomponentssample.sourcesofdata.network

import com.github.jorgecastillo.architecturecomponentssample.model.error.CharacterError
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import katz.Either.Left
import katz.Either.Right
import katz.Id
import katz.Reader
import java.net.HttpURLConnection

/**
 * This is the network data source. Calls are made using Karumi's MarvelApiClient.
 * @see "https://github.com/Karumi/MarvelApiClientAndroid"
 *
 * Both requests return a new Reader enclosing an action to resolve when you provide them with the
 * required execution context.
 *
 * The getHeroesFromAvengerComics() method maps the getAll() result to filter the list with just the
 * elements with given conditions. It's returning heroes appearing on comics with the  "Avenger"
 * word in the title. Yep, I wanted to retrieve Avengers but the Marvel API is a bit weird
 * sometimes.
 */
class MarvelNetworkDataSource {

  fun getAll() = Reader.ask<GetHeroesContext>(Id).map {
    ctx ->
    try {
      val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
      Right<List<CharacterDto>>(ctx.apiClient.getAll(query).response.characters)
    } catch (e: MarvelAuthApiException) {
      Left(CharacterError.AuthenticationError())
    } catch (e: MarvelApiException) {
      if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) {
        Left(CharacterError.NotFoundError())
      } else {
        Left(CharacterError.UnknownServerError())
      }
    }
  }

  fun getHeroesFromAvengerComics() = getAll().map { res ->
    when (res) {
      is Right -> res.map {
        it.filter {
          it.comics.items.map { it.name }.filter {
            it.contains("Avenger", true)
          }.count() > 0
        }
      }
      is Left -> res
    }
  }
}
