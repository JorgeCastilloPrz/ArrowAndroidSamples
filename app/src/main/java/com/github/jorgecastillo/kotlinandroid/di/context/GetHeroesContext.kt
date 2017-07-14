package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.MarvelNetworkDataSource
import com.github.jorgecastillo.kotlinandroid.data.MarvelHeroesRepository
import com.github.jorgecastillo.kotlinandroid.domain.interactor.GetHeroesFromAvengerComicsUseCase
import com.github.jorgecastillo.kotlinandroid.domain.interactor.GetSuperHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView
import com.karumi.marvelapiclient.CharacterApiClient

data class GetHeroesContext(val view: SuperHeroesView) {

  val apiClient
    get() = CharacterApiClient(com.karumi.marvelapiclient.MarvelApiConfig.Builder(
        com.github.jorgecastillo.kotlinandroid.BuildConfig.MARVEL_PUBLIC_KEY,
        com.github.jorgecastillo.kotlinandroid.BuildConfig.MARVEL_PRIVATE_KEY).debug().build())

  val networkDataSource = MarvelNetworkDataSource()
  val heroesRepository = MarvelHeroesRepository()
  val getSuperHeroesUseCase = GetSuperHeroesUseCase()
  val getHeroesFromAvengerComicsUseCase = GetHeroesFromAvengerComicsUseCase()
}
