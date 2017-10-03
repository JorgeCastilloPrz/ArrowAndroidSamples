package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.LocalOnly
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkFirst
import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.NetworkOnly
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroDetails
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroesFromAvengerComics
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.functional.MonadControl
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

inline fun <reified F> getHeroesWithCachePolicy(policy: CachePolicy,
    C: MonadControl<F, GetHeroesContext, CharacterError>): HK<F, List<CharacterDto>> = when (policy) {
  is NetworkOnly -> fetchAllHeroes(C)
  is NetworkFirst -> fetchAllHeroes(C) // TODO change to conditional call
  is LocalOnly -> fetchAllHeroes(C) // TODO change to local only cache call
  is LocalFirst -> fetchAllHeroes(C) // TODO change to conditional call
}

inline fun <reified F> getHeroDetails(policy: CachePolicy, heroId: String,
    C: MonadControl<F, GetHeroDetailsContext, CharacterError>): HK<F, CharacterDto> = when (policy) {
  is NetworkOnly -> fetchHeroDetails(heroId, C)
  is NetworkFirst -> fetchHeroDetails(heroId, C) // TODO change to conditional call
  is LocalOnly -> fetchHeroDetails(heroId, C) // TODO change to local only cache call
  is LocalFirst -> fetchHeroDetails(heroId, C) // TODO change to conditional call
}

inline fun <reified F> getHeroesFromAvengerComicsWithCachePolicy(policy: CachePolicy,
    C: MonadControl<F, GetHeroesContext, CharacterError>): HK<F, List<CharacterDto>> =
    when (policy) {
      is NetworkOnly -> fetchHeroesFromAvengerComics(C)
      is NetworkFirst -> fetchHeroesFromAvengerComics(C) // TODO change to conditional call
      is LocalOnly -> fetchHeroesFromAvengerComics(C) // TODO change to local only cache call
      is LocalFirst -> fetchHeroesFromAvengerComics(C) // TODO change to conditional call
    }
