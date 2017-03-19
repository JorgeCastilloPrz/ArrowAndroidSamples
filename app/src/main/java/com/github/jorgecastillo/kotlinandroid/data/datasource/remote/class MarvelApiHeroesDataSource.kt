package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource
import com.github.jorgecastillo.kotlinandroid.data.errors.Error
import com.github.jorgecastillo.kotlinandroid.domain.Result
import com.github.jorgecastillo.kotlinandroid.domain.binding
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.domain.raiseError
import com.github.jorgecastillo.kotlinandroid.domain.result
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.model.CharactersQuery

class MarvelApiHeroesDataSource(val apiClient: CharacterApiClient) : HeroesDataSource {

  override fun getAll(): Result<Error.HeroesNotFound, NonEmptyList<SuperHero>> =
      binding {
        val query = CharactersQuery.Builder.create().withOffset(0).withLimit(10).build()
        val characterDTOs = bind(apiClient.asyncResult(query))

        val superHeroes = characterDTOs.response.characters.map { mapApiCharacterToSuperHero(it) }

        val heroesResult: Result<Error.HeroesNotFound, NonEmptyList<SuperHero>> =
            if (superHeroes.isEmpty()) {
              Error.HeroesNotFound().raiseError()
            } else {
              NonEmptyList.unsafeFromList(superHeroes).result()
            }
        yields(bind(heroesResult))
      }
}

