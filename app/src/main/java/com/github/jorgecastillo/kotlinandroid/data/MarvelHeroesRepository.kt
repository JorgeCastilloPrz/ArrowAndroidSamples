package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.datasource.HeroesDataSource

class MarvelHeroesRepository(val dataSources: List<HeroesDataSource>) : HeroesRepository {

  override fun getHeroes() = dataSources[0].getAll()
}
