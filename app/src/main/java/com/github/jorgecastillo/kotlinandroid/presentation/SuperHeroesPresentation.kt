package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.domain.interactor.GetSuperHeroesInteractor
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList
import com.github.jorgecastillo.kotlinandroid.presentation.mapper.mapSuperHeroToViewModel
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel

interface SuperHeroesView {

  fun drawHeroes(heroes: NonEmptyList<SuperHeroViewModel>)

  fun showHeroesNotFoundError()

  fun showServerError()
}

/**
 * Suspend function that will always run on a background thread using the CommonPool (ForkJoinPool).
 */
fun getSuperHeroes(view: SuperHeroesView,
    getSuperHeroesInteractor: GetSuperHeroesInteractor) {

  getSuperHeroesInteractor.getSuperHeroes().onComplete(
      onSuccess = { view.drawHeroes(it.map { mapSuperHeroToViewModel(it) }) },
      onError = { view.showHeroesNotFoundError() },
      onUnhandledException = { view.showServerError() }
  )
}
