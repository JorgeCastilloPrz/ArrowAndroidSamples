package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.effects.IO
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository.CachePolicy.NetworkOnly
import com.karumi.marvelapiclient.model.CharacterDto

object HeroesRepository {

  sealed class CachePolicy {
    object NetworkOnly : CachePolicy()
    object NetworkFirst : CachePolicy()
    object LocalOnly : CachePolicy()
    object LocalFirst : CachePolicy()
  }

  fun getHeroesWithCachePolicy(policy: CachePolicy): IO<List<CharacterDto>> =
      when (policy) {
        NetworkOnly -> DataSource.fetchAllHeroes()
        NetworkFirst -> DataSource.fetchAllHeroes() // TODO change to conditional call
        LocalOnly -> DataSource.fetchAllHeroes() // TODO change to local only cache call
        LocalFirst -> DataSource.fetchAllHeroes() // TODO change to conditional call
      }

  fun getHeroDetails(policy: CachePolicy, heroId: String): IO<CharacterDto> =
      when (policy) {
        NetworkOnly -> DataSource.fetchHeroDetails(heroId)
        NetworkFirst -> DataSource.fetchHeroDetails(heroId) // TODO change to conditional call
        LocalOnly -> DataSource.fetchHeroDetails(heroId) // TODO change to local only cache call
        LocalFirst -> DataSource.fetchHeroDetails(heroId) // TODO change to conditional call
      }

}
