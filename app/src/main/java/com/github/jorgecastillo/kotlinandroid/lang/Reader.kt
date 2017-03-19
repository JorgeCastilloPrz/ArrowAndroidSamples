package com.github.jorgecastillo.kotlinandroid.lang

import org.funktionale.composition.compose

/**
 * Basic implementation of the Reader monad. Provides an "implicit" context (configuration) for
 * function execution. Intended to provide Dependency Injection.
 */
class Reader<A>(val rd: (ReaderContext) -> A) {

  fun <B> map(f: (A) -> B): Reader<B> = Reader(f compose rd)

  fun <B> flatMap(f: (A) -> Reader<B>): Reader<B> = Reader { c -> f(rd(c)).rd(c) }
}
