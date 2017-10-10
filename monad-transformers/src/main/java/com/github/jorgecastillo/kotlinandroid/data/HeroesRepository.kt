package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroDetails
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.effects.IO

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

fun <D : SuperHeroesContext> getHeroes(policy: CachePolicy): AsyncResult<D, IO<List<CharacterDto>>> = when (policy) {
  is NetworkOnly -> fetchAllHeroes()
  is NetworkFirst -> fetchAllHeroes() // TODO change to conditional call
  is LocalOnly -> fetchAllHeroes() // TODO change to local only cache call
  is LocalFirst -> fetchAllHeroes() // TODO change to conditional call
}

fun <D : SuperHeroesContext> getHeroDetails(policy: CachePolicy,
    heroId: String): AsyncResult<D, IO<List<CharacterDto>>> = when (policy) {
  is NetworkOnly -> fetchHeroDetails(heroId)
  is NetworkFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
  is LocalOnly -> fetchHeroDetails(heroId) // TODO change to local only cache call
  is LocalFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
}

