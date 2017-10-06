package com.github.jorgecastillo.kotlinandroid.free.interpreter

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.free.algebra.*
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.*
import kategory.Either.Left
import kategory.Either.Right
import java.net.HttpURLConnection

@Suppress("UNCHECKED_CAST")
fun <F> interpreter(ctx: SuperHeroesContext, ME: MonadError<F, CharacterError>): FunctionK<HeroesAlgebraHK, F> =
        object : FunctionK<HeroesAlgebraHK, F> {
            override fun <A> invoke(fa: HeroesAlgebraKind<A>): HK<F, A> {
                val op = fa.ev()
                return when (op) {
                    is HeroesAlgebra.GetAll -> getAllHeroesImpl(ctx, ME)
                    is HeroesAlgebra.GetSingle -> getHeroDetailsImpl(ctx, ME, op.heroId)
                    is HeroesAlgebra.HandlePresentationEffects -> ME.catch(
                            { handlePresentationEffects(ctx, op.result) },
                            { exceptionAsCharacterError(it) }
                    )
                    is HeroesAlgebra.Attempt<*> -> {
                        val result = op.fa.foldMap(interpreter(ctx, ME), ME)
                        ME.attempt(result)
                    }
                } as HK<F, A>
            }
        }

fun <F> getAllHeroesImpl(ctx: SuperHeroesContext, ME: MonadError<F, CharacterError>): HK<F, List<CharacterDto>> {
    return ME.binding {
        val query = Builder.create().withOffset(0).withLimit(50).build()
        ME.catch(
                { ctx.apiClient.getAll(query).response.characters.toList() },
                { exceptionAsCharacterError(it) }
        )
    }
}

fun <F> getHeroDetailsImpl(
        ctx: SuperHeroesContext,
        ME: MonadError<F, CharacterError>,
        heroId: String): HK<F, CharacterDto> =
        ME.binding {
            ME.catch(
                    { ctx.apiClient.getCharacter(heroId).response },
                    { exceptionAsCharacterError(it) }
            )
        }

fun exceptionAsCharacterError(e: Throwable): CharacterError =
        when (e) {
            is MarvelAuthApiException -> AuthenticationError
            is MarvelApiException ->
                if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
                else UnknownServerError(Option.Some(e))
            else -> UnknownServerError((Option.Some(e)))
        }

fun handlePresentationEffects(
        ctx: SuperHeroesContext,
        result: Either<CharacterError, List<CharacterDto>>): Unit =
        when (result) {
            is Left -> {
                displayErrors(ctx, result.a); }
            is Right -> when (ctx) {
                is GetHeroesContext -> ctx.view.drawHeroes(result.b.map {
                    SuperHeroViewModel(
                            it.id,
                            it.name,
                            it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                            it.description)
                })
                is GetHeroDetailsContext -> ctx.view.drawHero(result.b.map {
                    SuperHeroViewModel(
                            it.id,
                            it.name,
                            it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                            it.description)
                }[0])
            }
        }

fun displayErrors(ctx: SuperHeroesContext, c: CharacterError): Unit {
    when (c) {
        is NotFoundError -> ctx.view.showNotFoundError()
        is UnknownServerError -> ctx.view.showGenericError()
        is AuthenticationError -> ctx.view.showAuthenticationError()
    }
}


