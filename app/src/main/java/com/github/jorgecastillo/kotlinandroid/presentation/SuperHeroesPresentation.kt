package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.architecturecomponentssample.model.error.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroes
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.Reader

interface SuperHeroesView {

  fun drawHeroes(heroes: List<SuperHeroViewModel>)

  fun showHeroesNotFoundError()

  fun showGenericError()

  fun showAuthenticationError()
}

fun getSuperHeroes() = Reader.ask<GetHeroesContext>().flatMap { (view) ->
  getHeroes().map { future ->
    future.onComplete { res ->
      res.fold({
        error ->
        when (error) {
          is NotFoundError -> view.showHeroesNotFoundError()
          is UnknownServerError -> view.showGenericError()
          is AuthenticationError -> view.showAuthenticationError()
        }
      }, {
        success ->
        view.drawHeroes(success.map {
          SuperHeroViewModel(
              it.name,
              it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY))
        })
      })
    }
  }
}
