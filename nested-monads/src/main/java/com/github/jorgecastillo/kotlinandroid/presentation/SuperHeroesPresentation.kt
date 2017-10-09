package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroDetailsUseCase
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.Reader
import kategory.flatMap
import kategory.map

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
})

fun getSuperHeroes() = Reader.ask<GetHeroesContext>().flatMap({ (_, view: SuperHeroesListView) ->
  getHeroesUseCase().map({ res ->
    res.map {
      it.fold({ error ->
        when (error) {
          is NotFoundError -> view.showNotFoundError()
          is UnknownServerError -> view.showGenericError()
          is AuthenticationError -> view.showAuthenticationError()
        }
      }, { success ->
        view.drawHeroes(success.map {
          SuperHeroViewModel(
              it.id,
              it.name,
              it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
              it.description)
        })
      })
    }
  })
})

fun getSuperHeroDetails(heroId: String) = Reader.ask<GetHeroDetailsContext>()
    .flatMap({ (_, view: SuperHeroDetailView) ->
      getHeroDetailsUseCase(heroId).map({ res ->
        res.map {
          it.fold({ error ->
            when (error) {
              is NotFoundError -> view.showNotFoundError()
              is UnknownServerError -> view.showGenericError()
              is AuthenticationError -> view.showAuthenticationError()
            }
          }, { success ->
            view.drawHero(success.map {
              SuperHeroViewModel(
                  it.id,
                  it.name,
                  it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                  it.description)
            }.first())
          })
        }
      })
    })
