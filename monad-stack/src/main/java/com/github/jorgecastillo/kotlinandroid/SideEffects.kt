package com.github.jorgecastillo.kotlinandroid


class Calculator {

  private val cache = mapOf<Int, Int>()

  private fun isFactor(num: Int, potential: Int) = num % potential == 0

  private fun factorsOf(num: Int) = (1..num).filter { isFactor(num, it) }

  fun sumFactors(num: Int): Int {
    val sum = cache.getOrElse(num, { factorsOf(num).reduce { a, b -> a + b } })
    println("Factors of $num = $sum")
    return sum
  }
}

class ViewImpl {

  val calculator = Calculator()

  fun onResume() {
    val factorsOfTenSum = calculator.sumFactors(10)
    render(factorsOfTenSum)
  }

  private fun render(factorsOfTenSum: Int) {}
}


private val cache = mapOf<Int, Int>()

private fun isFactor(num: Int, potential: Int) = num % potential == 0

private fun factorsOf(num: Int) = (1..num).filter { isFactor(num, it) }

fun sumFactors(num: Int): Int = factorsOf(num).reduce { a, b -> a + b }

class ViewImpl2 {

  fun onResume() {
    val sum = cache.getOrElse(10, { sumFactors(10) })
    println("Factors of 10 = $sum")
    render(sum)
  }

  private fun render(factorsOfTenSum: Int) {}
}





fun test2(): Unit {
  val calculator = Calculator()
  val sum = calculator.sumFactors(10)
  ViewImpl2().onResume()
}











