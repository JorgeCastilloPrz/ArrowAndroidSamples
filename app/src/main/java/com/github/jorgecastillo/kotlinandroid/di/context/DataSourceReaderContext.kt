package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.MarvelApiHeroesDataSource
import com.github.jorgecastillo.kotlinandroid.lang.ReaderContext
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig

open class DataSourceReaderContext : ReaderContext, ApiClientReaderContext() {

  val heroesDataSources = listOf<HeroesDataSource>(MarvelApiHeroesDataSource(marvelApiClient))
}
