package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroDetails
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

inline fun <reified F> getHeroesWithCachePolicy(policy: CachePolicy): HK<F, List<CharacterDto>> = when (policy) {
  is NetworkOnly -> fetchAllHeroes()
  is NetworkFirst -> fetchAllHeroes() // TODO change to conditional call
  is LocalOnly -> fetchAllHeroes() // TODO change to local only cache call
  is LocalFirst -> fetchAllHeroes() // TODO change to conditional call
}

inline fun <reified F> getHeroDetails(policy: CachePolicy, heroId: String): HK<F, CharacterDto> = when (policy) {
  is NetworkOnly -> fetchHeroDetails(heroId)
  is NetworkFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
  is LocalOnly -> fetchHeroDetails(heroId) // TODO change to local only cache call
  is LocalFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
}
