package com.github.jorgecastillo.kotlinandroid

import kategory.andThen

private val cache = mapOf<Int, Int>()

private fun isFactor(num: Int, potential: Int) = num % potential == 0

private fun factorsOf(num: Int) = (1..num).filter { isFactor(num, it) }

fun sumFactors(logger: Logger, cache: ComputationCache): (Int) -> Int = { num: Int ->
  val sum = cache.getOrElse(num, { factorsOf(num).reduce { a, b -> a + b } })
  logger.log("Factors of $num = $sum")
  sum
}

class ComputationCache {

  fun getOrElse(key: Int, defaultValue: () -> Int): Int = defaultValue()
}

class Logger {

  fun log(message: String): Unit {

  }
}


/*
class Presenter(private val calculator: Calculator) {

  fun onViewRestored(view: View) {
    val deferredComputation = calculator.sumFactors()
    deferredComputation.andThen { doOtherStuff() }.invoke(10)
  }

  fun doOtherStuff() {
  }

  interface View
}
*/

/*

fun test2(): Unit {
  // Calculator().sumFactors(10)
  val pres = Presenter(Calculator())
  pres.onViewRestored()
  pres.doOtherStuff()
}
*/











