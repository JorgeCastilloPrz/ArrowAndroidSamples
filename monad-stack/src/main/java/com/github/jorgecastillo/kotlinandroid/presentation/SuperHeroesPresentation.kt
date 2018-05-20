package com.github.jorgecastillo.kotlinandroid.presentation

import arrow.core.Some
import arrow.core.identity
import arrow.data.ReaderApi
import arrow.data.flatMap
import arrow.data.map
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroDetailsUseCase
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage.Size.PORTRAIT_UNCANNY
import java.net.HttpURLConnection

interface HeroesView {
  fun showNotFoundError()
  fun showGenericError()
  fun showAuthenticationError()
}

interface SuperHeroesListView : HeroesView {
  fun drawHeroes(heroes: List<SuperHeroViewModel>)
}

interface SuperHeroDetailView : HeroesView {
  fun drawHero(hero: SuperHeroViewModel)
}

fun onHeroListItemClick(heroId: String) = ReaderApi.ask<GetHeroesContext>().flatMap({
  it.heroDetailsPage.go(heroId)
})

fun getSuperHeroes() = ReaderApi.ask<GetHeroesContext>()
    .flatMap({ (_, view: SuperHeroesListView) ->
      getHeroesUseCase().map({ io ->
        io.unsafeRunAsync { maybeHeroes ->
          maybeHeroes.bimap(::exceptionAsCharacterError, ::identity).fold(
              { error -> drawError(error, view) },
              { success -> drawHeroes(view, success) })
        }
      })
    })

fun getSuperHeroDetails(heroId: String) = ReaderApi.ask<GetHeroDetailsContext>()
    .flatMap({ (_, view: SuperHeroDetailView) ->
      getHeroDetailsUseCase(heroId).map({ io ->
        io.unsafeRunAsync { maybeHeroes ->
          maybeHeroes.bimap(::exceptionAsCharacterError, ::identity).fold(
              { error -> drawError(error, view) },
              { hero -> drawHero(hero, view) })
        }
      })
    })

fun exceptionAsCharacterError(e: Throwable): CharacterError =
    when (e) {
      is MarvelAuthApiException -> CharacterError.AuthenticationError
      is MarvelApiException ->
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) CharacterError.NotFoundError
        else CharacterError.UnknownServerError(Some(e))
      else -> CharacterError.UnknownServerError(Some(e))
    }

private fun drawError(error: CharacterError,
                      view: HeroesView) {
  when (error) {
    is NotFoundError -> view.showNotFoundError()
    is UnknownServerError -> view.showGenericError()
    is AuthenticationError -> view.showAuthenticationError()
  }
}

private fun drawHeroes(view: SuperHeroesListView, success: List<CharacterDto>) {
  view.drawHeroes(success.map {
    SuperHeroViewModel(
        it.id,
        it.name,
        it.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
        it.description)
  })
}

private fun drawHero(success: List<CharacterDto>, view: SuperHeroDetailView) {
  view.drawHero(success.map {
    SuperHeroViewModel(
        it.id,
        it.name,
        it.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
        it.description)
  }.first())
}


