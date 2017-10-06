package com.github.jorgecastillo.kotlinandroid.functional

import kategory.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Basic future implementation to achieve asynchronicity based on Kotlin coroutines.
 */
@higherkind
@deriving(Functor::class, Applicative::class, Monad::class)
class Future<T> : FutureKind<T> {

    private val deferred: Deferred<T>

    private constructor(deferred: Deferred<T>) {
        this.deferred = deferred
    }

    constructor(f: () -> T) : this(async(CommonPool) { f() })

    fun <X> map(f: (T) -> X): Future<X> = Future(async(CommonPool) { f(deferred.await()) })

    fun <X> flatMap(f: (T) -> FutureKind<X>): Future<X> =
            Future(async(CommonPool) { f(deferred.await()).ev().deferred.await() })

    fun <B> ap(ff: FutureKind<(T) -> B>): Future<B> =
            zip(ff).map { it.b(it.a) }

    fun <B> zip(fb: FutureKind<B>): Future<Tuple2<T, B>> =
            flatMap { a -> fb.ev().map { b -> Tuple2(a, b) } }

    fun onComplete(f: (T) -> Unit) {
        launch(UI) {
            f(deferred.await())
        }
    }

    companion object {

        fun <A, B> tailRecM(a: A, f: (A) -> FutureKind<Either<A, B>>): Future<B> =
                f(a).ev().flatMap {
                    it.fold({ tailRecM(a, f).ev() }, { Future.pure(it) })
                }

        fun <A> pure(a: A): Future<A> =
                Future({ a })

    }
}
