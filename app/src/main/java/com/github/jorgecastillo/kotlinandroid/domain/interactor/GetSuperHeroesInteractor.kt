package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import katz.Reader

class GetSuperHeroesInteractor {

  fun get() = Reader.ask<GetHeroesContext>().flatMap {
    it.heroesRepository.getHeroes()
  }
}
