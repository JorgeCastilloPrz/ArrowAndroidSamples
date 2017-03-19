package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.data.HeroesRepository
import com.github.jorgecastillo.kotlinandroid.data.errors.Error
import com.github.jorgecastillo.kotlinandroid.domain.Result
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList

class GetSuperHeroesInteractor(val repository: HeroesRepository) {

  fun getSuperHeroes(): Result<Error.HeroesNotFound, NonEmptyList<SuperHero>> =
      repository.getHeroes()
}
