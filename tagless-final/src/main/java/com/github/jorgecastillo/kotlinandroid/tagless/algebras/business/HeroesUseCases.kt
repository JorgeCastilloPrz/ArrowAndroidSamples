package com.github.jorgecastillo.kotlinandroid.tagless.algebras.business

import arrow.HK
import arrow.TC
import arrow.typeclass
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence.HeroesRepository
import com.karumi.marvelapiclient.model.CharacterDto

@typeclass
interface HeroesUseCases<F> : TC {

    fun repository(): HeroesRepository<F>

    fun getHeroes(): HK<F, List<CharacterDto>> =
            repository().getHeroesWithCachePolicy(HeroesRepository.CachePolicy.NetworkOnly)

    fun getHeroDetails(heroId: String): HK<F, CharacterDto> =
            repository().getHeroDetails(HeroesRepository.CachePolicy.NetworkOnly, heroId)


}
