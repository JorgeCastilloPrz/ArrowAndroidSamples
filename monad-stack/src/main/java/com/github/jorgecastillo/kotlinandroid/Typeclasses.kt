package com.github.jorgecastillo.kotlinandroid

import kategory.Eq

val userEq: Eq<User> = Eq({ user1, user2 -> user1.name == user2.name })

fun eqTest() {
  userEq.eqv(User("Jorge"), User("Paco"))
}
