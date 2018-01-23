package com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence

import arrow.HK
import arrow.TC
import arrow.typeclass
import com.karumi.marvelapiclient.model.CharacterDto

@typeclass
interface HeroesRepository<F> : TC {

    sealed class CachePolicy {
        object NetworkOnly : CachePolicy()
        object NetworkFirst : CachePolicy()
        object LocalOnly : CachePolicy()
        object LocalFirst : CachePolicy()
    }

    fun dataSource(): DataSource<F>

    fun getHeroesWithCachePolicy(policy: CachePolicy): HK<F, List<CharacterDto>> =
            when (policy) {
                CachePolicy.NetworkOnly -> dataSource().fetchAllHeroes()
                CachePolicy.NetworkFirst -> dataSource().fetchAllHeroes() // TODO change to conditional call
                CachePolicy.LocalOnly -> dataSource().fetchAllHeroes() // TODO change to local only cache call
                CachePolicy.LocalFirst -> dataSource().fetchAllHeroes() // TODO change to conditional call
            }

    fun getHeroDetails(policy: HeroesRepository.CachePolicy, heroId: String): HK<F, CharacterDto> =
            when (policy) {
                CachePolicy.NetworkOnly -> dataSource().fetchHeroDetails(heroId)
                CachePolicy.NetworkFirst -> dataSource().fetchHeroDetails(heroId) // TODO change to conditional call
                CachePolicy.LocalOnly -> dataSource().fetchHeroDetails(heroId) // TODO change to local only cache call
                CachePolicy.LocalFirst -> dataSource().fetchHeroDetails(heroId) // TODO change to conditional call
            }

}
