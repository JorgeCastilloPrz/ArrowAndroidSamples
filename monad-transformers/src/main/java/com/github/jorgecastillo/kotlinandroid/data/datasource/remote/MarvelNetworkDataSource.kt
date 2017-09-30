package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.functional.*
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kategory.*
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


fun <D : SuperHeroesContext> fetchAllHeroes(): AsyncResult<D, List<CharacterDto>> =
        AsyncResult.monad<D>().binding {
            val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
            val ctx = AsyncResult.ask<D>().bind()
            AsyncResult.monadError<D>().catch(
                    { ctx.apiClient.getAll(query).response.characters.toList() },
                    { exceptionAsCharacterError(it) }
            )
        }.ev()

fun <D : SuperHeroesContext> fetchHeroDetails(heroId: String): AsyncResult<D, CharacterDto> =
        AsyncResult.monad<D>().binding {
            val ctx = AsyncResult.ask<D>().bind()
            AsyncResult.monadError<D>().catch(
                    { ctx.apiClient.getCharacter(heroId).response },
                    { exceptionAsCharacterError(it) }
            ).ev()
        }.ev()

fun <D : SuperHeroesContext> fetchHeroesFromAvengerComics(): AsyncResult<D, List<CharacterDto>> =
        fetchAllHeroes<D>().map {
            it.filter {
                it.comics.items.map { it.name }.filter {
                    it.contains("Avenger", true)
                }.count() > 0
            }
        }
