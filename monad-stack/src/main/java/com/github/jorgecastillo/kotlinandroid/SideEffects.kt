package com.github.jorgecastillo.kotlinandroid


private val cache = mapOf<Int, Int>()

private fun isFactor(num: Int, potential: Int) = num % potential == 0

private fun factorsOf(num: Int) = (1..num).filter { isFactor(num, it) }

fun sumFactors(): (Int) -> Int = { num ->
  val sum = cache.getOrElse(num, { factorsOf(num).reduce { a, b -> a + b } })
  println("Factors of $num = $sum")
  sum
}









fun test2(): Unit {
  // Calculator().sumFactors(10)
}











