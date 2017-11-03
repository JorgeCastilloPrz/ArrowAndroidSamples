package com.github.jorgecastillo.kotlinandroid.di.context

import android.content.Context
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroDetailView
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesListView
import com.github.jorgecastillo.kotlinandroid.presentation.navigation.HeroDetailsPage
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import kategory.effects.IO
import kategory.effects.asyncContext

sealed class SuperHeroesContext(ctx: Context) {

  val heroDetailsPage = HeroDetailsPage()
  val apiClient
    get() = CharacterApiClient(MarvelApiConfig.Builder(
        BuildConfig.MARVEL_PUBLIC_KEY,
        BuildConfig.MARVEL_PRIVATE_KEY).debug().build())
  val threading = IO.asyncContext()

  data class GetHeroesContext(val ctx: Context, val view: SuperHeroesListView) : SuperHeroesContext(ctx)
  data class GetHeroDetailsContext(val ctx: Context, val view: SuperHeroDetailView) : SuperHeroesContext(ctx)
}
