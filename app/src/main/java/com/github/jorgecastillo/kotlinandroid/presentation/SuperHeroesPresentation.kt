package com.github.jorgecastillo.kotlinandroid.presentation

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run

/**
 * Suspend function that will always run on a background thread using the CommonPool (ForkJoinPool).
 */
suspend fun getSuperHeroes() = run(CommonPool) {

}
