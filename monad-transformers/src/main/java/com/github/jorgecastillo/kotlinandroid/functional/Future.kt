package com.github.jorgecastillo.kotlinandroid.functional

import kategory.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

typealias FutureKind<A> = HK<Future.F, A>

fun <A> FutureKind<A>.ev(): Future<A> =
    this as Future<A>

/**
 * Basic future implementation to achieve asynchronicity based on Kotlin coroutines.
 */
class Future<T> : FutureKind<T> {

  class F private constructor()

  private val deferred: Deferred<T>

  private constructor(deferred: Deferred<T>) {
    this.deferred = deferred
  }

  constructor(f: () -> T) : this(async(CommonPool) { f() })

  fun <X> map(f: (T) -> X): Future<X> {
    return Future(async(CommonPool) { f(deferred.await()) })
  }

  fun <X> flatMap(f: (T) -> Future<X>): Future<X> {
    return Future(async(CommonPool) { f(deferred.await()).deferred.await() })
  }

  fun <B> zip(fb: Future<B>): Future<Tuple2<T, B>> =
      flatMap { a -> fb.map { b -> Tuple2(a, b) } }

  fun onComplete(f: (T) -> Unit) {
    launch(UI) {
      f(deferred.await())
    }
  }

  companion object : Monad<F> {
    override fun <A, B> flatMap(fa: FutureKind<A>, f: (A) -> FutureKind<B>): Future<B> =
        fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: FutureKind<A>, f: (A) -> B): HK<F, B> =
        fa.ev().map { f(it) }

    override fun <A, B> tailRecM(a: A, f: (A) -> FutureKind<Either<A, B>>): Future<B> =
        flatMap(f(a).ev()) {
          when (it) {
            is Either.Left -> tailRecM(a, f)
            is Either.Right -> pure(it.b)
          }
        }

    override fun <A> pure(a: A): Future<A> =
        Future({ a })

    override fun <A, B> product(fa: FutureKind<A>, fb: FutureKind<B>): Future<Tuple2<A, B>> =
        fa.ev().zip(fb.ev())
  }
}
