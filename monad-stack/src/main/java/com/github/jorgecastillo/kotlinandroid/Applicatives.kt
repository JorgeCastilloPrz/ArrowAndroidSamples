package com.github.jorgecastillo.kotlinandroid

import com.github.jorgecastillo.kotlinandroid.domain.model.CharacterError
import kategory.Either
import kategory.Eval
import kategory.Option
import kategory.Tuple2
import kategory.applicative
import kategory.ev
import kategory.map

// pure()

val intOption = Option.applicative().pure(1) // Option(1)
val jorgeOption = Option.applicative().pure(User("Jorge")) // Option(User("Jorge"))
val myEither: Either<CharacterError, Int> = Either.applicative<CharacterError>().pure(1)

// Shortcuts provided

val anotherIntOption = Option.pure(1)

// ap()

fun applicativeTest() {
    Option.applicative().ap(Option(1), Option({ n: Int -> n + 1 })) // Some(2)
    intOption.ap(Option({ n: Int -> n + 1 })) // Some(2)

    // map2()
    val printBoth = Option.applicative()
            .map2(Option(1), Option("x"), { z: Tuple2<Int, String> -> "${z.a}${z.b}" })

    printBoth.map {}

    // map2Eval()
    val lazilyPrintBoth = Option.applicative().map2Eval(
                    Option(1),
                    Eval.later { Option("x") },
                    { z: Tuple2<Int, String> ->  "${z.a}${z.b}" })

    lazilyPrintBoth.map {}
    val evaluatedLazyPrintBoth = lazilyPrintBoth.value()
    evaluatedLazyPrintBoth.map {  }
    val downcasted = evaluatedLazyPrintBoth.ev()
    downcasted.map {  }
}

