package com.github.jorgecastillo.kotlinandroid.di.context

import android.content.Context
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.asyncContext
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroDetailView
import com.github.jorgecastillo.kotlinandroid.presentation.SuperHeroesListView
import com.github.jorgecastillo.kotlinandroid.presentation.navigation.HeroDetailsPage
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig.Builder

sealed class SuperHeroesContext {

  abstract val ctx: Context

  val heroDetailsPage = HeroDetailsPage()
  val apiClient
    get() = CharacterApiClient(Builder(
        BuildConfig.MARVEL_PUBLIC_KEY,
        BuildConfig.MARVEL_PRIVATE_KEY).debug().build())
  fun <D: SuperHeroesContext> threading() = AsyncResult.asyncContext<D>()

  data class ApplicationContext(override val ctx: Context) : SuperHeroesContext()
  data class GetHeroesContext(override val ctx: Context, val view: SuperHeroesListView) : SuperHeroesContext()
  data class GetHeroDetailsContext(override val ctx: Context, val view: SuperHeroDetailView) : SuperHeroesContext()
}
