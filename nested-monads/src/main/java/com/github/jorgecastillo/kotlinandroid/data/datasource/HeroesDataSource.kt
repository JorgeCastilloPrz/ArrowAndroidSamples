package com.github.jorgecastillo.kotlinandroid.data.datasource

import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero

interface HeroesDataSource {

  fun getAll(): List<SuperHero>
}
