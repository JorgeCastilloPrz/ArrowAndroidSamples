package com.github.jorgecastillo.kotlinandroid.functional

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import kategory.*

typealias AsyncResultKind<A> = HK<AsyncResult.F, A>

fun <A> AsyncResultKind<A>.ev(): AsyncResult<A> =
        this as AsyncResult<A>

typealias Result<A> = Kleisli<EitherTF<Future.F, CharacterError>, GetHeroesContext, A>

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

        fun ETM(): EitherTMonadError<Future.F, CharacterError> =
                EitherTInstances(Future)

        fun KM(): KleisliInstances<EitherTF<Future.F, CharacterError>, GetHeroesContext, CharacterError> =
                KleisliInstances(ETM())

        override fun <A, B> map(fa: AsyncResultKind<A>, f: (A) -> B): AsyncResult<B> =
                AsyncResult(KM().map(fa.ev().value, f))

        override fun <A, B> product(fa: AsyncResultKind<A>,
                                    fb: AsyncResultKind<B>): AsyncResult<Tuple2<A, B>> =
                AsyncResult(KM().product(fa.ev().value, fb.ev().value))

        override fun <A, B> flatMap(fa: AsyncResultKind<A>,
                                    f: (A) -> AsyncResultKind<B>): AsyncResult<B> =
                AsyncResult(KM().flatMap(fa.ev().value, f.andThen { it.ev().value }))

        override fun <A> handleErrorWith(fa: AsyncResultKind<A>,
                                         f: (CharacterError) -> AsyncResultKind<A>): AsyncResult<A> =
                AsyncResult(KM().handleErrorWith(fa.ev().value, f.andThen { it.ev().value }))

        override fun <A, B> tailRecM(a: A, f: (A) -> AsyncResultKind<Either<A, B>>): AsyncResult<B> =
                AsyncResult(KM().tailRecM(a, f.andThen { it.ev().value }))

        override fun <A> raiseError(e: CharacterError): AsyncResult<A> =
                AsyncResult(KM().raiseError<A>(e))

        override fun <A> pure(a: A): AsyncResult<A> =
                AsyncResult(KM().pure(a))

        override fun ask(): AsyncResult<GetHeroesContext> =
                AsyncResult(KM().ask())

        val unit: AsyncResult<Unit> = pure(Unit)

        override fun <A> local(
                f: (GetHeroesContext) -> GetHeroesContext, fa: AsyncResultKind<A>): AsyncResult<A> =
                AsyncResult(KM().local(f, fa.ev().value))

        fun <B> bind(c: suspend MonadContinuation<AsyncResult.F, *>.() -> AsyncResult<B>): AsyncResult<B> =
                binding(c).ev()

    }

    fun run(ctx: GetHeroesContext): HK<EitherTF<Future.F, CharacterError>, A> = value.run(ctx)
}

