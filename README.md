Kotlin Android Functional Architecture
======================================
[![Build Status](https://travis-ci.org/JorgeCastilloPrz/KotlinAndroidFunctional.svg?branch=master)](https://travis-ci.org/JorgeCastilloPrz/KotlinAndroidFunctional)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.1.3.2-blue.svg)](http://kotlinlang.org/)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)

![Kotlin logo](assets/kotlin_logo.png)

Kotlin playground to investigate some functional progamming approaches for architecture of Android apps.

# Main framework being used

To achieve **functional programing** over Kotlin I am using a library that we have been working on 
in the spanish dev community. It's called [kategory](https://github.com/kategory/kategory) and it's 
still in alpha stages, but the typeclasses and constructs available are working quite ok. The first 
release is around the corner!

Big thanks to @raulraja for his interest on making functional style feelings grow on the Android dev 
community, along with really good devs from Spain working on it. [Here is the list of contributors](https://github.com/kategory/kategory/graphs/contributors). 

# Strategies showcased on this repo

This project showcases a different functional architecture approach per module. These are the 
strategies / gradle modules available.

## Nested Monads
On this module, you will find a not very common approach using nested Monads like `Reader`, 
`Future`, or `Either` to construct the asynchronous result I want to get. This module showcases 
the first natural step that any OOP Android developer would probably implement on his first attempt 
to use Monads. You need some nested behaviors, so you move on and nest them. But in the end, It's 
not a quite common approach on FP, since we usually would end up combining all the properties from 
all those Monads into a single one much more powerful representing the result, just to simplify 
things.
## Monad Transformers
This module would be like **nested monads 2.0**. It presents a second iteration over the 
**nested-monads** module, where we are simplifying things a lot by applying transformers on top of 
Monads to bind additional behaviors to them, so we can achieve the same behaviors with less code. 
That is indeed a very common approach in Functional Programming. We are adding `AsyncResult` Monad 
which is going to cover all the needs we have: DI + Error Handling + Async.
[You probably want to look at this PR for more description details](https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/pull/3).
## Tagless-Final
Tagless-Final style is focused on never depending on concrete types like Option or Try, but use 
Higher Kinds to make our code depend on typeclass constrained behaviors, leaving the decision about 
which concrete types to use to the moment when we want to run the code.
[You will really want to look at this PR to have a very good and detailed description of what tagless-final is](https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/pull/2).
## Free Monads 
TBA. This is going to be another cool approach using FP, but still not ready.

# Goals and rationales

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

## Testing Coroutines
JetBrains [introduced the concept of *Coroutines*](https://blog.jetbrains.com/kotlin/2017/03/kotlin-1-1/) 
on Kotlin 1.1 as an easy to use way to implement asynchronous tasks. JetBrains defines the concept 
like this: *"Coroutines are just much better threads: almost free to start and keep around, 
extremely cheap to suspend (suspension is for coroutines what blocking is for threads), very easy 
to compose and customize."*. So wouldn't be nice to give it a try?

Let's keep an eye on [the official coroutines guide](https://github.com/Kotlin/kotlinx.coroutines/blob/master/coroutines-guide.md).

On this project, I am using coroutines for a simple `Future` implementation based on them.

## Alternative roads to Dependency Injection
From centuries ago, Android devs have been using complex frameworks like Dagger to achieve 
dependency injection. But **DI is just a concept not bound to any library. It's is all about 
passing collaborators to your classes from the outside world**. That means DI would also be to 
just add some setters or a constructor with some collaborator arguments to your class. 
The moment you do that, the class gets open to receive it's behaviors from the external world, 
and you are free to use a framework or something just created by you to bind the instance.

The `Reader` is just an alternative to achieve `DI` that is gonna play a good role in terms of 
psinergy with all the resting `Monads` being used. That's why I am choosing it.  

### Reader monad

The initial approach is going to be based on the Reader monad, which is just a way to defer the 
dependency resolution to the very moment when you want to run all the execution chain in the edge 
of your system (i.e: Activity / Framgnet / CustomView for Android). The idea is to concatenate 
`Reader` construction for the whole execution chain agnostically of how dependencies are going to 
be resolved, and provide the dependency resolution strategy when you need to run it. 

It's validated while you type, since the bindings are statically declared in the entity 
responsible of creating the mentioned strategies. That means you are not going to be able to 
compile if your dependency tree is not correctly prepared.

### No state

Trying to achieve purity on this repo, as much as I can. Purity means determinism, and functional 
behaviors are abstracted to functions, not to classes. I found out that I don't really need to play 
with instances most of the time since all the operations and transformations over the data can 
always be pure and just wrapped in functions as first class citizens.
 
The only dependencies I am passing in on the `Reader` are the `ApiClient` and the `MVP view` 
reference, so I can switch both at testing environments. Everything else can be exercised as it is 
in production.

Attributions
------------
The library being used here to fetch super heroes is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid) 
from [Karumi](https://github.com/Karumi). Of course it's targeting the real [Marvel API](https://developer.marvel.com/), 
so you will need to add `marvelPublicKey=your_public_key` and `marvelPrivateKey=your_private_key` to 
your home `gradle.properties` to be able to compile or run it. You can also add them by command line: 

`./gradlew detektCheck build -PmarvelPublicKey="\"whatever\"" -PmarvelPrivateKey="\"whatever\""`

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://www.linkedin.com/in/jorgecastilloprz">
  <img alt="Add me to Linkedin" src="https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/blob/master/assets/linkedin.png" />
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

