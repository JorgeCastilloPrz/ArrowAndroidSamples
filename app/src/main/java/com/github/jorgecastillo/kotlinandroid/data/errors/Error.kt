package com.github.jorgecastillo.kotlinandroid.data.errors

sealed class Error {
  class HeroesNotFound : Error()
  class HeroNotFound(val id: String) : Error()
}
