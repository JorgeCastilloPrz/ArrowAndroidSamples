package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComicsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.data.getHeroesWithCachePolicy

fun getHeroesUseCase() = getHeroesWithCachePolicy()

fun getHeroesFromAvengerComicsUseCase() = getHeroesFromAvengerComicsWithCachePolicy()
