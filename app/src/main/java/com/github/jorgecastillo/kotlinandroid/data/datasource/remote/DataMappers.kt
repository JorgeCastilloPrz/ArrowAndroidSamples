package com.github.jorgecastillo.kotlinandroid.presentation.mapper

import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.karumi.marvelapiclient.model.CharacterDto

fun mapApiCharacterToSuperHero(character: CharacterDto) = SuperHero(character.name)
