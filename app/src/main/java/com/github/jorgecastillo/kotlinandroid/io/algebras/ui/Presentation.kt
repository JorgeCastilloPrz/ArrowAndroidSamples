package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getHeroDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getHeroes
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.HeroViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage.Size.PORTRAIT_UNCANNY

interface SuperHeroesView {

  fun showLoading(): Unit

  fun hideLoading(): Unit

  fun showNotFoundError(): Unit

  fun showGenericError(): Unit

  fun showAuthenticationError(): Unit
}

interface SuperHeroesListView : SuperHeroesView {

  fun drawHeroes(heroes: List<HeroViewState>): Unit
}

interface SuperHeroDetailView : SuperHeroesView {

  fun drawHero(hero: HeroViewState)
}

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. We'll end up running these methods using a valid F type that support Concurrent behaviors,
 * like IO.
 */
fun <F> Runtime<F>.onHeroListItemClick(
  ctx: Context,
  heroId: String
): Kind<F, Unit> =
  goToHeroDetailsPage(ctx, heroId)

private fun displayErrors(
  view: SuperHeroesView,
  t: Throwable
): Unit {
  when (CharacterError.fromThrowable(t)) {
    is CharacterError.NotFoundError -> view.showNotFoundError()
    is CharacterError.UnknownServerError -> view.showGenericError()
    is CharacterError.AuthenticationError -> view.showAuthenticationError()
  }
}

fun <F> Runtime<F>.getAllHeroes(view: SuperHeroesListView): Kind<F, Unit> = fx.concurrent {
  !effect { view.showLoading() }
  val maybeHeroes = !getHeroes().attempt()
  !effect { view.hideLoading() }
  !effect {
    maybeHeroes.fold(
      ifLeft = { displayErrors(view, it) },
      ifRight = { view.drawHeroes(it.map { heroDto -> heroDto.toViewState() }) }
    )
  }
}

fun <F> Runtime<F>.getSuperHeroDetails(
  heroId: String,
  view: SuperHeroDetailView
): Kind<F, Unit> = fx.concurrent {
  !effect { view.showLoading() }
  val maybeHero = !getHeroDetails(heroId).attempt()
  !effect { view.hideLoading() }
  !effect {
    maybeHero.fold(
      ifLeft = { displayErrors(view, it) },
      ifRight = { heroDto -> view.drawHero(heroDto.toViewState()) }
    )
  }
}

fun CharacterDto.toViewState() = HeroViewState(
  id,
  name,
  thumbnail.getImageUrl(PORTRAIT_UNCANNY),
  description
)
