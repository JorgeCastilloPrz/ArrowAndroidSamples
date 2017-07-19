package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage

interface SuperHeroesView {

    fun drawHeroes(heroes: List<SuperHeroViewModel>): Unit

    fun showHeroesNotFoundError(): Unit

    fun showGenericError(): Unit

    fun showAuthenticationError(): Unit
}

fun displayErrors(c: CharacterError): AsyncResult<Unit> =
    AsyncResult.bind {
        val ctx = AsyncResult.ask().bind()
        when (c) {
            is NotFoundError -> ctx.view.showHeroesNotFoundError()
            is UnknownServerError -> ctx.view.showGenericError()
            is AuthenticationError -> ctx.view.showAuthenticationError()
        }
        AsyncResult.unit
    }

fun drawHeroes(heroes : List<SuperHeroViewModel>): AsyncResult<Unit> =
        AsyncResult.bind {
            val ctx = AsyncResult.ask().bind()
            ctx.view.drawHeroes(heroes)
            AsyncResult.unit
        }

fun charactersToHeroes(characters: List<CharacterDto>) : List<SuperHeroViewModel> =
        characters.map {
            SuperHeroViewModel(
                    it.name,
                    it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY))
        }

fun getSuperHeroes(): AsyncResult<Unit> =
        getHeroesUseCase()
                .map(::charactersToHeroes)
                .flatMap(::drawHeroes)
                .handleErrorWith(::displayErrors)
