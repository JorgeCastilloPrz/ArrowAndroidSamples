package com.github.jorgecastillo.kotlinandroid

import kategory.*
import kategory.Option.Some

// List<T>
// Option<T>
// Either<L, R>
// Try<A>

val a : List<Int> = listOf()
val aS : List<String> = listOf()
val userList: List<User> = listOf()

// Arity
val jorge = User("Jorge Castillo")

val userOption: Option<User> = Some(jorge) // arity 1 - unary
val eitherIntUser: Either<Error, User> = Either.Right(jorge) // arity 2 - binary
val kleisli: Kleisli<User, String, Int>? = null // arity 3

val myBool : Boolean = false // arity 0 - nullary

data class User(val name: String)

// Type constructor theoretical notation: * -> *

// List<T>: * -> *
// Either<L, R>: (*, *) -> *, * -> * -> *

// Option<T> -> Option<User>


// Type constructors in Typeclasses

// Monad<F>
// F => Option<A>

// val optionMonad: Monad<Option>
