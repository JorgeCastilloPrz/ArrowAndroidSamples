package com.github.jorgecastillo.kotlinandroid.free.algebra

import arrow.HK
import arrow.core.Either
import arrow.core.FunctionK
import arrow.free.Free
import arrow.free.foldMap
import arrow.free.instances.FreeMonadInstance
import arrow.higherkind
import arrow.typeclasses.Monad
import arrow.typeclasses.monad
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.karumi.marvelapiclient.model.CharacterDto

/**
 * Algebra for Hero data sources. Algebras are defined by a sealed class (ADT) with a limited amount of implementations reflecting the operations available.
 */
@higherkind
sealed class HeroesAlgebra<A> : HeroesAlgebraKind<A> {
  object GetAll : HeroesAlgebra<List<CharacterDto>>()
  class GetSingle(val heroId: String) : HeroesAlgebra<CharacterDto>()
  class HandlePresentationEffects(val result: Either<CharacterError, List<CharacterDto>>) : HeroesAlgebra<Unit>()
  class Attempt<A>(val fa: FreeHeroesAlgebra<A>) : HeroesAlgebra<Either<Throwable, A>>()
  companion object : FreeMonadInstance<HeroesAlgebraHK>
}

typealias FreeHeroesAlgebra<A> = Free<HeroesAlgebraHK, A>

inline fun <reified F> Free<HeroesAlgebraHK, List<CharacterDto>>.run(
    interpreter: FunctionK<HeroesAlgebraHK, F>, MF: Monad<F> = monad()): HK<F, List<CharacterDto>> =
    this.foldMap(interpreter, MF)

/**
 * Module definition (Data Source methods). Here we lift to the Free context all the operation blocks defined on the algebra.
 */
fun getAllHeroes(): FreeHeroesAlgebra<List<CharacterDto>> =
    Free.liftF(HeroesAlgebra.GetAll)

fun getSingleHero(heroId: String): FreeHeroesAlgebra<CharacterDto> =
    Free.liftF(HeroesAlgebra.GetSingle(heroId))

fun handlePresentationEffects(result: Either<CharacterError, List<CharacterDto>>): FreeHeroesAlgebra<Unit> =
    Free.liftF(HeroesAlgebra.HandlePresentationEffects(result))

fun <A> attempt(fa: FreeHeroesAlgebra<A>): FreeHeroesAlgebra<Either<Throwable, A>> =
    Free.liftF(HeroesAlgebra.Attempt(fa))

