package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero

class StubHeroesDataSource : HeroesDataSource {

  override fun getAll() = listOf(SuperHero("IronMan"), SuperHero("Spider-Man"),
      SuperHero("Batman"), SuperHero("Goku"), SuperHero("Vegeta"), SuperHero("SuperMan"),
      SuperHero("Ant-Man"), SuperHero("Krilin"), SuperHero("Super Mario"), SuperHero("Wolverine"),
      SuperHero("Massacre"), SuperHero("Jake Wharton"), SuperHero("Jesus Christ"),
      SuperHero("Donald Trump (villain)"))
}

