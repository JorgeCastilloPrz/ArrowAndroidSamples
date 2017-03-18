package com.github.jorgecastillo.kotlinandroid.view

import kotlinx.coroutines.experimental.Job

/**
 * Any JobHolder will automatically be forced to have a root job, which will be the root of the
 * job hierarchy for the current scope. This job is used to create a tree that corresponds to the
 * current activity, fragment or view implementing this.
 */
interface JobHolder {
  val job: Job?
}
