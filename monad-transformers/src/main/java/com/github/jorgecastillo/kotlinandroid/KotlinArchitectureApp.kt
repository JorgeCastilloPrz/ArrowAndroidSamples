package com.github.jorgecastillo.kotlinandroid

import android.app.Application
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.github.jorgecastillo.kotlinandroid.functional.MonadControl
import kategory.GlobalInstances
import kategory.InstanceParametrizedType

class KotlinArchitectureApp : Application() {

  override fun onCreate() {
    super.onCreate()
    initGlobalInstances()
  }

  private fun initGlobalInstances() {
    GlobalInstances.putIfAbsent(
        InstanceParametrizedType(
            MonadControl::class.java,
            listOf(AsyncResult.F::class.java, GetHeroesContext::class.java,
                CharacterError::class.java)
        ),
        AsyncResult
    )
  }
}
