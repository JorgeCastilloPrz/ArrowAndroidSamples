package com.github.jorgecastillo.kotlinandroid.functional

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import kategory.*

typealias AsyncResultKind<A> = HK<AsyncResult.F, A>

fun <A> AsyncResultKind<A>.ev(): AsyncResult<A> =
    this as AsyncResult<A>

typealias Result<A> = Kleisli<EitherTKindPartial<Future.F, CharacterError>, GetHeroesContext, A>

//There should be a monad error instance for EitherT in kategory
class AsyncResult<A>(val value: Result<A>) : AsyncResultKind<A> {
  class F private constructor()

  fun <B> map(f: (A) -> B): AsyncResult<B> =
      map(this, f)

  fun <B> product(fb: AsyncResult<B>): AsyncResult<Tuple2<A, B>> =
      product(this, fb)

  fun <B> flatMap(f: (A) -> AsyncResult<B>): AsyncResult<B> =
      flatMap(this, f)

  fun handleErrorWith(f: (CharacterError) -> AsyncResult<A>): AsyncResult<A> =
      handleErrorWith(this, f)

  fun handleError(f: (CharacterError) -> A): AsyncResult<A> =
      handleError(this, f).ev()

  companion object :
      Monad<AsyncResult.F>,
      MonadError<AsyncResult.F, CharacterError>,
      MonadReader<AsyncResult.F, GetHeroesContext> {

    fun ETM(): MonadError<EitherTKindPartial<Future.F, CharacterError>, CharacterError> =
        EitherT.monadError(Future)

    fun KME(): KleisliMonadErrorInstance<EitherTKindPartial<Future.F, CharacterError>, GetHeroesContext, CharacterError> =
        Kleisli.monadError(ETM())

    fun KMR(): KleisliMonadReaderInstance<EitherTKindPartial<Future.F, CharacterError>, GetHeroesContext> =
        Kleisli.monadReader(ETM())

    override fun <A, B> map(fa: AsyncResultKind<A>, f: (A) -> B): AsyncResult<B> =
        AsyncResult(KME().map(fa.ev().value, f))

    override fun <A, B> product(fa: AsyncResultKind<A>,
        fb: AsyncResultKind<B>): AsyncResult<Tuple2<A, B>> =
        AsyncResult(KME().product(fa.ev().value, fb.ev().value))

    override fun <A, B> flatMap(fa: AsyncResultKind<A>,
        f: (A) -> AsyncResultKind<B>): AsyncResult<B> =
        AsyncResult(KME().flatMap(fa.ev().value, f.andThen { it.ev().value }))

    override fun <A> handleErrorWith(fa: AsyncResultKind<A>,
        f: (CharacterError) -> AsyncResultKind<A>): AsyncResult<A> =
        AsyncResult(KME().handleErrorWith(fa.ev().value, f.andThen { it.ev().value }))

    override fun <A, B> tailRecM(a: A, f: (A) -> AsyncResultKind<Either<A, B>>): AsyncResult<B> =
        AsyncResult(KME().tailRecM(a, f.andThen { it.ev().value }))

    override fun <A> raiseError(e: CharacterError): AsyncResult<A> =
        AsyncResult(KME().raiseError<A>(e))

    override fun <A> pure(a: A): AsyncResult<A> =
        AsyncResult(KME().pure(a))

    override fun ask(): AsyncResult<GetHeroesContext> =
        AsyncResult(KMR().ask())

    val unit: AsyncResult<Unit> = pure(Unit)

    override fun <A> local(
        f: (GetHeroesContext) -> GetHeroesContext, fa: AsyncResultKind<A>): AsyncResult<A> =
        AsyncResult(KMR().local(f, fa.ev().value))

    fun <B> bind(c: suspend MonadContinuation<AsyncResult.F, *>.() -> AsyncResult<B>): AsyncResult<B> =
        binding(c).ev()

  }

  fun run(ctx: GetHeroesContext): HK<EitherTKindPartial<Future.F, CharacterError>, A> = value.run(ctx)
}

