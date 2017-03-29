package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.data.MarvelHeroesRepository
import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.MarvelApiHeroesDataSource
import com.github.jorgecastillo.kotlinandroid.domain.interactor.GetSuperHeroesInteractor
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig

class GetHeroesContext(val view: SuperHeroesView) {

  val publicKey = "1ecec67411f89e8b9bd52681844bd41b"
  val privateKey = "a7b24cc0e3d776f57193ac4ec1a3e0491e251e06"

  val apiClientConfig = MarvelApiConfig.Builder(publicKey, privateKey).debug().build()!!
  val marvelApiClient = CharacterApiClient(apiClientConfig)
  val heroesDataSources = listOf<HeroesDataSource>(MarvelApiHeroesDataSource(marvelApiClient))
  val heroesRepository = MarvelHeroesRepository(heroesDataSources)
  val getSuperHeroesInteractor = GetSuperHeroesInteractor(heroesRepository)
}
