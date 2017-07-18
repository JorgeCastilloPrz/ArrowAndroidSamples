package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComicsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.data.getHeroesWithCachePolicy
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

inline fun <reified F> getHeroesUseCase(): HK<F, List<CharacterDto>> =
    getHeroesWithCachePolicy()

inline fun <reified F> getHeroesFromAvengerComicsUseCase(): HK<F, List<CharacterDto>> =
    getHeroesFromAvengerComicsWithCachePolicy()
