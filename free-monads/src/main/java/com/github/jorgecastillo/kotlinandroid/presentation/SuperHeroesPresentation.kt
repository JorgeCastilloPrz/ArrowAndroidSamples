package com.github.jorgecastillo.kotlinandroid.presentation

import arrow.core.Either
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.free.ev
import arrow.typeclasses.binding
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.free.algebra.*
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import java.net.HttpURLConnection

interface SuperHeroesView {
  fun showNotFoundError()
  fun showGenericError()
  fun showAuthenticationError()
}

interface SuperHeroesListView : SuperHeroesView {
  fun drawHeroes(heroes: List<SuperHeroViewModel>)
}

interface SuperHeroDetailView : SuperHeroesView {
  fun drawHero(hero: SuperHeroViewModel)
}

fun showSuperHeroes(): FreeHeroesAlgebra<Unit> =
    HeroesAlgebra.binding {
        val res: Either<Throwable, List<CharacterDto>> = attempt(getAllHeroes()).bind()
        val efRes = handlePresentationEffects(res.bimap(::exceptionAsCharacterError, ::identity)).bind()
        yields(efRes)
    }.ev()

fun showSuperHeroDetail(heroId: String): FreeHeroesAlgebra<Unit> =
        HeroesAlgebra.binding {
            val res: Either<Throwable, CharacterDto> = attempt(getSingleHero(heroId)).bind()
            val efRes = handlePresentationEffects(res.bimap(::exceptionAsCharacterError, ::listOf)).bind()
            yields(efRes)
        }.ev()


fun exceptionAsCharacterError(e: Throwable): CharacterError =
        when (e) {
            is MarvelAuthApiException -> CharacterError.AuthenticationError
            is MarvelApiException ->
                if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) CharacterError.NotFoundError
                else CharacterError.UnknownServerError(Some(e))
            else -> CharacterError.UnknownServerError((Some(e)))
        }
