package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.errors.Error
import com.github.jorgecastillo.kotlinandroid.domain.Result
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList

interface HeroesRepository {

  fun getHeroes(): Result<Error.HeroesNotFound, NonEmptyList<SuperHero>>
}
