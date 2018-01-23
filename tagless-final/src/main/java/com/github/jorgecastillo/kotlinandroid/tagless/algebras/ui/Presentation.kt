package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui

import android.content.Context
import arrow.HK
import arrow.TC
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.model.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage

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

@typeclass
interface Presentation<F> : TC {

    fun navigation(): Navigation<F>

    fun heroesService(): HeroesUseCases<F>

    fun ME(): MonadError<F, Throwable>

    fun onHeroListItemClick(ctx: Context, heroId: String): HK<F, Unit> =
            navigation().goToHeroDetailsPage(ctx, heroId)

    fun displayErrors(view: SuperHeroesView, t: Throwable): HK<F, Unit> =
            ME().pure(when (CharacterError.fromThrowable(t)) {
                is CharacterError.NotFoundError -> view.showNotFoundError()
                is CharacterError.UnknownServerError -> view.showGenericError()
                is CharacterError.AuthenticationError -> view.showAuthenticationError()
            })

    fun drawSuperHeroes(view: SuperHeroesListView): HK<F, Unit> =
            ME().binding {
                val result = ME().handleError(heroesService().getHeroes(), { displayErrors(view, it); emptyList() }).bind()
                ME().pure(view.drawHeroes(result.map {
                    SuperHeroViewModel(
                            it.id,
                            it.name,
                            it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                            it.description)
                }))
            }


    fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): HK<F, Unit> =
            ME().binding {
                val result = ME().handleError(heroesService().getHeroDetails(heroId), { displayErrors(view, it); CharacterDto() }).bind()
                ME().pure(view.drawHero(SuperHeroViewModel(
                        result.id,
                        result.name,
                        result.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                        result.description)))
            }

}
