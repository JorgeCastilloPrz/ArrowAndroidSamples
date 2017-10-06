package com.github.jorgecastillo.kotlinandroid.data.algebra

import com.karumi.marvelapiclient.model.CharacterDto
import kategory.Free
import kategory.FreeMonadInstance
import kategory.FunctionK
import kategory.HK
import kategory.Monad
import kategory.foldMap
import kategory.higherkind
import kategory.map
import kategory.monad

/**
 * Algebra for Hero data sources. Algebras are defined by a sealed class (ADT) with a limited amount of implementations reflecting the operations available.
 */
@higherkind sealed class HeroesAlgebra<A> : HeroesAlgebraKind<A> {

  class GetAll : HeroesAlgebra<List<CharacterDto>>()
  class GetSingle(val heroId: String) : HeroesAlgebra<List<CharacterDto>>()
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
    Free.liftF(HeroesAlgebra.GetAll())

fun getSingleHero(heroId: String): FreeHeroesAlgebra<List<CharacterDto>> =
    Free.liftF(HeroesAlgebra.GetSingle(heroId))

/**
 * More complex operation using the resting operation blocks already lifted to Free.
 */
fun getAllFromAvengerComics(): FreeHeroesAlgebra<List<CharacterDto>> = getAllHeroes().map {
  it.filter {
    it.comics.items.map { it.name }.filter {
      it.contains("Avenger", true)
    }.count() > 0
  }
}
