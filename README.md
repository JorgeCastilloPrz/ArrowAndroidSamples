Kotlin Android Functional Architecture
======================================
[![Build Status](https://www.bitrise.io/app/10edb6301af99ad4/status.svg?token=V3W1vKcNZknWnWzEX1M7Hw&branch=master)](https://www.bitrise.io/app/10edb6301af99ad4)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.10-blue.svg)](http://kotlinlang.org/)

<img style="float:left;" height="100" src="assets/ic_launcher.png" width="100">
<img style="float:left;" height="100" src="https://avatars2.githubusercontent.com/u/29458023?v=4&amp;s=200" width="100">

Functional Programing Android architecture approaches using [Arrow](http://arrow-kt.io/)

# How to import it

The library being used here to fetch super heroes is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid) 
from [Karumi](https://github.com/Karumi). Since it's targeting the real [Marvel API](https://developer.marvel.com/), 
you will need to add `marvelPublicKey=your_public_key` and `marvelPrivateKey=your_private_key` to 
your home `gradle.properties` to be able to compile or run it. You can also add them by command line: 

`./gradlew detektCheck build -PmarvelPublicKey="\"whatever\"" -PmarvelPrivateKey="\"whatever\""`

# Main framework being used

To achieve **functional programing** over Kotlin I am using a library that we have been working on 
in the spanish dev community. It's called [Arrow](https://github.com/arrow-kt/arrow) and its first
official release is around the corner!

Big thanks to all the lib contributors which I am part of. [Here they are](https://github.com/arrow-kt/arrow/graphs/contributors).

# Strategies showcased on this repo

This project showcases a different functional architecture approach per module. These are the 
strategies / gradle modules available.

## Monad Stack
This module showcases an approach based on a **stack of monads** resolving all the concerns that an app could 
have, including Dependency Injection, error handling, asynchrony and threading, IO computations, and so on.
* [Read this detailed post](https://medium.com/@JorgeCastilloPr/kotlin-functional-programming-i-monad-stack-518d1bd8fbee) to use it as the best documentation out there for this approach. 

## Monad Transformers
This module presents a second iteration over the **monad-stack** approach, where we are simplifying things a lot by applying transformers on top of 
the IO monad to bind additional behaviors to it, so we can achieve the same behaviors we had with the monad stack but with less code. 
That is indeed a very common approach in Functional Programming. We are adding `AsyncResult` Monad 
which is going to cover all the needs we have: DI + Error Handling + Async.
[You probably want to look at this PR for more description details](https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/pull/3).

## Tagless-Final
Tagless-Final style is focused on never depending on concrete types like Option or Try, but use 
Higher Kinds to make our code depend on typeclass constrained behaviors, leaving the decision about 
which concrete types to use to the moment when we want to run the code.
[You will really want to look at this PR to have a very good and detailed description of what tagless-final is](https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/pull/2).

## Free Monads 
This FP style is very trendy. We are applying it over Android thanks to Arrow here, on the `free-monads` project module. It's highly recommended to take a look at [this PR](https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/pull/6) in order to understand the approach.
**Free Monads** is based on the idea of composing an **AST** (abstract syntax tree) of computations with  type `Free<S, A>`, where `S` is your algebra, which will never depend on implementation details but on abstractions defined by an algebra, which is an algebraic data type (ADT). We are defining it through a `sealed` class on this sample. 
Those ops can be combined as blocks to create more complex ones. Then, we need an **interpreter** which will be in charge to provide implementation details for the moment when the user decides to run the whole AST providing semantics to it and a `Monad` instance to resolve all effects / perform execution of effects in a controlled context. The user has the power of chosing which interpreter to use and which monad instance he wants to solve the problem. That enables testing, since we can easily remove our real side effects in the app at our testing environment by switching the interpreter by a fake one.

# Goals and rationale

## Modeling success and error cases
**Referential transparency** from a function perspective means that it should be clearly defining 
the work it's going to do based on its name, input parameter types and return type. It's a quite 
important concept on functional programing. We are quite used to model our error cases based on 
exceptions and callbacks in many cases, but exceptions do not surpass thread limits, and callbacks 
completely destroy referential transparency. 

To sum up, callbacks also break referential transparency, since a void return type on a method 
means that it could be applying side effects and we wouldn't really know. We don't have any clue 
about how the result is going to look like by looking at the function declaration.

The main goal I have for error handling here is to be able to integrate errors with successful 
results using Monads. The purpose is to benefit from functional structures to manage use case 
results in a very readable way, so all the concerns about what the function is going to return and 
how it's going to work for it.

This approach helps me to program in a simple imperative style about how to process asynchronous 
results that could be produced by an API query.

A result of type `Reader<Future<Either<Error, Success>>` is clearly defining a deferred computation 
(`Reader`) that will require some context to work. When we decide to run this block, we will run the 
 reader passing a context to it to provide all it's required dependencies, and it will run an 
 async task using a Future that will produce a result of either Error type or Success type on 
 completion. And that can be the return type for an use case, for example.
 
If we define the different types of expected `Errors` with a `sealed class` we should be able to 
close the hierarchy to limit the amount of expected errors inside our domain and do pattern matching 
to it (`when` statement) to achieve different behaviors depending on that.

## Alternative roads to Dependency Injection
From centuries ago, Android devs have been using complex frameworks like Dagger to achieve 
dependency injection. But **DI is just a concept not bound to any library. It's is all about 
passing collaborators to your classes from the outside world**. That means DI would also be to 
just add some setters or a constructor with some collaborator arguments to your class. 

The `Reader` or the `Kleisli` used on this project are just an alternative to achieve `DI` that 
is gonna play a good role in terms of psynergy along with the resting `Monads` being used.  

### No state

Trying to achieve purity on this repo, as much as I can. Purity means determinism. Functional 
behaviors are usually abstracted to functions, not to classes. I don't need to play with instances most 
of the time since all the operations and transformations over the data can always be pure and just wrapped 
in functions as first class citizens.

Attributions
------------
The library being used here to fetch super heroes is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid) 
from [Karumi](https://github.com/Karumi).

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://twitter.com/jorgecastillopr">
  <img alt="Twitter profile" src="https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/blob/master/assets/twitter_logo.png" />
</a>
<a href="https://medium.com/@jorgecastillopr">
  <img alt="Medium blog" src="https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/blob/master/assets/medium_blog_logo.png" />
</a>

License
-------

    Copyright 2017 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

