package com.github.jorgecastillo.kotlinandroid

import com.github.jorgecastillo.kotlinandroid.domain.model.SuperHero
import kategory.Either
import kategory.EitherHK
import kategory.Eq
import kategory.Eval
import kategory.Foldable
import kategory.HK
import kategory.Monad
import kategory.OptionHK
import kategory.Semigroup
import kategory.Try
import kategory.TryHK

// Typeclasses

// - Monad<F>, Functor<F>, Applicative<F>, Foldable<F>, Traverse<F>, Monoid<A> ...

val tryMonad = object : Monad<TryHK> {
    override fun <A, B> flatMap(fa: HK<TryHK, A>, f: (A) -> HK<TryHK, B>): HK<TryHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A> pure(a: A): HK<TryHK, A> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<TryHK, Either<A, B>>): HK<TryHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

val optionMonad = object : Monad<OptionHK> {
    override fun <A, B> flatMap(fa: HK<OptionHK, A>, f: (A) -> HK<OptionHK, B>): HK<OptionHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A> pure(a: A): HK<OptionHK, A> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<OptionHK, Either<A, B>>): HK<OptionHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

val eitherMonad = object : Monad<EitherHK> {
    override fun <A, B> flatMap(fa: HK<EitherHK, A>, f: (A) -> HK<EitherHK, B>): HK<EitherHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A> pure(a: A): HK<EitherHK, A> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherHK, Either<A, B>>): HK<EitherHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

// Ad-Hoc polymorphism

fun fuse(sg: Semigroup<Character>, a: Character, b: Character): Character = sg.combine(a, b)

val dragonBallCharSemigroup = object : Semigroup<Character> {
    override fun combine(a: Character, b: Character): Character {
        return Character(a.name + b.name)
    }
}

fun randomCode() {
    val goku = Character("Goku")
    val vegeta = Character("Vegeta")
    // fuse(dragonBallCharSemigroup, goku, vegeta)

    // findHeroesBySaga("Avengers", HeroesService())
}

fun findHeroesBySaga(saga: String, heroesService: HeroesService): Foldable<SuperHero> =
        object : Foldable<SuperHero> {
            override fun <A, B> foldL(fa: HK<SuperHero, A>, b: B, f: (B, A) -> B): B {
                TODO("not implemented")
            }

            override fun <A, B> foldR(fa: HK<SuperHero, A>, lb: Eval<B>,
                    f: (A, Eval<B>) -> Eval<B>): Eval<B> {
                TODO("not implemented")
            }

        }



















val userEq: Eq<User> = Eq({ user1, user2 -> user1.name == user2.name })

fun eqTest() {
    userEq.eqv(User("Jorge"), User("Paco"))
}

// data types vs Typeclasses

// Monad<F>

val myOptionMonad : Monad<OptionHK> = object : Monad<OptionHK> {
    override fun <A, B> flatMap(fa: HK<OptionHK, A>, f: (A) -> HK<OptionHK, B>): HK<OptionHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A> pure(a: A): HK<OptionHK, A> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<OptionHK, Either<A, B>>): HK<OptionHK, B> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

// Tagless Final (MTL) -> Based on abstractions defined by typeclasses

// Related to DI



data class Character(val name: String)
data class BunchOfHeroes(val heroes: List<SuperHero>)
class HeroesService {
    fun fetchHeroes(saga: String): BunchOfHeroes = TODO()
}
