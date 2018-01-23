package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.effects.IO
import arrow.effects.ev
import arrow.effects.monadError
import arrow.typeclasses.binding
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

object Presentation {

  fun onHeroListItemClick(ctx: Context, heroId: String): IO<Unit> =
      Navigation.goToHeroDetailsPage(ctx, heroId)

  private fun displayErrors(view: SuperHeroesView, t: Throwable): IO<Unit> =
      IO.monadError().pure(when (CharacterError.fromThrowable(t)) {
        is CharacterError.NotFoundError -> view.showNotFoundError()
        is CharacterError.UnknownServerError -> view.showGenericError()
        is CharacterError.AuthenticationError -> view.showAuthenticationError()
      }).ev()

  fun drawSuperHeroes(view: SuperHeroesListView): IO<Unit> {
    val monadError = IO.monadError()
    return monadError.binding {
      val result = monadError.handleError(HeroesUseCases.getHeroes(), { displayErrors(view, it); emptyList() }).bind()
      monadError.pure(view.drawHeroes(result.map {
        SuperHeroViewModel(
            it.id,
            it.name,
            it.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
            it.description)
      }))
    }.ev()
  }

  fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): IO<Unit> {
    val monadError = IO.monadError()
    return monadError.binding {
      val result = monadError.handleError(HeroesUseCases.getHeroDetails(heroId),
          { displayErrors(view, it); CharacterDto() }).bind()
      monadError.pure(view.drawHero(SuperHeroViewModel(
          result.id,
          result.name,
          result.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
          result.description)))
    }.ev()
  }
}
