package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.effects.IO
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository.CachePolicy.*
import com.karumi.marvelapiclient.model.CharacterDto

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. This is equivalent, but already fixing the type F to IO, for simplicity. Sometimes you're
 * okay fixing the type to some concrete type you know will fulfill your needs for all the cases.
 * But remember: you're losing polymorphism on your program when doing this.
 */
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
