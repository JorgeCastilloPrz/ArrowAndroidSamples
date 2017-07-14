package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import kategory.Reader

class GetHeroesFromAvengerComicsUseCase {

  fun get() = Reader.ask<GetHeroesContext>().flatMap {
    it.heroesRepository.getHeroesFromAvengerComics()
  }
}
