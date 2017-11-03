package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import com.karumi.marvelapiclient.model.CharacterDto

fun mapApiCharacterToSuperHero(character: CharacterDto) = SuperHero(character.name)
