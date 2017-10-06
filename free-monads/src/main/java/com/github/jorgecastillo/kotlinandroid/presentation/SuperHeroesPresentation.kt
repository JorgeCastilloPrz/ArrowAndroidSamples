package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.free.algebra.*
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.Either
import kategory.binding
import kategory.ev

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
        val res: Either<CharacterError, List<CharacterDto>> = attempt(getAllHeroes()).bind()
        val efRes = handlePresentationEffects(res).bind()
        yields(efRes)
    }.ev()

fun showSuperHeroDetail(heroId: String): FreeHeroesAlgebra<Unit> =
        HeroesAlgebra.binding {
            val res: Either<CharacterError, CharacterDto> = attempt(getSingleHero(heroId)).bind()
            val efRes = handlePresentationEffects(res.map(::listOf)).bind()
            yields(efRes)
        }.ev()

