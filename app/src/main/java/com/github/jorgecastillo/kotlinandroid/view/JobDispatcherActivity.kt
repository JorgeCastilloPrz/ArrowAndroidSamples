package com.github.jorgecastillo.kotlinandroid.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.experimental.Job

/**
 * This activity links its lifecycle to a job that will be the root of the coroutine tree for the
 * current scope. All the following jobs executed will have this one as their ancestor. When this
 * job gets cancelled, the cancel event will be propagated to all the jobs in the tree.
 */
open class JobDispatcherActivity : AppCompatActivity(), JobHolder {

  override var job: Job? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    job = Job()
  }

  override fun onDestroy() {
    super.onDestroy()
    job?.cancel()
  }
}
