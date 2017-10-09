package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroes
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.map

fun getHeroesUseCase() = getHeroes(NetworkOnly).map { io -> io.map { discardNonValidHeroes(it) } }

private fun discardNonValidHeroes(heroes: List<CharacterDto>) =
    heroes.filter {
      !it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY)
          .contains("image_not_available", true)
    }


fun getHeroDetailsUseCase(heroId: String) = getHeroDetails(NetworkOnly, heroId)

