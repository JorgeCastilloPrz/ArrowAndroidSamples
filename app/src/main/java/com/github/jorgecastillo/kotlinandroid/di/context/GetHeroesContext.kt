package com.github.jorgecastillo.kotlinandroid.di.context

import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView
import com.karumi.marvelapiclient.CharacterApiClient

data class GetHeroesContext(val view: SuperHeroesView) {

  val apiClient
    get() = CharacterApiClient(com.karumi.marvelapiclient.MarvelApiConfig.Builder(
        com.github.jorgecastillo.kotlinandroid.BuildConfig.MARVEL_PUBLIC_KEY,
        com.github.jorgecastillo.kotlinandroid.BuildConfig.MARVEL_PRIVATE_KEY).debug().build())
}
