package com.github.jorgecastillo.kotlinandroid.functional

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult.Companion.KMR
import kategory.Applicative
import kategory.Either
import kategory.EitherT
import kategory.EitherTKindPartial
import kategory.Functor
import kategory.HK
import kategory.Kleisli
import kategory.KleisliMonadErrorInstance
import kategory.KleisliMonadErrorInstanceImplicits
import kategory.KleisliMonadReaderInstance
import kategory.KleisliMonadReaderInstanceImplicits
import kategory.Monad
import kategory.MonadError
import kategory.MonadReader
import kategory.andThen
import kategory.ev

typealias Result<D, A> = Kleisli<EitherTKindPartial<FutureHK, CharacterError>, D, A>

fun <D : SuperHeroesContext, A> Result<D, A>.asyncResult(): AsyncResult<D, A> = AsyncResult(this)

class AsyncResultHK private constructor()

typealias AsyncResultKind<D, A> = kategory.HK2<AsyncResultHK, D, A>

typealias AsyncResultKindPartial<D> = kategory.HK<AsyncResultHK, D>
@Suppress("UNCHECKED_CAST")
inline fun <D : SuperHeroesContext, A> AsyncResultKind<D, A>.ev(): AsyncResult<D, A> =
    this as AsyncResult<D, A>

class AsyncResult<D : SuperHeroesContext, A>(val value: Result<D, A>) : AsyncResultKind<D, A> {

  fun <B> map(f: (A) -> B): AsyncResult<D, B> =
      value.map(f, ETM()).asyncResult()

  fun <B> flatMap(f: (A) -> AsyncResultKind<D, B>): AsyncResult<D, B> =
      value.flatMap(f.andThen { it.ev().value }, ETM()).asyncResult()

  fun <B> ap(ff: AsyncResultKind<D, (A) -> B>): AsyncResult<D, B> = ff.ev().flatMap { this.ev().map(it) }

  fun handleErrorWith(f: (CharacterError) -> AsyncResult<D, A>): AsyncResult<D, A> =
      AsyncResult(KME<D>().handleErrorWith(value, f.andThen { it.ev().value }))

  fun run(d: D): EitherT<FutureHK, CharacterError, A> = value.run(d).ev()

  companion object {

    fun ETM(): MonadError<EitherTKindPartial<FutureHK, CharacterError>, CharacterError> =
        EitherT.monadError()

    fun <D> KME(): KleisliMonadErrorInstance<EitherTKindPartial<FutureHK, CharacterError>, D, CharacterError> =
        KleisliMonadErrorInstanceImplicits.instance(ETM())

    fun <D> KMR(): KleisliMonadReaderInstance<EitherTKindPartial<FutureHK, CharacterError>, D> =
        KleisliMonadReaderInstanceImplicits.instance(ETM())

    fun <D : SuperHeroesContext, A, B> tailRecM(a: A, f: (A) -> AsyncResultKind<D, Either<A, B>>): AsyncResult<D, B> =
        AsyncResult(KME<D>().tailRecM(a, f.andThen { it.ev().value }))

    fun <D : SuperHeroesContext, A> pure(a: A): AsyncResult<D, A> =
        AsyncResult(KME<D>().pure(a))

    fun <D : SuperHeroesContext> ask(): AsyncResult<D, D> =
        AsyncResult(KMR<D>().ask())

    fun <D : SuperHeroesContext> unit(): AsyncResult<D, Unit> = pure(Unit)

    fun <D : SuperHeroesContext, A> local(
        f: (D) -> D, fa: AsyncResultKind<D, A>): AsyncResult<D, A> =
        AsyncResult(KMR<D>().local(f, fa.ev().value))
  }
}

interface AsyncResultFunctorInstance<D : SuperHeroesContext> : Functor<AsyncResultKindPartial<D>> {

  override fun <A, B> map(fa: AsyncResultKind<D, A>, f: (A) -> B): AsyncResult<D, B> =
      fa.ev().map(f)

}

object AsyncResultFunctorInstanceImplicits {
  @JvmStatic
  fun <D : SuperHeroesContext> instance(): AsyncResultFunctorInstance<D> =
      object : AsyncResultFunctorInstance<D> {}
}

fun <D : SuperHeroesContext> AsyncResult.Companion.functor(): AsyncResultFunctorInstance<D> =
    AsyncResultFunctorInstanceImplicits.instance()

interface AsyncResultApplicativeInstance<D : SuperHeroesContext> : AsyncResultFunctorInstance<D>, Applicative<AsyncResultKindPartial<D>> {

  override fun <A, B> map(fa: AsyncResultKind<D, A>, f: (A) -> B): AsyncResult<D, B> =
      fa.ev().map(f)

  override fun <A, B> ap(fa: AsyncResultKind<D, A>, ff: HK<AsyncResultKindPartial<D>, (A) -> B>): AsyncResult<D, B> =
      fa.ev().ap(ff)

  override fun <A> pure(a: A): AsyncResult<D, A> =
      AsyncResult.pure(a)

}

object AsyncResultApplicativeInstanceImplicits {
  @JvmStatic
  fun <D : SuperHeroesContext> instance(): AsyncResultApplicativeInstance<D> =
      object : AsyncResultApplicativeInstance<D> {}
}

fun <D : SuperHeroesContext> AsyncResult.Companion.applicative(): AsyncResultApplicativeInstance<D> =
    AsyncResultApplicativeInstanceImplicits.instance()

interface AsyncResultMonadInstance<D : SuperHeroesContext> : AsyncResultApplicativeInstance<D>, Monad<AsyncResultKindPartial<D>> {

  override fun <A, B> flatMap(fa: AsyncResultKind<D, A>, f: (A) -> AsyncResultKind<D, B>): AsyncResultKind<D, B> =
      fa.ev().flatMap(f)

  override fun <A, B> ap(fa: AsyncResultKind<D, A>, ff: HK<AsyncResultKindPartial<D>, (A) -> B>): AsyncResult<D, B> =
      fa.ev().ap(ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> AsyncResultKind<D, Either<A, B>>): AsyncResultKind<D, B> =
      AsyncResult.tailRecM(a, f)
}

object AsyncResultMonadInstanceImplicits {
  @JvmStatic
  fun <D : SuperHeroesContext> instance(): AsyncResultMonadInstance<D> =
      object : AsyncResultMonadInstance<D> {}
}

fun <D : SuperHeroesContext> AsyncResult.Companion.monad(): AsyncResultMonadInstance<D> =
    AsyncResultMonadInstanceImplicits.instance()

interface AsyncResultMonadErrorInstance<D : SuperHeroesContext> : AsyncResultMonadInstance<D>, MonadError<AsyncResultKindPartial<D>, CharacterError> {

  override fun <A> raiseError(e: CharacterError): AsyncResult<D, A> =
      AsyncResult(AsyncResult.KME<D>().raiseError(e))

  override fun <A> handleErrorWith(fa: AsyncResultKind<D, A>,
      f: (CharacterError) -> AsyncResultKind<D, A>): AsyncResult<D, A> =
      fa.ev().handleErrorWith(f.andThen { it.ev() })

}

object AsyncResultMonadErrorInstanceImplicits {
  @JvmStatic
  fun <D : SuperHeroesContext> instance(): AsyncResultMonadErrorInstance<D> =
      object : AsyncResultMonadErrorInstance<D> {}
}

fun <D : SuperHeroesContext> AsyncResult.Companion.monadError(): AsyncResultMonadErrorInstance<D> =
    AsyncResultMonadErrorInstanceImplicits.instance()

interface AsyncResultMonadReaderInstance<D : SuperHeroesContext> : AsyncResultMonadErrorInstance<D>, MonadReader<AsyncResultKindPartial<D>, D> {

  override fun ask(): HK<AsyncResultKindPartial<D>, D> = AsyncResult(KMR<D>().ask())

  override fun <A> local(f: (D) -> D, fa: HK<AsyncResultKindPartial<D>, A>): HK<AsyncResultKindPartial<D>, A> =
      AsyncResult(KMR<D>().local(f, fa.ev().value))
}

object AsyncResultMonadReaderInstanceImplicits {
  @JvmStatic
  fun <D : SuperHeroesContext> instance(): AsyncResultMonadReaderInstance<D> =
      object : AsyncResultMonadReaderInstance<D> {}
}

fun <D : SuperHeroesContext> AsyncResult.Companion.monadReader(): AsyncResultMonadReaderInstance<D> =
    AsyncResultMonadReaderInstanceImplicits.instance()
