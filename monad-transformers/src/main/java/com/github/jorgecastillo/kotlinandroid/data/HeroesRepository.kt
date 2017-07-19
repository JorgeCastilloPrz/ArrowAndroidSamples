package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroesFromAvengerComics
import com.github.jorgecastillo.kotlinandroid.functional.AsyncResult
import com.karumi.marvelapiclient.model.CharacterDto

fun getHeroesWithCachePolicy(): AsyncResult<List<CharacterDto>> =
    fetchAllHeroes()

fun getHeroesFromAvengerComicsWithCachePolicy(): AsyncResult<List<CharacterDto>> =
    fetchHeroesFromAvengerComics()
