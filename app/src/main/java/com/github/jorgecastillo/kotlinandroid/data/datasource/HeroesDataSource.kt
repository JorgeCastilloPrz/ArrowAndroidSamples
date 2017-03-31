package com.github.jorgecastillo.kotlinandroid.data.datasource

import com.github.jorgecastillo.kotlinandroid.di.context.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.lang.Reader

interface HeroesDataSource {

  fun getAll(): Reader<GetHeroesContext, List<SuperHero>>
}
