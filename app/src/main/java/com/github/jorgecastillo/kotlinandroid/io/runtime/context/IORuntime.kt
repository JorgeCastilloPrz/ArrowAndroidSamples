package com.github.jorgecastillo.kotlinandroid.io.runtime.context

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.Concurrent
import kotlinx.coroutines.CoroutineDispatcher

/**
 * This context contains the program dependencies. It can potentially work over any data type F that
 * supports concurrency, or in other words, any data type F that there's an instance of concurrent
 * Fx for.
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class Runtime<F>(
        concurrent: Concurrent<F>,
        val mainDispatcher: CoroutineDispatcher,
        val bgDispatcher: CoroutineDispatcher
) : Concurrent<F> by concurrent

fun IO.Companion.runtime(ctx: RuntimeContext) =
        object : Runtime<ForIO>(IO.concurrent(), ctx.mainDispatcher, ctx.bgDispatcher) {}

data class RuntimeContext(
        val bgDispatcher: CoroutineDispatcher,
        val mainDispatcher: CoroutineDispatcher
)
