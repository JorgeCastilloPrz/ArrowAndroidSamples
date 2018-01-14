package com.github.jorgecastillo.kotlinandroid

import android.content.res.Resources.NotFoundException
import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import kategory.Option
import kategory.Either
import kategory.Option.None
import kategory.Option.Some
import kategory.Tuple2
import java.util.logging.Level
import java.util.logging.Logger

// Pure functions

val cachedComputations: Map<Tuple2<Int, Int>, Int> = mapOf()

fun add(a: Int, b: Int): Int = a + b

fun product(a: Int, b: Int): Int = a * b

fun division(a: Int, b: Int): Int = a / b

fun triangleArea(b: Int, h: Int): Int = b * h / 2

fun squareArea(side: Int): Int = side * side

fun purityTest() {
  add(3, 2) // 5
  val five = 5
}

val presence = Some(SuperHero("IronMan"))
val absence = None

fun findHeroByName(heroName: String): Option<SuperHero> {
  return if (heroName == "Goku") {
    Option(SuperHero("Goku"))
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
