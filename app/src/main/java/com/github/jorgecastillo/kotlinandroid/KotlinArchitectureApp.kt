package com.github.jorgecastillo.kotlinandroid

import android.app.Application
import kategory.Id

class KotlinArchitectureApp : Application() {

  override fun onCreate() {
    super.onCreate()
    initGlobalInstances()
  }

  private fun initGlobalInstances() {
    val id = Id(1)
  }
}
