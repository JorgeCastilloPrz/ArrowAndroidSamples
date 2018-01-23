package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui

import android.content.Context
import arrow.HK
import arrow.TC
import arrow.typeclass
import arrow.typeclasses.Applicative
import com.github.jorgecastillo.kotlinandroid.tagless.runtime.ui.SuperHeroDetailActivity

@typeclass
interface Navigation<F> : TC {

    fun applicative(): Applicative<F>

    fun goToHeroDetailsPage(ctx: Context, heroId: String): HK<F, Unit> =
            applicative().pure(SuperHeroDetailActivity.launch(ctx, heroId))

}