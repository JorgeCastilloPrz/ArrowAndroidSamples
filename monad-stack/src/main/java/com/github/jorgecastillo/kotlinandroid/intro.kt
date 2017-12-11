package com.github.jorgecastillo.kotlinandroid

import android.content.res.Resources.NotFoundException
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import kategory.Option
import kategory.Either

// Pure functions

fun add(a: Int, b: Int) = a + b

fun product(a: Int, b: Int) = a * b

fun division(a: Int, b: Int) = a / b

fun triangleArea(b: Int, h: Int) = b * h / 2

fun squareArea(side: Int) = side * side

fun findHeroByName(heroName: String): Option<SuperHero> {
  return if (heroName == "Goku") {
    Option.fromNullable(SuperHero("Goku"))
  } else {
    Option.None
  }
}

fun findHeroById(id: Long): Either<Exception, SuperHero> {
  return if (id == 10L) {
    Either.Right(SuperHero("Goku"))
  } else {
    Either.Left(NotFoundException())
  }
}








fun test() {
  add(1, 2)
  product(1, 2)
  division(2, 1)
  triangleArea(2, 2)
  squareArea(2)
  findHeroByName("Goku")
}
