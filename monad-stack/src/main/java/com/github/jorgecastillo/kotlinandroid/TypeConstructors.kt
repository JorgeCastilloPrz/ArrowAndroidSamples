package com.github.jorgecastillo.kotlinandroid

import kategory.*

val a : List<Int> = listOf()
val aS : List<String> = listOf()
val userList: List<User> = listOf()

val userOption: Option<User> = Option.None // arity 1
val eitherIntUser: Either<Int, User> = Either.Right(User("Jorge")) // arity 2
val kleisli: Kleisli<User, String, Int>? = null // arity 3

data class User(val name: String)

// List<Int>: * -> *
// Either<Int, User>: (*, *) -> *, * -> * -> *
// (*, *, *) -> *
//
