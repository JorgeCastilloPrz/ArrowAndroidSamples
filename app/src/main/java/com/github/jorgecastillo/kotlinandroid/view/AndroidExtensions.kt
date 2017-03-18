package com.github.jorgecastillo.kotlinandroid.view

import android.content.Context
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.NonCancellable

/**
 * extension property to retrieve the root job from any context coroutine scope.
 */
val Context.contextJob: Job
  get() = (this as? JobHolder)?.job ?: NonCancellable
