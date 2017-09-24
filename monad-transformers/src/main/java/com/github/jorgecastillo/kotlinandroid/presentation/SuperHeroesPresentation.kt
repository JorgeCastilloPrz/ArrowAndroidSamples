package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroDetailsUseCase
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.Id
import kategory.Reader
import kategory.monad

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

fun onHeroListItemClick(heroId: String) = Reader.ask<GetHeroesContext>().flatMap({
  it.heroDetailsPage.go(heroId)
}, Id.monad())

fun displayErrors(c: CharacterError): AsyncResult<Unit> =
    AsyncResult.bind {
      val ctx = AsyncResult.ask().bind()
      when (c) {
        is NotFoundError -> ctx.view.showNotFoundError()
        is UnknownServerError -> ctx.view.showGenericError()
        is AuthenticationError -> ctx.view.showAuthenticationError()
      }
      AsyncResult.unit
    }

fun drawHeroes(heroes: List<SuperHeroViewModel>): AsyncResult<Unit> =
    AsyncResult.bind {
      val ctx = AsyncResult.ask().bind()
      ctx.view.drawHeroes(heroes)
      AsyncResult.unit
    }

fun charactersToHeroes(characters: List<CharacterDto>): List<SuperHeroViewModel> =
    characters.map {
      SuperHeroViewModel(
          it.name,
          it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY))
    }

fun getSuperHeroes(): AsyncResult<Unit> =
    getHeroesUseCase()
        .map(::charactersToHeroes)
        .flatMap(::drawHeroes)
        .handleErrorWith(::displayErrors)

fun getSuperHeroDetails(heroId: String): AsyncResult<Unit> =
    getHeroDetailsUseCase(heroId)
        .map(::charactersToHeroes)
        .flatMap(::drawHero)
        .handleErrorWith(::displayErrors)
