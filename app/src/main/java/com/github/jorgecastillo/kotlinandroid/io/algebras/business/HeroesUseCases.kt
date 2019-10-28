package com.github.jorgecastillo.kotlinandroid.io.algebras.business

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.CachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.getHeroDetailsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.getHeroesWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import com.karumi.marvelapiclient.model.CharacterDto

fun <F> Runtime<F>.getHeroes(): Kind<F, List<CharacterDto>> =
        getHeroesWithCachePolicy(CachePolicy.NetworkOnly)

fun <F> Runtime<F>.getHeroDetails(heroId: String): Kind<F, CharacterDto> =
        getHeroDetailsWithCachePolicy(CachePolicy.NetworkOnly, heroId)
