package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.free.algebra.FreeHeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.free.algebra.getAllFromAvengerComics
import com.github.jorgecastillo.kotlinandroid.free.algebra.getAllHeroes
import com.github.jorgecastillo.kotlinandroid.free.algebra.getSingleHero
import com.karumi.marvelapiclient.model.CharacterDto

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

fun getHeroesWithCachePolicy(policy: CachePolicy): FreeHeroesAlgebra<List<CharacterDto>> =
    when (policy) {
      is NetworkOnly -> getAllHeroes()
      is NetworkFirst -> getAllHeroes() // TODO change to conditional call
      is LocalOnly -> getAllHeroes() // TODO change to local only cache call
      is LocalFirst -> getAllHeroes() // TODO change to conditional call
    }

fun getHeroDetails(policy: CachePolicy, heroId: String): FreeHeroesAlgebra<List<CharacterDto>> =
    when (policy) {
      is NetworkOnly -> getSingleHero(heroId)
      is NetworkFirst -> getSingleHero(heroId) // TODO change to conditional call
      is LocalOnly -> getSingleHero(heroId) // TODO change to local only cache call
      is LocalFirst -> getSingleHero(heroId) // TODO change to conditional call
    }

fun getHeroesFromAvengerComicsWithCachePolicy(policy: CachePolicy): FreeHeroesAlgebra<List<CharacterDto>> =
    when (policy) {
      is NetworkOnly -> getAllFromAvengerComics()
      is NetworkFirst -> getAllFromAvengerComics() // TODO change to conditional call
      is LocalOnly -> getAllFromAvengerComics() // TODO change to local only cache call
      is LocalFirst -> getAllFromAvengerComics() // TODO change to conditional call
    }
