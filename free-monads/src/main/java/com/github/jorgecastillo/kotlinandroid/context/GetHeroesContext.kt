package com.github.jorgecastillo.kotlinandroid.di.context

import android.content.Context
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroDetailView
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesListView
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesView
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig.Builder

sealed class SuperHeroesContext {

  abstract val ctx: Context
  abstract val view: SuperHeroesView

  val apiClient
    get() = CharacterApiClient(Builder(
        BuildConfig.MARVEL_PUBLIC_KEY,
        BuildConfig.MARVEL_PRIVATE_KEY).debug().build())

  data class GetHeroesContext(override val ctx: Context, override val view: SuperHeroesListView) : SuperHeroesContext()
  data class GetHeroDetailsContext(override val ctx: Context,
      override val view: SuperHeroDetailView) : SuperHeroesContext()
}


