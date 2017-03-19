package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.domain.interactor.GetSuperHeroesInteractor
import com.github.jorgecastillo.kotlinandroid.lang.ReaderContext

open class InteractorReaderContext : ReaderContext, RepositoryReaderContext() {

  val getSuperHeroesInteractor = GetSuperHeroesInteractor(heroesRepository)
}
