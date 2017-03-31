package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.data.MarvelHeroesRepository
import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.StubHeroesDataSource
import com.github.jorgecastillo.kotlinandroid.domain.interactor.GetSuperHeroesInteractor
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView

data class GetHeroesContext(val view: SuperHeroesView) {

  val heroesDataSources = listOf<HeroesDataSource>(StubHeroesDataSource())
  val heroesRepository = MarvelHeroesRepository()
  val getSuperHeroesInteractor = GetSuperHeroesInteractor()
}
