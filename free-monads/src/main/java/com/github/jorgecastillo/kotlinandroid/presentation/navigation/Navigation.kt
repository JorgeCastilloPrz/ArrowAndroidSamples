package com.github.jorgecastillo.kotlinandroid.presentation.navigation

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.functional.MonadControl
import com.github.jorgecastillo.kotlinandroid.view.SuperHeroDetailActivity
import kategory.map

class HeroDetailsPage {

  inline fun <reified F> go(heroId: String, C: MonadControl<F, GetHeroesContext, CharacterError>) =
      C.ask().map(C, {
        SuperHeroDetailActivity.launch(it.ctx, heroId)
      })
}
