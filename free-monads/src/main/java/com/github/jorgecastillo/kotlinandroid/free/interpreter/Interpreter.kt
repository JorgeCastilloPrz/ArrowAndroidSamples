package com.github.jorgecastillo.kotlinandroid.free.interpreter

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebraHK
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebraKind
import com.github.jorgecastillo.kotlinandroid.free.algebra.ev
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import com.karumi.marvelapiclient.model.MarvelImage
import kategory.Either
import kategory.Either.Left
import kategory.Either.Right
import kategory.FunctionK
import kategory.HK
import kategory.MonadError
import kategory.Try
import kategory.bindingE
import kategory.catch
import kategory.effects.AsyncContext
import kategory.foldMap
import kategory.right
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

@Suppress("UNCHECKED_CAST")
fun <F> interpreter(ctx: SuperHeroesContext, ME: MonadError<F, Throwable>,
    AC: AsyncContext<F>): FunctionK<HeroesAlgebraHK, F> =
    object : FunctionK<HeroesAlgebraHK, F> {
      override fun <A> invoke(fa: HeroesAlgebraKind<A>): HK<F, A> {
        val op = fa.ev()
        return when (op) {
          is HeroesAlgebra.GetAll -> getAllHeroesImpl(ctx, ME, AC)
          is HeroesAlgebra.GetSingle -> getHeroDetailsImpl(ctx, ME, AC, op.heroId)
          is HeroesAlgebra.HandlePresentationEffects -> {
            ME.catch({ handlePresentationEffectsImpl(ctx, op.result) })
          }
          is HeroesAlgebra.Attempt<*> -> {
            val result = op.fa.foldMap(interpreter(ctx, ME, AC), ME)
            ME.attempt(result)
          }
        } as HK<F, A>
      }
    }

private fun <F, A, B> runInAsyncContext(
    f: () -> A,
    onError: (Throwable) -> B,
    onSuccess: (A) -> B,
    AC: AsyncContext<F>): HK<F, B> {
  return AC.runAsync { proc ->
    async(CommonPool) {
      val result = Try { f() }.fold(onError, onSuccess)
      proc(result.right())
    }
  }
}

fun <F> getAllHeroesImpl(
    ctx: SuperHeroesContext,
    ME: MonadError<F, Throwable>,
    AC: AsyncContext<F>): HK<F, List<CharacterDto>> {
  return ME.bindingE {
    val query = Builder.create().withOffset(0).withLimit(50).build()
    runInAsyncContext(
        f = { ctx.apiClient.getAll(query).response.characters.toList() },
        onError = { ME.raiseError<List<CharacterDto>>(it) },
        onSuccess = { ME.pure(it) },
        AC = AC
    ).bind()
  }
}

fun <F> getHeroDetailsImpl(
    ctx: SuperHeroesContext,
    ME: MonadError<F, Throwable>,
    AC: AsyncContext<F>,
    heroId: String): HK<F, CharacterDto> =
    ME.bindingE {
      runInAsyncContext(
          f = { ctx.apiClient.getCharacter(heroId).response },
          onError = { ME.raiseError<CharacterDto>(it) },
          onSuccess = { ME.pure(it) },
          AC = AC
      ).bind()
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
    is NotFoundError -> ctx.view.showNotFoundError()
    is UnknownServerError -> ctx.view.showGenericError()
    is AuthenticationError -> ctx.view.showAuthenticationError()
  }
}


