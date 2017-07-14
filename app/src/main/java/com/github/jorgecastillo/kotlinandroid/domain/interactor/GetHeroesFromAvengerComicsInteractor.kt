package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import kategory.Reader

class GetHeroesFromAvengerComicsInteractor {

  fun get() = Reader.ask<GetHeroesContext>().flatMap {
    it.heroesRepository.getHeroesFromAvengerComics()
  }
}
