package com.github.jorgecastillo.kotlinandroid.free.interpreter

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.AuthenticationError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.NotFoundError
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError.UnknownServerError
import com.github.jorgecastillo.kotlinandroid.domain.usecase.getHeroesUseCase
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesDataSourceAlgebraHK
import com.github.jorgecastillo.kotlinandroid.free.algebra.ev
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResultMonadReaderInstance
import com.github.jorgecastillo.kotlinandroid.functional.ev
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel
import com.karumi.marvelapiclient.MarvelApiException
import com.karumi.marvelapiclient.MarvelAuthApiException
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery.Builder
import com.karumi.marvelapiclient.model.MarvelImage.Size.PORTRAIT_UNCANNY
import kategory.Either
import kategory.FunctionK
import kategory.HK
import kategory.Option
import kategory.binding
import kategory.flatMap
import java.net.HttpURLConnection

/*fun test(): Unit {
  val heroesDS = object : HeroesDataSource {
  }

  val MR = AsyncResult.monadReader<GetHeroesContext>()
  heroesDS.getAll().foldMap(asyncResultDataSourceInterpreter(MR), MR)
}*/

inline fun <reified F, D : SuperHeroesContext> asyncResultDataSourceInterpreter(
    ARM: AsyncResultMonadReaderInstance<D>): FunctionK<HeroesDataSourceAlgebraHK, F> =
    object : FunctionK<HeroesDataSourceAlgebraHK, F> {
      override fun <A> invoke(fa: HK<HeroesDataSourceAlgebraHK, A>): HK<F, A> {
        val op = fa.ev()
        return when (op) {
          is HeroesAlgebra.GetAll -> getAllHeroesAsyncResult(ARM) as HK<F, A>
          is HeroesAlgebra.GetSingle -> getHeroDetails(ARM, op.heroId) as HK<F, A>
          is HeroesAlgebra.HandlePresentationEffects -> handlePresentationEffects(ARM, op.result) as HK<F, A>
        }
      }
    }

fun <D : SuperHeroesContext> getAllHeroesAsyncResult(
    AR: AsyncResultMonadReaderInstance<D>): AsyncResult<D, List<CharacterDto>> {
  return AR.binding {
    val query = Builder.create().withOffset(0).withLimit(50).build()
    val ctx = AR.ask().bind()
    AR.catch(
        { ctx.apiClient.getAll(query).response.characters.toList() },
        { exceptionAsCharacterError(it) }
    )
  }.ev()
}

fun <D : SuperHeroesContext> getHeroDetails(AR: AsyncResultMonadReaderInstance<D>,
    heroId: String): AsyncResult<D, List<CharacterDto>> =
    AR.binding {
      val ctx = AR.ask().bind()
      AR.catch(
          { listOf(ctx.apiClient.getCharacter(heroId).response) },
          { exceptionAsCharacterError(it) }
      ).ev()
    }.ev()

fun exceptionAsCharacterError(e: Throwable): CharacterError =
    when (e) {
      is MarvelAuthApiException -> AuthenticationError
      is MarvelApiException ->
        if (e.httpCode == HttpURLConnection.HTTP_NOT_FOUND) NotFoundError
        else UnknownServerError(Option.Some(e))
      else -> UnknownServerError((Option.Some(e)))
    }

fun <D : SuperHeroesContext> handlePresentationEffects(AR: AsyncResultMonadReaderInstance<D>,
    result: Either<CharacterError, List<CharacterDto>>): AsyncResult<D, Unit> =
    getHeroesUseCase().flatMap { it. }
    AR.binding {
      val ctx = AR.ask().bind()
      val result = AR.handleError(getHeroesUseCase(), { displayErrors(ctx, it); emptyList() }).bind()
      ctx.view.drawHeroes(result.map {
        SuperHeroViewModel(
            it.id,
            it.name,
            it.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
            it.description)
      })
      AR.pure(Unit)
    }.ev()

fun displayErrors(ctx: SuperHeroesContext, c: CharacterError): Unit {
  when (c) {
    is NotFoundError -> ctx.view.showNotFoundError()
    is UnknownServerError -> ctx.view.showGenericError()
    is AuthenticationError -> ctx.view.showAuthenticationError()
  }
}
