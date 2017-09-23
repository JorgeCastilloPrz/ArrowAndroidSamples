package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroesFromAvengerComics

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

fun getHeroes(policy: CachePolicy) = when (policy) {
  is NetworkOnly -> fetchAllHeroes()
  is NetworkFirst -> fetchAllHeroes() // TODO change to conditional call
  is LocalOnly -> fetchAllHeroes() // TODO change to local only cache call
  is LocalFirst -> fetchAllHeroes() // TODO change to conditional call
}

fun getHeroDetails(policy: CachePolicy, heroId: String) = when (policy) {
  is NetworkOnly -> fetchHeroDetails(heroId)
  is NetworkFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
  is LocalOnly -> fetchHeroDetails(heroId) // TODO change to local only cache call
  is LocalFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
}

fun getHeroesFromAvengerComics(policy: CachePolicy) = when (policy) {
  is NetworkOnly -> fetchHeroesFromAvengerComics()
  is NetworkFirst -> fetchHeroesFromAvengerComics() // TODO change to conditional call
  is LocalOnly -> fetchHeroesFromAvengerComics() // TODO change to local only cache call
  is LocalFirst -> fetchHeroesFromAvengerComics() // TODO change to conditional call
}
