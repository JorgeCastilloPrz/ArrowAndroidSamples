package com.github.jorgecastillo.kotlinandroid.lang

import org.funktionale.composition.compose

/**
 * Basic implementation of the Reader monad. Provides an "implicit" context (configuration) for
 * function execution. Intended to provide Dependency Injection.
 */
open class Reader<C : ReaderContext, A>(val rd: (C) -> A) {

  fun <B> map(f: (A) -> B): Reader<C, B> = Reader(f compose rd)

  fun <B> flatMap(f: (A) -> Reader<C, B>): Reader<C, B> = Reader { c -> f(rd(c)).rd(c) }

  fun run(c: C) = rd(c)
}
