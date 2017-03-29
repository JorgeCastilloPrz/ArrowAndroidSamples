package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList
import com.github.jorgecastillo.kotlinandroid.lang.Reader
import com.github.jorgecastillo.kotlinandroid.presentation.mapper.mapSuperHeroToViewModel
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel

interface SuperHeroesView {

  fun drawHeroes(heroes: NonEmptyList<SuperHeroViewModel>)

  fun showHeroesNotFoundError()

  fun showServerError()
}

fun getSuperHeroes(): Reader<GetHeroesContext, Unit> = Reader {
  val c = it
  it.getSuperHeroesInteractor.getSuperHeroes().map {
    it.onComplete(
        onSuccess = { c.view.drawHeroes(it.map { mapSuperHeroToViewModel(it) }) },
        onError = { c.view.showHeroesNotFoundError() },
        onUnhandledException = { c.view.showServerError() }
    )
  }
}
