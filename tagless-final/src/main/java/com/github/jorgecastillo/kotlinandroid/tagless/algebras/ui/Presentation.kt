package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui

import android.content.Context
import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.bindingCatch
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.model.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage
import javax.inject.Inject

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
 * Presentation operations defined as completely abstract over an F type. Part of the algebras. We
 * will fix F to a concrete type later on from a single point in the system. That helps us to
 * compose a complete program based on abstractions and provide implementation details later on.
 */
class Presentation<F> @Inject constructor(
    private val navigation: Navigation<F>,
    private val heroesService: HeroesUseCases<F>,
    private val monadSuspend: MonadDefer<F>) {

  fun onHeroListItemClick(ctx: Context, heroId: String): Kind<F, Unit> =
      navigation.goToHeroDetailsPage(ctx, heroId)

  private fun displayErrors(view: SuperHeroesView, t: Throwable): Kind<F, Unit> =
      monadSuspend {
        when (CharacterError.fromThrowable(t)) {
          is CharacterError.NotFoundError -> view.showNotFoundError()
          is CharacterError.UnknownServerError -> view.showGenericError()
          is CharacterError.AuthenticationError -> view.showAuthenticationError()
        }
      }

  fun drawSuperHeroes(view: SuperHeroesListView): Kind<F, Unit> =
      monadSuspend.bindingCatch {
        val result = heroesService.getHeroes().handleError {
          displayErrors(view, it); emptyList()
        }.bind()

        monadSuspend.just(view.drawHeroes(result.map {
          SuperHeroViewModel(
              it.id,
              it.name,
              it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
              it.description)
        })).bind()
      }

  fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): Kind<F, Unit> =
      monadSuspend.bindingCatch {
        val result = heroesService.getHeroDetails(heroId).handleError {
          displayErrors(view, it); CharacterDto()
        }.bind()

        monadSuspend.just(view.drawHero(SuperHeroViewModel(
            result.id,
            result.name,
            result.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
            result.description))).bind()
      }

}
