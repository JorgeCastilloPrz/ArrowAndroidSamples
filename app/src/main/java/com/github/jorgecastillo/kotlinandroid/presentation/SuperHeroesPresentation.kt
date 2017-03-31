package com.github.jorgecastillo.kotlinandroid.presentation

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.lang.Reader
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel

interface SuperHeroesView {

  fun drawHeroes(heroes: List<SuperHeroViewModel>)

  fun showHeroesNotFoundError()

  fun showServerError()
}

fun getSuperHeroes(): Reader<GetHeroesContext, Unit> =
    Reader.ask<GetHeroesContext>().flatMap { ctx ->
      ctx.getSuperHeroesInteractor.getSuperHeroes().map {
        if (it.isEmpty()) ctx.view.showHeroesNotFoundError()
        else ctx.view.drawHeroes(it.map { SuperHeroViewModel(it.name) })
      }
    }
