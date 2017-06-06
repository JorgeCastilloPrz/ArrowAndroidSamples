package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import katz.Id
import katz.Reader

class GetSuperHeroesInteractor {

  fun get() = Reader.ask<GetHeroesContext>(Id).flatMap {
    it.heroesRepository.getHeroes()
  }
}
