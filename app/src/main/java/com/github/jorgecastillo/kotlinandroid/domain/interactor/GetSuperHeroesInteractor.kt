package com.github.jorgecastillo.kotlinandroid.domain.interactor

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.Reader

class GetSuperHeroesInteractor {

  fun getSuperHeroes(): Reader<GetHeroesContext, List<SuperHero>> =
      Reader.ask<GetHeroesContext>().flatMap { ctx -> ctx.heroesRepository.getHeroes() }
}
