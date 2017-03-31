package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource
import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.Reader

class StubHeroesDataSource : HeroesDataSource {

  override fun getAll() = Reader<GetHeroesContext, List<SuperHero>> {
    listOf(SuperHero("IronMan"), SuperHero("Spider-Man"),
        SuperHero("Batman"), SuperHero("Goku"), SuperHero("Vegeta"), SuperHero("SuperMan"))
  }
}

