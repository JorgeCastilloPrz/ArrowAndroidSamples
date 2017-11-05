package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroDetailsUseCase
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.ev
import com.github.jorgecastillo.kotlinandroid.functional.monad
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.Reader
import kategory.binding
import kategory.flatMap

interface HeroesView {
  fun showNotFoundError()
  fun showGenericError()
  fun showAuthenticationError()
}

interface SuperHeroesListView : HeroesView {
  fun drawHeroes(heroes: List<SuperHeroViewModel>): Unit
}

interface SuperHeroDetailView : HeroesView {
  fun drawHero(hero: SuperHeroViewModel): Unit
}

fun onHeroListItemClick(heroId: String) = Reader.ask<GetHeroesContext>().flatMap({
  it.heroDetailsPage.go(heroId)
})

fun displayGetHeroesErrors(c: CharacterError): AsyncResult<GetHeroesContext, Unit> =
    AsyncResult.monad<GetHeroesContext>().binding {
      val ctx = AsyncResult.ask<GetHeroesContext>().bind()
      when (c) {
        is NotFoundError -> ctx.view.showNotFoundError()
        is UnknownServerError -> ctx.view.showGenericError()
        is AuthenticationError -> ctx.view.showAuthenticationError()
      }
      AsyncResult.pure<GetHeroesContext, Unit>(Unit)
    }.ev()

fun displayGetDetailsErrors(c: CharacterError): AsyncResult<GetHeroDetailsContext, Unit> =
    AsyncResult.monad<GetHeroDetailsContext>().binding {
      val ctx = AsyncResult.ask<GetHeroDetailsContext>().bind()
      when (c) {
        is NotFoundError -> ctx.view.showNotFoundError()
        is UnknownServerError -> ctx.view.showGenericError()
        is AuthenticationError -> ctx.view.showAuthenticationError()
      }
      AsyncResult.pure<GetHeroDetailsContext, Unit>(Unit)
    }.ev()

fun drawHeroes(heroes: List<SuperHeroViewModel>): AsyncResult<GetHeroesContext, Unit> =
    AsyncResult.monad<GetHeroesContext>().binding {
      val ctx = AsyncResult.ask<GetHeroesContext>().bind()
      ctx.view.drawHeroes(heroes)
      AsyncResult.unit<GetHeroesContext>()
    }.ev()

fun drawHero(hero: SuperHeroViewModel): AsyncResult<GetHeroDetailsContext, Unit> =
    AsyncResult.monad<GetHeroDetailsContext>().binding {
      val ctx = AsyncResult.ask<GetHeroDetailsContext>().bind()
      ctx.view.drawHero(hero)
      AsyncResult.unit<GetHeroDetailsContext>()
    }.ev()

fun charactersToHeroes(characters: List<CharacterDto>): List<SuperHeroViewModel> =
    characters.map(::characterToHero)

fun characterToHero(character: CharacterDto): SuperHeroViewModel =
    SuperHeroViewModel(
        character.id,
        character.name,
        character.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
        character.description)

fun charactersToHero(characters: List<CharacterDto>): SuperHeroViewModel =
    characters.map(::characterToHero).first()

fun getSuperHeroes(): AsyncResult<GetHeroesContext, Unit> =
    getHeroesUseCase<GetHeroesContext>()
        .map { charactersToHeroes(it) }
        .flatMap { drawHeroes(it) }
        .handleErrorWith { displayGetHeroesErrors(it) }

fun getSuperHeroDetails(
    heroId: String): AsyncResult<GetHeroDetailsContext, Unit> =
    getHeroDetailsUseCase<GetHeroDetailsContext>(heroId)
        .map { charactersToHero(it) }
        .flatMap { drawHero(it) }
        .handleErrorWith { displayGetDetailsErrors(it) }
