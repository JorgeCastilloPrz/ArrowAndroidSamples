package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComicsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.data.getHeroesWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.functional.MonadControl
import com.github.jorgecastillo.kotlinandroid.functional.monadControl
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

inline fun <reified F> getHeroesUseCase(): HK<F, List<CharacterDto>> =
    getHeroesWithCachePolicy(NetworkOnly)

inline fun <reified F> getHeroDetailsUseCase(heroId: String): HK<F, CharacterDto> =
    getHeroDetails(NetworkOnly, heroId)

inline fun <reified F> getHeroesFromAvengerComicsUseCase(): HK<F, List<CharacterDto>> =
    getHeroesFromAvengerComicsWithCachePolicy(NetworkOnly)
