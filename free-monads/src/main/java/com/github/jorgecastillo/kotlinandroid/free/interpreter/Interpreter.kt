package com.github.jorgecastillo.kotlinandroid.free.interpreter

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.FunctionK
import arrow.core.Try
import arrow.core.right
import arrow.effects.typeclasses.Async
import arrow.free.foldMap
import arrow.typeclasses.MonadError
import arrow.typeclasses.bindingCatch
import com.github.jorgecastillo.kotlinandroid.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.*
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.free.algebra.fix
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import com.karumi.marvelapiclient.model.MarvelImage
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

@Suppress("UNCHECKED_CAST")
fun <F> interpreter(ctx: SuperHeroesContext, ME: MonadError<F, Throwable>,
                    AC: Async<F>): FunctionK<HeroesAlgebra.F, F> =
    object : FunctionK<HeroesAlgebra.F, F> {
      override fun <A> invoke(fa: Kind<HeroesAlgebra.F, A>): Kind<F, A> {
        val op = fa.fix()
        return when (op) {
          is HeroesAlgebra.GetAll -> getAllHeroesImpl(ctx, ME, AC)
          is HeroesAlgebra.GetSingle -> getHeroDetailsImpl(ctx, ME, AC, op.heroId)
          is HeroesAlgebra.HandlePresentationEffects -> {
            ME.run { catch { handlePresentationEffectsImpl(ctx, op.result) } }
          }
          is HeroesAlgebra.Attempt<*> -> {
            val result = op.fa.foldMap(interpreter(ctx, ME, AC), ME)
            ME.run { result.attempt() }
          }
        } as Kind<F, A>
      }
    }

private fun <F, A, B> runInAsyncContext(
    f: () -> A,
    onError: (Throwable) -> B,
    onSuccess: (A) -> B,
    AC: Async<F>): Kind<F, B> {
  return AC.async { proc ->
    async(CommonPool) {
      val result = Try { f() }.fold(onError, onSuccess)
      proc(result.right())
    }
  }
}

fun <F> getAllHeroesImpl(
    ctx: SuperHeroesContext,
    ME: MonadError<F, Throwable>,
    AC: Async<F>): Kind<F, List<CharacterDto>> {
  return ME.bindingCatch {
    val query = Builder.create().withOffset(0).withLimit(50).build()
    val result = runInAsyncContext(
        f = { ctx.apiClient.getAll(query).response.characters.toList() },
        onError = { ME.raiseError<List<CharacterDto>>(it) },
        onSuccess = { ME.just(it) },
        AC = AC
    ).bind()
    result.bind()
  }
}

fun <F> getHeroDetailsImpl(
    ctx: SuperHeroesContext,
    ME: MonadError<F, Throwable>,
    AC: Async<F>,
    heroId: String): Kind<F, CharacterDto> =
    ME.bindingCatch {
      val result = runInAsyncContext(
          f = { ctx.apiClient.getCharacter(heroId).response },
          onError = { ME.raiseError<CharacterDto>(it) },
          onSuccess = { ME.just(it) },
          AC = AC
      ).bind()
      result.bind()
    }

fun handlePresentationEffectsImpl(
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
    NotFoundError -> ctx.view.showNotFoundError()
    is UnknownServerError -> ctx.view.showGenericError()
    AuthenticationError -> ctx.view.showAuthenticationError()
  }
}


