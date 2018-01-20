package com.github.jorgecastillo.kotlinandroid.domain.usecase

import arrow.HK
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroesWithCachePolicy
import com.karumi.marvelapiclient.model.CharacterDto

inline fun <reified F> getHeroesUseCase(): HK<F, List<CharacterDto>> =
    getHeroesWithCachePolicy(NetworkOnly)

inline fun <reified F> getHeroDetailsUseCase(heroId: String): HK<F, CharacterDto> =
    getHeroDetails(NetworkOnly, heroId)
