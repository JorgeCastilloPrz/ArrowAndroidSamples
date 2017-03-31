package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.Reader

class MarvelHeroesRepository {

  fun getHeroes() = Reader<GetHeroesContext, List<SuperHero>> {
    it.heroesDataSources[0].getAll().run(it)
  }
}
