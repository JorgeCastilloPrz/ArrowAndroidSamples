package com.github.jorgecastillo.architecturecomponentssample.sourcesofdata.network

import android.arch.lifecycle.LiveData
import com.github.jorgecastillo.architecturecomponentssample.model.error.CharacterError
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import katz.Either
import katz.Either.Left
import katz.Either.Right
import java.net.HttpURLConnection
import javax.inject.Inject

class MarvelNetworkDataSource @Inject constructor(val apiClient: CharacterApiClient) {

  fun getAll(): Either<CharacterError, LiveData<List<CharacterDto>>> =
      try {
        val query = CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()
        Right<LiveData<List<CharacterDto>>>(
            apiClient.getAll(query).response.characters.let {
              object : LiveData<List<CharacterDto>>() {
                override fun getValue(): List<CharacterDto>? {
                  return it
                }
              }
            })
      } catch (e: MarvelAuthApiException) {
        Left(CharacterError.AuthenticationError())
      } catch (e: MarvelApiException) {
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) {
          Left(CharacterError.NotFoundError())
        } else {
          Left(CharacterError.UnknownServerError())
        }
      }

  fun getAvengers(): Either<CharacterError, LiveData<List<CharacterDto>>> = getAll().let {
    res ->
    when (res) {
      is Right -> res.map {
        it.value!!.filter {
          it.comics.items.map { it.name }.filter {
            it.contains("Avenger", true)
          }.count() > 0
        }.let {
          object : LiveData<List<CharacterDto>>() {
            override fun getValue(): List<CharacterDto>? {
              return it
            }
          }
        }
      }
      is Left -> res
    }
  }
}
