package com.github.jorgecastillo.kotlinandroid.data.datasource

import com.github.jorgecastillo.kotlinandroid.data.errors.Error
import com.github.jorgecastillo.kotlinandroid.domain.Result
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList

interface HeroesDataSource {

  fun getAll(): Result<Error.HeroesNotFound, NonEmptyList<SuperHero>>
}
