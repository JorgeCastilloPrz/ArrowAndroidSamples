package com.github.jorgecastillo.kotlinandroid.io.algebras.business

import arrow.effects.IO
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository
import com.karumi.marvelapiclient.model.CharacterDto

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. This is equivalent, but already fixing the type F to IO, for simplicity. Sometimes you're
 * okay fixing the type to some concrete type you know will fulfill your needs for all the cases.
 * But remember: you're losing polymorphism on your program when doing this.
 */
object HeroesUseCases {

  fun getHeroes(): IO<List<CharacterDto>> =
      HeroesRepository.getHeroesWithCachePolicy(
          HeroesRepository.CachePolicy.NetworkOnly)

  fun getHeroDetails(heroId: String): IO<CharacterDto> =
      HeroesRepository.getHeroDetails(HeroesRepository.CachePolicy.NetworkOnly, heroId)
}
