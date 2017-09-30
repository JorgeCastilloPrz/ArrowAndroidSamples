package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
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
import kategory.*

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
}, Id.monad())

fun <D: SuperHeroesContext> displayErrors(c: CharacterError): AsyncResult<D, Unit> =
        AsyncResult.monad<D>().binding {
            val ctx = AsyncResult.ask<D>().bind()
            when (c) {
                is NotFoundError -> ctx.view.showNotFoundError()
                is UnknownServerError -> ctx.view.showGenericError()
                is AuthenticationError -> ctx.view.showAuthenticationError()
            }
            AsyncResult.unit<D>()
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
        characters.map(::characterToHeroe)

fun characterToHeroe(character: CharacterDto): SuperHeroViewModel =
        SuperHeroViewModel(
                character.id,
                character.name,
                character.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                character.description)

fun getSuperHeroes(): AsyncResult<GetHeroesContext, Unit> =
        getHeroesUseCase<GetHeroesContext>()
                .map(::charactersToHeroes)
                .flatMap(::drawHeroes)
                .handleErrorWith(::displayErrors)

fun getSuperHeroDetails(heroId: String): AsyncResult<GetHeroDetailsContext, Unit> =
        getHeroDetailsUseCase<GetHeroDetailsContext>(heroId)
                .map(::characterToHeroe)
                .flatMap(::drawHero)
                .handleErrorWith(::displayErrors)
