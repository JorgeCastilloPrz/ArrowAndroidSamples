package com.github.jorgecastillo.kotlinandroid.free.algebra

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.free.Free
import arrow.free.foldMap
import arrow.free.instances.FreeMonadInstance
import arrow.higherkind
import arrow.typeclasses.Monad
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.karumi.marvelapiclient.model.CharacterDto

fun <A> Kind<HeroesAlgebra.F, A>.fix(): HeroesAlgebra<A> = this as HeroesAlgebra<A>
typealias FreeHeroesAlgebra<A> = Free<HeroesAlgebra.F, A>


/**
 * Algebra for Hero data sources. Algebras are defined by a sealed class (ADT) with a limited
 * amount of implementations reflecting the operations available.
 */
sealed class HeroesAlgebra<A> : Kind<HeroesAlgebra.F, A> {
  class F private constructor()

  object GetAll : HeroesAlgebra<List<CharacterDto>>()
  class GetSingle(val heroId: String) : HeroesAlgebra<CharacterDto>()
  class HandlePresentationEffects(val result: Either<CharacterError, List<CharacterDto>>) : HeroesAlgebra<Unit>()
  class Attempt<A>(val fa: FreeHeroesAlgebra<A>) : HeroesAlgebra<Either<Throwable, A>>()

  /**
   * We must lift to the Free context all the operation blocks defined on the algebra and compose
   * our program logic based on those.
   */
  companion object : FreeMonadInstance<F> {
    fun getAllHeroes(): FreeHeroesAlgebra<List<CharacterDto>> =
        Free.liftF(HeroesAlgebra.GetAll)

    fun getSingleHero(heroId: String): FreeHeroesAlgebra<CharacterDto> =
        Free.liftF(HeroesAlgebra.GetSingle(heroId))

    fun handlePresentationEffects(result: Either<CharacterError, List<CharacterDto>>): FreeHeroesAlgebra<Unit> =
        Free.liftF(HeroesAlgebra.HandlePresentationEffects(result))

    fun <A> attempt(fa: FreeHeroesAlgebra<A>): FreeHeroesAlgebra<Either<Throwable, A>> =
        Free.liftF(HeroesAlgebra.Attempt(fa))
  }
}

inline fun <reified F> Free<HeroesAlgebra.F, List<CharacterDto>>.run(
    interpreter: FunctionK<HeroesAlgebra.F, F>, MF: Monad<F>): Kind<F, List<CharacterDto>> =
    this.foldMap(interpreter, MF)
