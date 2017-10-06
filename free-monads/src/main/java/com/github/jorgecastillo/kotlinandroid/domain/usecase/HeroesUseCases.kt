package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.free.algebra.FreeHeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.data.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.getHeroesFromAvengerComicsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.data.getHeroesWithCachePolicy
import com.karumi.marvelapiclient.model.CharacterDto

fun getHeroesUseCase(): FreeHeroesAlgebra<List<CharacterDto>> =
    getHeroesWithCachePolicy(NetworkOnly)

fun getHeroDetailsUseCase(heroId: String): FreeHeroesAlgebra<List<CharacterDto>> =
    getHeroDetails(NetworkOnly, heroId)

fun getHeroesFromAvengerComicsUseCase(): FreeHeroesAlgebra<List<CharacterDto>> =
    getHeroesFromAvengerComicsWithCachePolicy(NetworkOnly)
