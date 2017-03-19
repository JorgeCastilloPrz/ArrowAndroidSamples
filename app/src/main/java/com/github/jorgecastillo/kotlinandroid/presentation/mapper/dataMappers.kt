package com.github.jorgecastillo.kotlinandroid.presentation.mapper

import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.github.jorgecastillo.kotlinandroid.view.viewmodel.SuperHeroViewModel

fun mapSuperHeroToViewModel(hero : SuperHero) = SuperHeroViewModel(hero.name)
