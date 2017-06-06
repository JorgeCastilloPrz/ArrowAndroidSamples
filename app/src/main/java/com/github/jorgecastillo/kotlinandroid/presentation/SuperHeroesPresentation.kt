package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.architecturecomponentssample.model.error.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import katz.Either.Left
import katz.Either.Right
import katz.Id
import katz.Reader

interface SuperHeroesView {

  fun drawHeroes(heroes: List<SuperHeroViewModel>)

  fun showHeroesNotFoundError()

  fun showGenericError()

  fun showAuthenticationError()
}

fun getSuperHeroes() = Reader.ask<GetHeroesContext>(Id).flatMap { ctx ->
  ctx.getSuperHeroesInteractor.get().map { res ->
    when (res) {
      is Left -> when (res.a) {
        is NotFoundError -> ctx.view.showHeroesNotFoundError()
        is UnknownServerError -> ctx.view.showGenericError()
        is AuthenticationError -> ctx.view.showAuthenticationError()
      }
      is Right -> ctx.view.drawHeroes(res.b.map { SuperHeroViewModel(it.name) })
    }
  }
}
