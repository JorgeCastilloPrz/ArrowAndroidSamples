package com.github.jorgecastillo.kotlinandroid.functional

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import kategory.*

typealias Control<F> = MonadControl<F, GetHeroesContext, CharacterError>

inline fun <reified F> control(): Control<F> = monadControl()

typealias AsyncResultKind<A> = HK<AsyncResult.F, A>

fun <A> AsyncResultKind<A>.ev(): AsyncResult<A> =
    this as AsyncResult<A>

typealias Result<A> = Kleisli<EitherTKindPartial<Future.F, CharacterError>, GetHeroesContext, A>

//There should be a monad error instance for EitherT in kategory
class AsyncResult<A>(val value: Result<A>) : AsyncResultKind<A> {
  class F private constructor()

  companion object : AsyncResultMonadControl

  fun run(ctx: GetHeroesContext): HK<EitherTKindPartial<Future.F, CharacterError>, A> = value.run(ctx)
}

interface MonadControl<F, D, E> :
    MonadError<F, E>,
    MonadReader<F, D>,
    Typeclass

inline fun <reified F, reified D, reified E> monadControl(): MonadControl<F, D, E> =
    instance(InstanceParametrizedType(MonadControl::class.java,
        listOf(F::class.java, D::class.java, E::class.java)))

interface AsyncResultMonadControl : MonadControl<AsyncResult.F, GetHeroesContext, CharacterError> {

  fun ETME(): MonadError<EitherTKindPartial<Future.F, CharacterError>, CharacterError> =
      EitherT.monadError(Future)

  fun KME(): KleisliMonadErrorInstance<EitherTKindPartial<Future.F, CharacterError>, GetHeroesContext, CharacterError> =
      Kleisli.monadError(ETME())

  fun KMR(): KleisliMonadReaderInstance<EitherTKindPartial<Future.F, CharacterError>, GetHeroesContext> =
      Kleisli.monadReader(ETME())

  override fun <A, B> map(fa: HK<AsyncResult.F, A>, f: (A) -> B): AsyncResult<B> =
      AsyncResult(KME().map(fa.ev().value, f))

  override fun <A, B> product(fa: HK<AsyncResult.F, A>,
      fb: HK<AsyncResult.F, B>): AsyncResult<Tuple2<A, B>> =
      AsyncResult(KME().product(fa.ev().value, fb.ev().value))

  override fun <A, B> flatMap(fa: HK<AsyncResult.F, A>,
      f: (A) -> HK<AsyncResult.F, B>): AsyncResult<B> =
      AsyncResult(KME().flatMap(fa.ev().value, f.andThen { it.ev().value }))

  override fun <A> handleErrorWith(fa: HK<AsyncResult.F, A>,
      f: (CharacterError) -> HK<AsyncResult.F, A>): AsyncResult<A> =
      AsyncResult(KME().handleErrorWith(fa.ev().value, f.andThen { it.ev().value }))

  override fun <A, B> tailRecM(a: A, f: (A) -> HK<AsyncResult.F, Either<A, B>>): AsyncResult<B> =
      AsyncResult(KME().tailRecM(a, f.andThen { it.ev().value }))

  override fun <A> raiseError(e: CharacterError): AsyncResult<A> =
      AsyncResult(KME().raiseError(e))

  override fun <A> pure(a: A): AsyncResult<A> =
      AsyncResult(KME().pure(a))

  override fun ask(): AsyncResult<GetHeroesContext> =
      AsyncResult(KMR().ask())

  override fun <A> local(f: (GetHeroesContext) -> GetHeroesContext,
      fa: HK<AsyncResult.F, A>): AsyncResult<A> =
      AsyncResult(KMR().local(f, fa.ev().value))
}
