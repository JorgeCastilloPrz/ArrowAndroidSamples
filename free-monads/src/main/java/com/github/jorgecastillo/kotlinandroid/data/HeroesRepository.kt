package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.algebra.FreeHeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.data.algebra.getAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.interpreter.getHeroDetails
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

inline fun <reified F> getHeroesWithCachePolicy(policy: CachePolicy): FreeHeroesAlgebra<List<CharacterDto>> = when (policy) {
  is NetworkOnly -> getAllHeroes()
  is NetworkFirst -> getAllHeroes() // TODO change to conditional call
  is LocalOnly -> getAllHeroes() // TODO change to local only cache call
  is LocalFirst -> getAllHeroes() // TODO change to conditional call
}

inline fun <reified F> getHeroDetails(policy: CachePolicy, heroId: String): FreeHeroesAlgebra<List<CharacterDto>> = when (policy) {
  is NetworkOnly -> getHeroDetails(heroId)
  is NetworkFirst -> getHeroDetails(
      heroId) // TODO change to conditional call
  is LocalOnly -> getHeroDetails(
      heroId) // TODO change to local only cache call
  is LocalFirst -> getHeroDetails(
      heroId) // TODO change to conditional call
}

inline fun <reified F> getHeroesFromAvengerComicsWithCachePolicy(policy: CachePolicy): HK<F, List<CharacterDto>> =
    when (policy) {
      is NetworkOnly -> fetchHeroesFromAvengerComics()
      is NetworkFirst -> fetchHeroesFromAvengerComics() // TODO change to conditional call
      is LocalOnly -> fetchHeroesFromAvengerComics() // TODO change to local only cache call
      is LocalFirst -> fetchHeroesFromAvengerComics() // TODO change to conditional call
    }
