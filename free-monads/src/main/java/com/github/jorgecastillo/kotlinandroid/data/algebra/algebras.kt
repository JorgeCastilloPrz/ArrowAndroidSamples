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
@higherkind sealed class HeroesDataSourceAlgebra<A> : HeroesDataSourceAlgebraKind<A> {

  class GetAll : HeroesDataSourceAlgebra<List<CharacterDto>>()
  class GetSingle(val heroId: String) : HeroesDataSourceAlgebra<List<CharacterDto>>()
  companion object : FreeMonadInstance<HeroesDataSourceAlgebraHK>
}

typealias FreeHeroesDataSource<A> = Free<HeroesDataSourceAlgebraHK, A>

inline fun <reified F> Free<HeroesDataSourceAlgebraHK, List<CharacterDto>>.runList(
    interpreter: FunctionK<HeroesDataSourceAlgebraHK, F>, MF: Monad<F> = monad()): HK<F, List<CharacterDto>> =
    this.foldMap(interpreter, MF)

inline fun <reified F> Free<HeroesDataSourceAlgebraHK, CharacterDto>.runSingle(
    interpreter: FunctionK<HeroesDataSourceAlgebraHK, F>, MF: Monad<F> = monad()): HK<F, CharacterDto> =
    this.foldMap(interpreter, MF)

/**
 * Module definition. Here we lift to the Free context all the operation blocks defined on the algebra.
 */
interface HeroesDataSource {

  fun getAll(): FreeHeroesDataSource<List<CharacterDto>> =
      Free.liftF(HeroesDataSourceAlgebra.GetAll())

  fun getSingle(heroId: String): FreeHeroesDataSource<List<CharacterDto>> =
      Free.liftF(HeroesDataSourceAlgebra.GetSingle(heroId))

  /**
   * More complex operation using the resting operation blocks already lifted to Free.
   */
  fun getAllFromAvengerComics(): FreeHeroesDataSource<List<CharacterDto>> = getAll().map {
    it.filter {
      it.comics.items.map { it.name }.filter {
        it.contains("Avenger", true)
      }.count() > 0
    }
  }
}
