package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui

import android.content.Context
import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import com.github.jorgecastillo.kotlinandroid.tagless.runtime.ui.SuperHeroDetailActivity
import javax.inject.Inject

class Navigation<F> @Inject constructor(private val monadSuspend: MonadDefer<F>) {

  fun goToHeroDetailsPage(ctx: Context, heroId: String): Kind<F, Unit> =
      monadSuspend { SuperHeroDetailActivity.launch(ctx, heroId) }
}
