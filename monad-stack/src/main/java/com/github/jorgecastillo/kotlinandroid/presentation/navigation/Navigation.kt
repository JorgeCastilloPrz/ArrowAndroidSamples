package com.github.jorgecastillo.kotlinandroid.presentation.navigation

import arrow.data.ReaderApi
import arrow.data.map
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.view.SuperHeroDetailActivity

class HeroDetailsPage {
  fun go(heroId: String) = ReaderApi.ask<GetHeroesContext>().map({ (ctx) ->
    SuperHeroDetailActivity.launch(ctx, heroId)
  })
}
