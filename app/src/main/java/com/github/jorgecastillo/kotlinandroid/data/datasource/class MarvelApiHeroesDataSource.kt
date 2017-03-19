package com.github.jorgecastillo.kotlinandroid.data.datasource

import com.github.jorgecastillo.kotlinandroid.data.errors.Error
import com.github.jorgecastillo.kotlinandroid.domain.Result
import com.github.jorgecastillo.kotlinandroid.domain.binding
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList
import com.karumi.marvelapiclient.CharacterApiClient

class MarvelApiHeroesDataSource(val apiClient: CharacterApiClient) : HeroesDataSource {

  override fun getAll(): Result<Error.HeroesNotFound, NonEmptyList<SuperHero>> =
    binding {
      val response = bind(apiClient.requestHeroes().asyncResult())
      val album = AlbumMapper().map(response.album)
      val albumResult: Result<AlbumNotFound, Album> =
          if (album.isEmpty()) {
            AlbumNotFound(id).raiseError()
          } else {
            album.get().result()
          }
      yields(bind(albumResult))
    }
}

