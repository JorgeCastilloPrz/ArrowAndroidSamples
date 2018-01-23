package com.github.jorgecastillo.kotlinandroid.io.algebras.business

import arrow.effects.IO
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository
import com.karumi.marvelapiclient.model.CharacterDto

object HeroesUseCases {

  fun getHeroes(): IO<List<CharacterDto>> =
      HeroesRepository.getHeroesWithCachePolicy(
          HeroesRepository.CachePolicy.NetworkOnly)

  fun getHeroDetails(heroId: String): IO<CharacterDto> =
      HeroesRepository.getHeroDetails(HeroesRepository.CachePolicy.NetworkOnly, heroId)
}
