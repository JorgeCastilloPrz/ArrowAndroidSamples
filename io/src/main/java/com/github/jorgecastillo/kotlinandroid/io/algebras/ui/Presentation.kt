package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.monadError
import arrow.typeclasses.bindingCatch
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage.Size.PORTRAIT_UNCANNY

interface SuperHeroesView {

  fun showNotFoundError(): Unit

  fun showGenericError(): Unit

  fun showAuthenticationError(): Unit
}

interface SuperHeroesListView : SuperHeroesView {

  fun drawHeroes(heroes: List<SuperHeroViewModel>): Unit

}

interface SuperHeroDetailView : SuperHeroesView {

  fun drawHero(hero: SuperHeroViewModel)

}

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. This is equivalent, but already fixing the type F to IO, for simplicity. Sometimes you're
 * okay fixing the type to some concrete type you know will fulfill your needs for all the cases.
 * But remember: you're losing polymorphism on your program when doing this.
 */
object Presentation {

  fun onHeroListItemClick(ctx: Context, heroId: String): IO<Unit> =
      Navigation.goToHeroDetailsPage(ctx, heroId)

  private fun displayErrors(view: SuperHeroesView, t: Throwable): IO<Unit> =
      IO.monadError().just(when (CharacterError.fromThrowable(t)) {
        is CharacterError.NotFoundError -> view.showNotFoundError()
        is CharacterError.UnknownServerError -> view.showGenericError()
        is CharacterError.AuthenticationError -> view.showAuthenticationError()
      }).fix()

  fun drawSuperHeroes(view: SuperHeroesListView): IO<Unit> {
    val monadError = IO.monadError()
    return monadError.bindingCatch {
      val result = HeroesUseCases.getHeroes().handleError {
        displayErrors(view, it); emptyList()
      }.bind()

      monadError.just(view.drawHeroes(result.map {
        SuperHeroViewModel(
            it.id,
            it.name,
            it.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
            it.description)
      })).bind()
    }.fix()
  }

  fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): IO<Unit> {
    val monadError = IO.monadError()
    return monadError.bindingCatch {
      val result = HeroesUseCases.getHeroDetails(heroId).handleError {
        displayErrors(view, it); CharacterDto()
      }.bind()

      monadError.just(view.drawHero(SuperHeroViewModel(
          result.id,
          result.name,
          result.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
          result.description))).bind()
    }.fix()
  }
}
