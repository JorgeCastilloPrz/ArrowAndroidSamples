package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesReaderContext
import com.github.jorgecastillo.kotlinandroid.lang.NonEmptyList
import com.github.jorgecastillo.kotlinandroid.lang.Reader
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
fun getSuperHeroes(): Reader<GetHeroesReaderContext, Unit> = Reader {
  val readerContext = it
  it.getSuperHeroesInteractor.getSuperHeroes().onComplete(
      onSuccess = { readerContext.view.drawHeroes(it.map { mapSuperHeroToViewModel(it) }) },
      onError = { readerContext.view.showHeroesNotFoundError() },
      onUnhandledException = { readerContext.view.showServerError() }
  )
}
