package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.functional.Control
import com.github.jorgecastillo.kotlinandroid.functional.monadControl
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.HK
import kategory.binding

interface SuperHeroesView {

  fun drawHeroes(heroes: List<SuperHeroViewModel>)

  fun showHeroesNotFoundError()

  fun showGenericError()

  fun showAuthenticationError()
}

fun displayErrors(ctx: GetHeroesContext, c: CharacterError) : Unit {
  when (c) {
    is NotFoundError -> ctx.view.showHeroesNotFoundError()
    is UnknownServerError -> ctx.view.showGenericError()
    is AuthenticationError -> ctx.view.showAuthenticationError()
  }
}

inline fun <reified F> getSuperHeroes(C: Control<F> = monadControl()): HK<F, Unit> =
    C.binding {
      val ctx = !C.ask()
      val result = !C.handleError(getHeroesUseCase(), { displayErrors(ctx, it); emptyList()})
      ctx.view.drawHeroes(result.map {
        SuperHeroViewModel(
            it.name,
            it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY))
      })
      C.pure(Unit)
    }
