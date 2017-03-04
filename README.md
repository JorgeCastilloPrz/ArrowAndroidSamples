Kotlin Android Architecture
===========================
[![Build Status](https://travis-ci.org/JorgeCastilloPrz/KotlinAndroidArchitecture.svg?branch=master)](https://travis-ci.org/JorgeCastilloPrz/KotlinAndroidArchitecture)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)

![Kotlin logo](./assets/medium_logo.png)

Kotlin playground to investigate some "kind-of-functional" approaches for architecture of Android apps.

**Kotlin version used:** 1.1

## Goals

### Error handling
Integrate errors and successful results alltogether using monads. The purpose is to benefit from functional structures to manage use case results by just thinking about the happy case. Exceptional cases can be managed at the end, and just when it's needed. Avoid using exceptions as those do not surpass context switch (thread boundaries) and they force you to use callbacks for async result propagation to the main thread.

### Testing Coroutines
JetBrains [introduced the concept of *Coroutines*](https://blog.jetbrains.com/kotlin/2017/03/kotlin-1-1/) on Kotlin 1.1 as an easy to use way to implement asynchronous tasks. JetBrains defines the concept like this: *"Coroutines are just much better threads: almost free to start and keep around, extremely cheap to suspend (suspension is for coroutines what blocking is for threads), very easy to compose and customize."*. So wouldn't be nice to give it a try?

### Alternative roads to Dependency Injection
From centuries ago, Android devs have been using complex frameworks like Dagger to achieve dependency injection. But **DI is just a concept not bound to any library. It's is all about passing collaborators to your classes from the outside world**. That means DI would also be to just add some setters or a constructor with some collaborator arguments to your class. The moment you do that, the class gets open to receive it's behaviors from the external world, and you are free to use a framework or something just created by you to bind the instance.

Also, we need to take into account that instances are always passed at runtime, but binding strategies can be solved at compile time, which is ideal in terms of fast error feedback. So investigating different ways to deal with DI seems like a very interesting way to find new paths to follow, as we might be able to do something different with Kotlin.

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://www.linkedin.com/in/jorgecastilloprz">
  <img alt="Add me to Linkedin" src="https://github.com/JorgeCastilloPrz/EasyMVP/blob/master/art/linkedin.png" />
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

