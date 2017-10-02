package com.github.jorgecastillo.kotlinandroid.presentation.navigation

import com.github.jorgecastillo.kotlinandroid.functional.Control
import com.github.jorgecastillo.kotlinandroid.functional.monadControl
import com.github.jorgecastillo.kotlinandroid.view.SuperHeroDetailActivity
import kategory.map

class HeroDetailsPage {

  inline fun <reified F> go(heroId: String, C: Control<F> = monadControl()) =
      C.ask().map(C, {
        SuperHeroDetailActivity.launch(it.ctx, heroId)
      })
}
