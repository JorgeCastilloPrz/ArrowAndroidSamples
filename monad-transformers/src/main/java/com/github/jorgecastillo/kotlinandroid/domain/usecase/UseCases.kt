package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComicsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.data.getHeroesWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

fun getHeroesUseCase(): AsyncResult<List<CharacterDto>> =
    getHeroesWithCachePolicy()

fun getHeroesFromAvengerComicsUseCase(): AsyncResult<List<CharacterDto>> =
    getHeroesFromAvengerComicsWithCachePolicy()
