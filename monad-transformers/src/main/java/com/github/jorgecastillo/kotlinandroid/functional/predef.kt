package com.github.jorgecastillo.kotlinandroid.functional

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import kategory.*

typealias Control<F> = MonadControl<F, GetHeroesContext, CharacterError>

inline fun <reified F> control(): Control<F> = monadControl()

typealias AsyncResultKind<A> = HK<AsyncResult.F, A>

fun <A> AsyncResultKind<A>.ev(): AsyncResult<A> =
    this as AsyncResult<A>

typealias Result<A> = Kleisli<EitherTF<Future.F, CharacterError>, GetHeroesContext, A>

//There should be a monad error instance for EitherT in kategory
class AsyncResult<A>(val value: Result<A>) : AsyncResultKind<A> {
  class F private constructor()
  companion object :
      AsyncResultMonadControl,
      GlobalInstance<MonadControl<AsyncResult.F, GetHeroesContext, CharacterError>>()

  fun run(ctx: GetHeroesContext): HK<EitherTF<Future.F, CharacterError>, A> = value.run(ctx)
}

interface MonadControl<F, D, E> :
    MonadError<F, E>,
    MonadReader<F, D>,
    Typeclass

inline fun <reified F, reified D, reified E> monadControl(): MonadControl<F, D, E> =
    instance(InstanceParametrizedType(MonadControl::class.java,
        listOf(F::class.java, D::class.java, E::class.java)))

interface AsyncResultMonadControl : MonadControl<AsyncResult.F, GetHeroesContext, CharacterError> {

  fun ETM(): EitherTMonadError<Future.F, CharacterError> =
      EitherTInstances(Future)

  fun KM(): KleisliInstances<EitherTF<Future.F, CharacterError>, GetHeroesContext, CharacterError> =
      KleisliInstances(ETM())

  override fun <A, B> map(fa: HK<AsyncResult.F, A>, f: (A) -> B): AsyncResult<B> =
      AsyncResult(KM().map(fa.ev().value, f))

  override fun <A, B> product(fa: HK<AsyncResult.F, A>,
      fb: HK<AsyncResult.F, B>): AsyncResult<Tuple2<A, B>> =
      AsyncResult(KM().product(fa.ev().value, fb.ev().value))

  override fun <A, B> flatMap(fa: HK<AsyncResult.F, A>,
      f: (A) -> HK<AsyncResult.F, B>): AsyncResult<B> =
      AsyncResult(KM().flatMap(fa.ev().value, f.andThen { it.ev().value }))

  override fun <A> handleErrorWith(fa: HK<AsyncResult.F, A>,
      f: (CharacterError) -> HK<AsyncResult.F, A>): AsyncResult<A> =
      AsyncResult(KM().handleErrorWith(fa.ev().value, f.andThen { it.ev().value }))

  override fun <A, B> tailRecM(a: A, f: (A) -> HK<AsyncResult.F, Either<A, B>>): AsyncResult<B> =
      AsyncResult(KM().tailRecM(a, f.andThen { it.ev().value }))

  override fun <A> raiseError(e: CharacterError): AsyncResult<A> =
      AsyncResult(KM().raiseError<A>(e))

  override fun <A> pure(a: A): AsyncResult<A> =
      AsyncResult(KM().pure(a))

  override fun ask(): AsyncResult<GetHeroesContext> =
      AsyncResult(KM().ask())

  override fun <A> local(f: (GetHeroesContext) -> GetHeroesContext,
      fa: HK<AsyncResult.F, A>): AsyncResult<A> =
      AsyncResult(KM().local(f, fa.ev().value))
}
