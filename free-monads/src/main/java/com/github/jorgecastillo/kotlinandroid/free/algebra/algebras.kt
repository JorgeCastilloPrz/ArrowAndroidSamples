package com.github.jorgecastillo.kotlinandroid.free.algebra

import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.Either
import kategory.Free
import kategory.FreeMonadInstance
import kategory.FunctionK
import kategory.HK
import kategory.Monad
import kategory.flatMap
import kategory.foldMap
import kategory.higherkind
import kategory.monad

/**
 * Algebra for Hero data sources. Algebras are defined by a sealed class (ADT) with a limited amount of implementations reflecting the operations available.
 */
@higherkind sealed class HeroesAlgebra<A> : HeroesAlgebraKind<A> {

  class GetAll : HeroesAlgebra<Either<CharacterError, List<CharacterDto>>>()
  class GetSingle(val heroId: String) : HeroesAlgebra<Either<CharacterError, List<CharacterDto>>>()
  class HandlePresentationEffects(val result: Either<CharacterError, List<CharacterDto>>) : HeroesAlgebra<Unit>()
  companion object : FreeMonadInstance<HeroesAlgebraHK>
}

typealias FreeHeroesAlgebra<A> = Free<HeroesAlgebraHK, A>

inline fun <reified F> Free<HeroesAlgebraHK, List<CharacterDto>>.run(
    interpreter: FunctionK<HeroesAlgebraHK, F>, MF: Monad<F> = monad()): HK<F, List<CharacterDto>> =
    this.foldMap(interpreter, MF)

/**
 * Module definition (Data Source methods). Here we lift to the Free context all the operation blocks defined on the algebra.
 */
fun getAllHeroes(): FreeHeroesAlgebra<Either<CharacterError, List<CharacterDto>>> =
    Free.liftF(HeroesAlgebra.GetAll())

fun getSingleHero(heroId: String): FreeHeroesAlgebra<Either<CharacterError, List<CharacterDto>>> =
    Free.liftF(HeroesAlgebra.GetSingle(heroId))

fun fetchAndDrawHeroes(result: Either<CharacterError, List<CharacterDto>>): FreeHeroesAlgebra<Unit> =
    getAllHeroes().flatMap { Free.liftF(HeroesAlgebra.HandlePresentationEffects(result)) }
