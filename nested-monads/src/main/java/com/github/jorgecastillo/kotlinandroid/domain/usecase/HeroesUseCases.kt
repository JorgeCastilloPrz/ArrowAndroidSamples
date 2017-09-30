package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroes
import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComics
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.Either
import kategory.Either.Left
import kategory.Either.Right
import kategory.*

fun getHeroesUseCase() = getHeroes(NetworkOnly).map({ future ->
  future.map { discardNonValidHeroes(it) }
})

private fun discardNonValidHeroes(maybeHeroes: Either<CharacterError, List<CharacterDto>>) =
    when (maybeHeroes) {
      is Right -> maybeHeroes.map {
        it.filter {
          !it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY)
              .contains("image_not_available", true)
        }
      }
      is Left -> maybeHeroes
    }

fun getHeroDetailsUseCase(heroId: String) = getHeroDetails(NetworkOnly, heroId)

fun getHeroesFromAvengerComicsUseCase() = getHeroesFromAvengerComics(NetworkOnly).map({ future ->
  future.map { discardNonValidHeroes(it) }
})
