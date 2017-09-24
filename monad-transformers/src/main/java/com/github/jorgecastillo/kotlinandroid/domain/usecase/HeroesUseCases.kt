package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroes
import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComics
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage

fun getHeroesUseCase(): AsyncResult<List<CharacterDto>> =
    getHeroes(NetworkOnly).map { discardNonValidHeroes(it) }

fun getHeroDetailsUseCase(heroId: String) = getHeroDetails(NetworkOnly, heroId)

fun getHeroesFromAvengerComicsUseCase(): AsyncResult<List<CharacterDto>> =
    getHeroesFromAvengerComics(NetworkOnly).map { discardNonValidHeroes(it) }

private fun discardNonValidHeroes(heroes: List<CharacterDto>) =
    heroes.filter {
      !it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY)
          .contains("image_not_available", true)
    }
