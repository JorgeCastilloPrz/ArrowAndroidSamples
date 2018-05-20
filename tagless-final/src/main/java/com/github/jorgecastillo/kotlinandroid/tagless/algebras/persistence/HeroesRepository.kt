package com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence

import arrow.Kind
import com.karumi.marvelapiclient.model.CharacterDto
import javax.inject.Inject

/**
 * Repository operations defined as completely abstract over an F type. Part of the algebras. We
 * will fix F to a concrete type later on from a single point in the system. That helps us to
 * compose a complete program based on abstractions and provide implementation details later on.
 */
class HeroesRepository<F> @Inject constructor(val dataSource: DataSource<F>) {

    sealed class CachePolicy {
        object NetworkOnly : CachePolicy()
        object NetworkFirst : CachePolicy()
        object LocalOnly : CachePolicy()
        object LocalFirst : CachePolicy()
    }

    fun getHeroesWithCachePolicy(policy: CachePolicy): Kind<F, List<CharacterDto>> =
            when (policy) {
                CachePolicy.NetworkOnly -> dataSource.fetchAllHeroes()
                CachePolicy.NetworkFirst -> dataSource.fetchAllHeroes() // TODO change to conditional call
                CachePolicy.LocalOnly -> dataSource.fetchAllHeroes() // TODO change to local only cache call
                CachePolicy.LocalFirst -> dataSource.fetchAllHeroes() // TODO change to conditional call
            }

    fun getHeroDetails(policy: HeroesRepository.CachePolicy, heroId: String): Kind<F, CharacterDto> =
            when (policy) {
                CachePolicy.NetworkOnly -> dataSource.fetchHeroDetails(heroId)
                CachePolicy.NetworkFirst -> dataSource.fetchHeroDetails(heroId) // TODO change to conditional call
                CachePolicy.LocalOnly -> dataSource.fetchHeroDetails(heroId) // TODO change to local only cache call
                CachePolicy.LocalFirst -> dataSource.fetchHeroDetails(heroId) // TODO change to conditional call
            }

}
