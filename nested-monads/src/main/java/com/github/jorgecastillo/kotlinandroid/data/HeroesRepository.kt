package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroesFromAvengerComics

fun getHeroesWithCachePolicy() = fetchAllHeroes()

fun getHeroesFromAvengerComicsWithCachePolicy() = fetchHeroesFromAvengerComics()
