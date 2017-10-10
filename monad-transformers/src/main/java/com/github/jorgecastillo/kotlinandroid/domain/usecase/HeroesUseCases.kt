package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroes
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage

fun <D : SuperHeroesContext> getHeroesUseCase(): AsyncResult<D, List<CharacterDto>> =
    getHeroes<D>(NetworkOnly).map { discardNonValidHeroes(it) }

fun <D : SuperHeroesContext> getHeroDetailsUseCase(heroId: String): AsyncResult<D, List<CharacterDto>> =
    getHeroDetails(NetworkOnly, heroId)

private fun discardNonValidHeroes(heroes: List<CharacterDto>) =
    heroes.filter {
      !it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY)
          .contains("image_not_available", true)
    }
