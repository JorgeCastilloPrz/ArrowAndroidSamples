package com.github.jorgecastillo.kotlinandroid.functional

import arrow.HK
import arrow.HK2
import arrow.TC
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.data.EitherT
import arrow.data.EitherTKindPartial
import arrow.data.Kleisli
import arrow.data.KleisliMonadErrorInstanceImplicits
import arrow.data.KleisliMonadReaderInstanceImplicits
import arrow.data.monadError
import arrow.effects.AsyncContext
import arrow.effects.IO
import arrow.effects.IOHK
import arrow.effects.Proc
import arrow.instance
import arrow.instances.KleisliMonadErrorInstance
import arrow.mtl.MonadReader
import arrow.mtl.instances.KleisliMonadReaderInstance
import arrow.syntax.either.right
import arrow.typeclasses.MonadError
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroDetailsContext
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError

class AsyncResultHK private constructor()
typealias AsyncResultKind<D, A> = HK2<AsyncResultHK, D, A>
typealias AsyncResultKindPartial<D> = HK<AsyncResultHK, D>

@Suppress("UNCHECKED_CAST")
inline fun <D : SuperHeroesContext, A> AsyncResultKind<D, A>.ev(): AsyncResult<D, A> =
    this as AsyncResult<D, A>

typealias Result<D, A> = Kleisli<EitherTKindPartial<IOHK, CharacterError>, D, A>

class AsyncResult<D : SuperHeroesContext, A>(
    val value: Result<D, A>) : AsyncResultKind<D, A> {

  fun run(ctx: D): HK<EitherTKindPartial<IOHK, CharacterError>, A> = value.run(ctx)

  companion object {
    inline operator fun <reified D : SuperHeroesContext> invoke(): MonadControl<AsyncResultKindPartial<D>, D, CharacterError> = monadControl()
  }
}

inline fun <reified F, reified D : SuperHeroesContext, E> monadControl(): MonadControl<F, D, E> =
    if (D::class == GetHeroesContext::class) {
      AsyncResultMonadControl.Companion.getHeroesControl() as MonadControl<F, D, E>
    } else {
      AsyncResultMonadControl.Companion.getHeroDetailsControl() as MonadControl<F, D, E>
    }

interface MonadControl<F, D, E> :
    MonadError<F, E>,
    MonadReader<F, D>,
    AsyncContext<F>,
    TC

interface AsyncResultMonadControl<D : SuperHeroesContext> : MonadControl<AsyncResultKindPartial<D>, D, CharacterError> {

  companion object {
  }

  fun ETME(): MonadError<EitherTKindPartial<IOHK, CharacterError>, CharacterError> =
      EitherT.monadError()

  fun <D : SuperHeroesContext> KME(): KleisliMonadErrorInstance<EitherTKindPartial<IOHK, CharacterError>, D, CharacterError> =
      KleisliMonadErrorInstanceImplicits.instance(ETME())

  fun <D : SuperHeroesContext> KMR(): KleisliMonadReaderInstance<EitherTKindPartial<IOHK, CharacterError>, D> =
      KleisliMonadReaderInstanceImplicits.instance(ETME())

  override fun <A, B> map(fa: HK<AsyncResultKindPartial<D>, A>, f: (A) -> B): AsyncResult<D, B> {
    return AsyncResult(KME<D>().map(fa.ev().value, f))
  }

  override fun <A, B> product(fa: HK<AsyncResultKindPartial<D>, A>,
      fb: HK<AsyncResultKindPartial<D>, B>): AsyncResult<D, Tuple2<A, B>> {
    return AsyncResult(KME<D>().product(fa.ev().value, fb.ev().value))
  }

  override fun <A, B> flatMap(fa: HK<AsyncResultKindPartial<D>, A>,
      f: (A) -> HK<AsyncResultKindPartial<D>, B>): AsyncResult<D, B> {
    return AsyncResult(KME<D>().flatMap(fa.ev().value, f.andThen { it.ev().value }))
  }

  override fun <A> handleErrorWith(fa: HK<AsyncResultKindPartial<D>, A>,
      f: (CharacterError) -> HK<AsyncResultKindPartial<D>, A>): AsyncResult<D, A> {
    return AsyncResult(KME<D>().handleErrorWith(fa.ev().value, f.andThen { it.ev().value }))
  }

  override fun <A, B> tailRecM(a: A,
      f: (A) -> HK<AsyncResultKindPartial<D>, Either<A, B>>): AsyncResult<D, B> {
    return AsyncResult(KME<D>().tailRecM(a, f.andThen { it.ev().value }))
  }

  override fun <A> raiseError(e: CharacterError): AsyncResult<D, A> =
      AsyncResult(KME<D>().raiseError(e))

  override fun <A> pure(a: A): AsyncResult<D, A> =
      AsyncResult(KME<D>().pure(a))

  override fun ask(): AsyncResult<D, D> =
      AsyncResult(KMR<D>().ask())

  override fun <A> local(f: (D) -> D, fa: HK<AsyncResultKindPartial<D>, A>): AsyncResult<D, A> {
    return AsyncResult(KMR<D>().local(f, fa.ev().value))
  }

  override fun <A> runAsync(fa: Proc<A>): HK<AsyncResultKindPartial<D>, A> =
      AsyncResult<D, A>(Kleisli({ _: D -> EitherT(IO.runAsync(fa).map { it.right() }) }))
}

@instance(AsyncResultMonadControl::class)
interface GetHeroesControlAsyncResultMonadControlInstance : AsyncResultMonadControl<GetHeroesContext>

@instance(AsyncResultMonadControl::class)
interface GetHeroDetailsControlAsyncResultMonadControlInstance : AsyncResultMonadControl<GetHeroDetailsContext>
