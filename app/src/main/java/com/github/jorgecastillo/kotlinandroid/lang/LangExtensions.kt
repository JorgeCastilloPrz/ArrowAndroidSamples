package com.github.jorgecastillo.kotlinandroid.lang

import org.funktionale.either.Disjunction
import org.funktionale.option.Option

fun <A, B> Option<A>.zip(that: Option<B>): Option<Pair<A, B>> {
  return this.flatMap { a -> that.map { b -> Pair(a, b) } }
}

fun <L> L.left(): Disjunction<L, Nothing> {
  return Disjunction.Left<L, Nothing>(this)
}

fun <R> R.right(): Disjunction<Nothing, R> {
  return Disjunction.Right<Nothing, R>(this)
}
