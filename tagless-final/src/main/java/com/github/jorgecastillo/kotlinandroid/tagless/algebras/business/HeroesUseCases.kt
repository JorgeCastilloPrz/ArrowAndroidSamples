package com.github.jorgecastillo.kotlinandroid.tagless.algebras.business

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence.HeroesRepository
import com.karumi.marvelapiclient.model.CharacterDto
import javax.inject.Inject

/**
 * Use case operations defined as completely abstract over an F type. Part of the algebras. We
 * will fix F to a concrete type later on from a single point in the system. That helps us to
 * compose a complete program based on abstractions and provide implementation details later on.
 */
class HeroesUseCases<F> @Inject constructor(private val repository: HeroesRepository<F>) {

  fun getHeroes(): Kind<F, List<CharacterDto>> =
      repository.getHeroesWithCachePolicy(HeroesRepository.CachePolicy.NetworkOnly)

  fun getHeroDetails(heroId: String): Kind<F, CharacterDto> =
      repository.getHeroDetails(HeroesRepository.CachePolicy.NetworkOnly, heroId)
}
