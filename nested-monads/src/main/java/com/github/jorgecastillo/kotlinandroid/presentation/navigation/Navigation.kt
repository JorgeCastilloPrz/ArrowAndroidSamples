package com.github.jorgecastillo.kotlinandroid.presentation.navigation

import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.view.SuperHeroDetailActivity
import kategory.Id
import kategory.Reader
import kategory.functor

class HeroDetailsPage {
  fun go(heroId: String) = Reader.ask<GetHeroesContext>().map({ (ctx) ->
    SuperHeroDetailActivity.launch(ctx, heroId)
  }, Id.functor())
}
