package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.data.MarvelHeroesRepository
import com.github.jorgecastillo.kotlinandroid.lang.ReaderContext

open class RepositoryReaderContext : ReaderContext, DataSourceReaderContext() {

  val heroesRepository = MarvelHeroesRepository(heroesDataSources)
}
