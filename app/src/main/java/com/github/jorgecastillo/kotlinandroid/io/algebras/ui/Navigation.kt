package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import com.github.jorgecastillo.kotlinandroid.io.runtime.ui.SuperHeroDetailActivity

fun <F> Runtime<F>.goToHeroDetailsPage(ctx: Context, heroId: String): Kind<F, Unit> = fx.concurrent {
    !effect { SuperHeroDetailActivity.launch(ctx, heroId) }
}
