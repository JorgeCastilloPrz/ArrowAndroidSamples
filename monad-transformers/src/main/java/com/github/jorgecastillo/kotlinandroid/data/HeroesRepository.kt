package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchAllHeroes
import com.github.jorgecastillo.kotlinandroid.data.datasource.remote.fetchHeroesFromAvengerComics
import com.karumi.marvelapiclient.model.CharacterDto
import kategory.HK

inline fun <reified F> getHeroesWithCachePolicy(): HK<F, List<CharacterDto>> =
    fetchAllHeroes()

inline fun <reified F> getHeroesFromAvengerComicsWithCachePolicy(): HK<F, List<CharacterDto>> =
    fetchHeroesFromAvengerComics()
