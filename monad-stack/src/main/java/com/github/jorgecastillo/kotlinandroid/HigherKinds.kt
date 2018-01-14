package com.github.jorgecastillo.kotlinandroid

import kategory.HK
import kategory.Option
import kategory.Option.None
import kategory.Option.Some
import kategory.Try
import kategory.TryHK
import kategory.ev
import java.io.IOException

// Higher Kinded Types - Higher Kinds - HKs
//
// - High level of abstraction
// - Abstract over 2+ levels
// - Define programs in terms of F<A> (F and A are generic).
// - Enable Typeclasses.
// - DataTypes (Option, Either, Try, Validated...) defined in terms of F<A>, F<A, B>, F<A, B, C> ...

// - DataTypes are annotated as @higherkind
// - Arrow emulation: HK<F, A>
// - Try<A> === HK<Try, A>
// - Try<A> <-> HK<Try, A>

val tryHK : HK<TryHK, Option<User>> = TODO()

fun someTest(): Unit {
  // downcast from HK<Try, A> to Try<A>
  val concreteType = tryHK.ev()
  val mappedTry = concreteType.map { mappingFunction(it) }

  // the mappedTry type after mapping is still Try<Option<User>>
  mappedTry.flatMap { getUser(1234) }
}

fun mappingFunction(it: Option<User>): Option<User> = TODO()

fun getUser(userId: Long): Try<Option<User>> = Try({ fetchUserById(userId) })

@Throws(IOException::class)
fun fetchUserById(id: Long): Option<User> = if (id == 1234L) Some(User("Jorge")) else None
