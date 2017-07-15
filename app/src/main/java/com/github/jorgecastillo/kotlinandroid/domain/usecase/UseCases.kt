package com.github.jorgecastillo.kotlinandroid.domain.usecase

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import kategory.Reader

fun getHeroes() = Reader.ask<GetHeroesContext>().flatMap {
  it.heroesRepository.getHeroes()
}

fun getHeroesFromAvengerComics() = Reader.ask<GetHeroesContext>().flatMap {
  it.heroesRepository.getHeroesFromAvengerComics()
}
