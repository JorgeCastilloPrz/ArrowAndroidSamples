package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.effects.IO
import arrow.effects.applicative
import arrow.effects.ev
import com.github.jorgecastillo.kotlinandroid.io.runtime.ui.SuperHeroDetailActivity

object Navigation {

  fun goToHeroDetailsPage(ctx: Context, heroId: String): IO<Unit> =
      IO.applicative().pure(SuperHeroDetailActivity.launch(ctx, heroId)).ev()
}
