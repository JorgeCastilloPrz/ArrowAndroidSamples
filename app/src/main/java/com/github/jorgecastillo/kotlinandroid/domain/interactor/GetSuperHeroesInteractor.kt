package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.data.HeroesRepository

class GetSuperHeroesInteractor(val repository: HeroesRepository) {

  fun getSuperHeroes() = repository.getHeroes()
}
